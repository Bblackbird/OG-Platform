/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.fudgemsg.FudgeContext;

import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.Pair;

/**
 * A {@link ViewComputationCacheSource} implementation based on {@link DefaultViewComputationCache} with
 * the given {@link IdentifierMap} and {@link BinaryDataStore} objects supplied by the given
 * {@link BinaryDataStoreFactory} instance.
 */
public class DefaultViewComputationCacheSource implements ViewComputationCacheSource {

  /**
   * Callback to receive notification of the releaseCaches message.
   */
  public static interface ReleaseCachesCallback {

    void onReleaseCaches(String viewName, long timestamp);

  }

  /**
   * Callback to locate missing data.
   */
  public static interface MissingValueLoader {

    byte[] findMissingValue(ViewComputationCacheKey cache, long identifier);

    Map<Long, byte[]> findMissingValues(ViewComputationCacheKey cache, Collection<Long> identifier);

  }

  private final IdentifierMap _identifierMap;
  private final FudgeContext _fudgeContext;

  private final ConcurrentMap<ViewComputationCacheKey, DefaultViewComputationCache> _cachesByKey = new ConcurrentHashMap<ViewComputationCacheKey, DefaultViewComputationCache>();
  private final Map<Pair<String, Long>, List<ViewComputationCacheKey>> _activeCaches = new HashMap<Pair<String, Long>, List<ViewComputationCacheKey>>();
  private final ReentrantLock _cacheManagementLock = new ReentrantLock();
  private final BinaryDataStoreFactory _privateDataStoreFactory;
  private final BinaryDataStoreFactory _sharedDataStoreFactory;

  private ReleaseCachesCallback _releaseCachesCallback;
  private MissingValueLoader _missingValueLoader;

  protected DefaultViewComputationCacheSource(final IdentifierMap identifierMap, final FudgeContext fudgeContext, final BinaryDataStoreFactory dataStoreFactory) {
    this(identifierMap, fudgeContext, dataStoreFactory, dataStoreFactory);
  }

  public DefaultViewComputationCacheSource(final IdentifierMap identifierMap, final FudgeContext fudgeContext, final BinaryDataStoreFactory privateDataStoreFactory,
      final BinaryDataStoreFactory sharedDataStoreFactory) {
    ArgumentChecker.notNull(identifierMap, "Identifier map");
    ArgumentChecker.notNull(fudgeContext, "Fudge context");
    ArgumentChecker.notNull(privateDataStoreFactory, "Private data store factory");
    ArgumentChecker.notNull(sharedDataStoreFactory, "Shared data store factory");
    _identifierMap = identifierMap;
    _fudgeContext = fudgeContext;
    _privateDataStoreFactory = privateDataStoreFactory;
    _sharedDataStoreFactory = sharedDataStoreFactory;
  }

  /**
   * Gets the identifierMap field.
   * @return the identifierMap
   */
  public IdentifierMap getIdentifierMap() {
    return _identifierMap;
  }

  /**
   * Gets the fudgeContext field.
   * @return the fudgeContext
   */
  public FudgeContext getFudgeContext() {
    return _fudgeContext;
  }

  @Override
  public ViewComputationCache cloneCache(String viewName, String calculationConfigurationName, long timestamp) {
    final ViewComputationCacheKey key = new ViewComputationCacheKey(viewName, calculationConfigurationName, timestamp);
    final DefaultViewComputationCache cache = _cachesByKey.get(key);
    final InMemoryIdentifierMap identifierMap = new InMemoryIdentifierMap();
    final InMemoryBinaryDataStore dataStore = new InMemoryBinaryDataStore();
    for (Pair<ValueSpecification, byte[]> value : cache) {
      dataStore.put(identifierMap.getIdentifier(value.getKey()), value.getValue());
    }
    return new DefaultViewComputationCache(identifierMap, dataStore, dataStore, getFudgeContext());
  }

  @Override
  public DefaultViewComputationCache getCache(String viewName, String calculationConfigurationName, long timestamp) {
    return getCache(new ViewComputationCacheKey(viewName, calculationConfigurationName, timestamp));
  }

  public DefaultViewComputationCache getCache(final ViewComputationCacheKey key) {
    DefaultViewComputationCache cache = findCache(key);
    if (cache == null) {
      cache = constructCache(key);
    }
    return cache;
  }

  protected DefaultViewComputationCache findCache(String viewName, String calculationConfigurationName, long timestamp) {
    return findCache(new ViewComputationCacheKey(viewName, calculationConfigurationName, timestamp));
  }

  protected DefaultViewComputationCache findCache(final ViewComputationCacheKey key) {
    return _cachesByKey.get(key);
  }

  protected DefaultViewComputationCache constructCache(final ViewComputationCacheKey key) {
    DefaultViewComputationCache cache = null;
    _cacheManagementLock.lock();
    try {
      // Have to double-check. Too expensive to construct otherwise.
      cache = _cachesByKey.get(key);
      if (cache == null) {
        final BinaryDataStore privateDataStore = _privateDataStoreFactory.createDataStore(key);
        final BinaryDataStore sharedDataStore = (_privateDataStoreFactory == _sharedDataStoreFactory) ? privateDataStore : _sharedDataStoreFactory.createDataStore(key);
        cache = createViewComputationCache(getIdentifierMap(), privateDataStore, sharedDataStore, getFudgeContext());
        _cachesByKey.put(key, cache);
        final Pair<String, Long> releaseKey = Pair.of(key.getViewName(), key.getSnapshotTimestamp());
        List<ViewComputationCacheKey> caches = _activeCaches.get(releaseKey);
        if (caches == null) {
          caches = new LinkedList<ViewComputationCacheKey>();
          _activeCaches.put(releaseKey, caches);
        }
        caches.add(key);
        final MissingValueLoader loader = getMissingValueLoader();
        if (loader != null) {
          cache.setMissingValueLoader(new DefaultViewComputationCache.MissingValueLoader() {

            @Override
            public byte[] findMissingValue(final long identifier) {
              return loader.findMissingValue(key, identifier);
            }

            @Override
            public Map<Long, byte[]> findMissingValues(Collection<Long> identifiers) {
              return loader.findMissingValues(key, identifiers);
            }

          });
        }
      }
    } finally {
      _cacheManagementLock.unlock();
    }
    return cache;
  }

  /**
   * Override this method if you need to create a different sub-class of {@link DefaultViewComputationCache}.
   * 
   * @param identifierMap the identifier map
   * @param privateDataStore the binary data store for private values
   * @param sharedDataStore the binary data store for shared values 
   * @param fudgeContext the Fudge context
   * @return a new {@link DefaultViewComputationCache} instance
   */
  protected DefaultViewComputationCache createViewComputationCache(final IdentifierMap identifierMap, final BinaryDataStore privateDataStore, final BinaryDataStore sharedDataStore,
      final FudgeContext fudgeContext) {
    return new DefaultViewComputationCache(identifierMap, privateDataStore, sharedDataStore, fudgeContext);
  }

  @Override
  public void releaseCaches(String viewName, long timestamp) {
    ArgumentChecker.notNull(viewName, "View name");
    final ReleaseCachesCallback callback = getReleaseCachesCallback();
    if (callback != null) {
      callback.onReleaseCaches(viewName, timestamp);
    }
    DefaultViewComputationCache[] caches;
    _cacheManagementLock.lock();
    try {
      final List<ViewComputationCacheKey> cacheKeys = _activeCaches.remove(Pair.of(viewName, timestamp));
      if (cacheKeys == null) {
        return;
      }
      caches = new DefaultViewComputationCache[cacheKeys.size()];
      int i = 0;
      for (ViewComputationCacheKey key : cacheKeys) {
        caches[i++] = _cachesByKey.remove(key);
      }
    } finally {
      _cacheManagementLock.unlock();
    }
    for (DefaultViewComputationCache cache : caches) {
      cache.delete();
    }
  }

  public void setReleaseCachesCallback(final ReleaseCachesCallback releaseCachesCallback) {
    _releaseCachesCallback = releaseCachesCallback;
  }

  public ReleaseCachesCallback getReleaseCachesCallback() {
    return _releaseCachesCallback;
  }

  public void setMissingValueLoader(final MissingValueLoader missingValueLoader) {
    _missingValueLoader = missingValueLoader;
  }

  public MissingValueLoader getMissingValueLoader() {
    return _missingValueLoader;
  }

}
