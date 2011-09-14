/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.core.historicaltimeseries.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.time.calendar.LocalDate;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ehcache.EHCacheUtils;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.tuple.ObjectsPair;
import com.opengamma.util.tuple.Pair;

/**
 * A cache decorating a {@code HistoricalTimeSeriesSource}.
 * <p>
 * The cache is implemented using {@code EHCache}.
 */
public class EHCachingHistoricalTimeSeriesSource implements HistoricalTimeSeriesSource {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(EHCachingHistoricalTimeSeriesSource.class);
  /**
   * The cache name.
   */
  private static final String CACHE_NAME = "HistoricalTimeSeriesCache";

  /**
   * The underlying source.
   */
  private final HistoricalTimeSeriesSource _underlying;
  /**
   * The cache.
   */
  private final Cache _cache;

  /**
   * Creates an instance.
   * 
   * @param underlying  the underlying source, not null
   * @param cacheManager  the cache manager, not null
   * @param maxElementsInMemory  cache configuration
   * @param memoryStoreEvictionPolicy  cache configuration
   * @param overflowToDisk  cache configuration
   * @param diskStorePath  cache configuration
   * @param eternal  cache configuration
   * @param timeToLiveSeconds  cache configuration
   * @param timeToIdleSeconds  cache configuration
   * @param diskPersistent  cache configuration
   * @param diskExpiryThreadIntervalSeconds  cache configuration
   * @param registeredEventListeners  cache configuration
   */
  public EHCachingHistoricalTimeSeriesSource(
      final HistoricalTimeSeriesSource underlying, final CacheManager cacheManager, final int maxElementsInMemory,
      final MemoryStoreEvictionPolicy memoryStoreEvictionPolicy, final boolean overflowToDisk, final String diskStorePath,
      final boolean eternal, final long timeToLiveSeconds, final long timeToIdleSeconds, final boolean diskPersistent,
      final long diskExpiryThreadIntervalSeconds, final RegisteredEventListeners registeredEventListeners) {
    ArgumentChecker.notNull(underlying, "underlying");
    ArgumentChecker.notNull(cacheManager, "cacheManager");
    _underlying = underlying;
    EHCacheUtils.addCache(cacheManager, CACHE_NAME, maxElementsInMemory, memoryStoreEvictionPolicy, overflowToDisk, diskStorePath,
        eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent, diskExpiryThreadIntervalSeconds, registeredEventListeners);
    _cache = EHCacheUtils.getCacheFromManager(cacheManager, CACHE_NAME);
  }

  /**
   * Creates an instance.
   * 
   * @param underlying  the underlying source, not null
   * @param cacheManager  the cache manager, not null
   */
  public EHCachingHistoricalTimeSeriesSource(HistoricalTimeSeriesSource underlying, CacheManager cacheManager) {
    ArgumentChecker.notNull(underlying, "underlying");
    ArgumentChecker.notNull(cacheManager, "Cache Manager");
    _underlying = underlying;
    EHCacheUtils.addCache(cacheManager, CACHE_NAME);
    _cache = EHCacheUtils.getCacheFromManager(cacheManager, CACHE_NAME);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the underlying source.
   * 
   * @return the underlying source, not null
   */
  public HistoricalTimeSeriesSource getUnderlying() {
    return _underlying;
  }

  /**
   * Gets the cache manager.
   * 
   * @return the cache manager, not null
   */
  public CacheManager getCacheManager() {
    return _cache.getCacheManager();
  }

  //-------------------------------------------------------------------------
  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    HistoricalTimeSeries hts = getFromCache(uniqueId);
    if (hts == null) {
      hts = _underlying.getHistoricalTimeSeries(uniqueId);
      if (hts != null) {
        s_logger.debug("Caching time-series {}", hts);
        _cache.put(new Element(uniqueId, hts));
      }
    }
    return hts;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      UniqueId uniqueId, LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
    HistoricalTimeSeries hts = getHistoricalTimeSeries(uniqueId);
    return getSubSeries(hts, start, includeStart, end, includeEnd);
  }

  //-------------------------------------------------------------------------
  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      ExternalIdBundle identifiers, String dataSource, String dataProvider, String dataField) {
    return getHistoricalTimeSeries(identifiers, (LocalDate) null, dataSource, dataProvider, dataField);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      ExternalIdBundle identifiers, LocalDate identifierValidityDate, String dataSource, String dataProvider, String dataField) {
    ArgumentChecker.notNull(identifiers, "identifiers");
    HistoricalTimeSeriesKey key = new HistoricalTimeSeriesKey(null, identifierValidityDate, identifiers, dataSource, dataProvider, dataField);
    HistoricalTimeSeries hts = getFromCache(key);
    if (hts == null) {
      hts = _underlying.getHistoricalTimeSeries(identifiers, identifierValidityDate, dataSource, dataProvider, dataField);
      if (hts != null) {
        s_logger.debug("Caching time-series {}", hts);
        _cache.put(new Element(key, hts.getUniqueId()));
        _cache.put(new Element(hts.getUniqueId(), hts));
      }
    }
    return hts;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      ExternalIdBundle identifiers, String dataSource, String dataProvider, String dataField, LocalDate start,
      boolean includeStart, LocalDate end, boolean includeEnd) {
    return getHistoricalTimeSeries(
        identifiers, (LocalDate) null, dataSource, dataProvider, dataField,
        start, includeStart, end, includeEnd);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      ExternalIdBundle identifiers, LocalDate currentDate, String dataSource, String dataProvider, String dataField,
      LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
    HistoricalTimeSeries tsPair = getHistoricalTimeSeries(identifiers, currentDate, dataSource, dataProvider, dataField);
    return getSubSeries(tsPair, start, includeStart, end, includeEnd);
  }

  //-------------------------------------------------------------------------
  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      String dataField, ExternalIdBundle identifierBundle, String resolutionKey) {
    return getHistoricalTimeSeries(dataField, identifierBundle, null, resolutionKey);
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      String dataField, ExternalIdBundle identifierBundle, LocalDate identifierValidityDate, String resolutionKey) {
    ArgumentChecker.notNull(dataField, "dataField");
    ArgumentChecker.notEmpty(identifierBundle, "identifierBundle");
    HistoricalTimeSeriesKey key = new HistoricalTimeSeriesKey(resolutionKey, identifierValidityDate, identifierBundle, null, null, dataField);
    HistoricalTimeSeries hts = getFromCache(key);
    if (hts == null) {
      hts = _underlying.getHistoricalTimeSeries(dataField, identifierBundle, identifierValidityDate, resolutionKey);
      if (hts != null) {
        s_logger.debug("Caching time-series {}", hts);
        _cache.put(new Element(key, hts.getUniqueId()));
        _cache.put(new Element(hts.getUniqueId(), hts));
      }
    }
    return hts;
  }

  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(
      String dataField, ExternalIdBundle identifierBundle, String resolutionKey, 
      LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
    return getHistoricalTimeSeries(dataField, identifierBundle, (LocalDate) null, resolutionKey, start, includeStart, end, includeEnd);
  }

  /*
   * PLAT-1589
   */
  private final class SubSeriesKey {
    private final LocalDate _start;
    private final boolean _includeStart;
    private final LocalDate _end;
    private final boolean _includeEnd;
    
    public SubSeriesKey(LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
      super();
      this._start = start;
      this._includeStart = includeStart;
      this._end = end;
      this._includeEnd = includeEnd;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _end.hashCode();
      result = prime * result + (_includeEnd ? 1231 : 1237);
      result = prime * result + (_includeStart ? 1231 : 1237);
      result = prime * result + _start.hashCode();
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      SubSeriesKey other = (SubSeriesKey) obj;
      if (_includeEnd != other._includeEnd) {
        return false;
      }
      if (_includeStart != other._includeStart) {
        return false;
      }
      if (!_end.equals(other._end)) {
        return false;
      }
      if (!_start.equals(other._start)) {
        return false;
      }
      return true;
    }
  }
  
  @Override
  public HistoricalTimeSeries getHistoricalTimeSeries(String dataField, ExternalIdBundle identifierBundle,
      LocalDate identifierValidityDate, String resolutionKey,
      LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
    
    HistoricalTimeSeriesKey seriesKey = new HistoricalTimeSeriesKey(resolutionKey, identifierValidityDate, identifierBundle, null, null, dataField);
    SubSeriesKey subseriesKey = new SubSeriesKey(start, includeStart, end, includeEnd);
    ObjectsPair<HistoricalTimeSeriesKey, SubSeriesKey> key = Pair.of(seriesKey, subseriesKey);
    Element element = _cache.get(key);
    if (element == null) {
      //TODO: if we have the full series cached computing a subseries could be faster
      //TODO: use the uid cache to make the underlying query easier?
      HistoricalTimeSeries sub = _underlying.getHistoricalTimeSeries(dataField, identifierBundle, identifierValidityDate, resolutionKey, start, includeStart, end, includeEnd);
      if (sub != null) {
        s_logger.debug("Caching sub time-series {}", sub);
        //TODO should probably split all these caches out
        element = new Element(key, sub);
        _cache.put(element);
      }
    }
    return element == null ? null : (HistoricalTimeSeries) element.getValue();
  }

  //-------------------------------------------------------------------------
  @Override
  public Map<ExternalIdBundle, HistoricalTimeSeries> getHistoricalTimeSeries(
      Set<ExternalIdBundle> identifierSet, String dataSource, String dataProvider, String dataField, LocalDate start,
      boolean includeStart, LocalDate end, boolean includeEnd) {
    ArgumentChecker.notNull(identifierSet, "identifierSet");
    Map<ExternalIdBundle, HistoricalTimeSeries> result = new HashMap<ExternalIdBundle, HistoricalTimeSeries>();
    Set<ExternalIdBundle> remainingIds = new HashSet<ExternalIdBundle>();
    // caching works individually but all misses can be passed to underlying as one request
    for (ExternalIdBundle identifiers : identifierSet) {
      HistoricalTimeSeriesKey key = new HistoricalTimeSeriesKey(null, null, identifiers, dataSource, dataProvider, dataField);
      HistoricalTimeSeries hts = getFromCache(key);
      if (hts != null) {
        hts = getSubSeries(hts, start, includeStart, end, includeEnd);
        result.put(identifiers, hts);
      } else {
        remainingIds.add(identifiers);
      }
    }
    if (remainingIds.size() > 0) {
      Map<ExternalIdBundle, HistoricalTimeSeries> remainingTsResults =
        _underlying.getHistoricalTimeSeries(remainingIds, dataSource, dataProvider, dataField, start, includeStart, end, includeEnd);
      for (Map.Entry<ExternalIdBundle, HistoricalTimeSeries> tsResult : remainingTsResults.entrySet()) {
        ExternalIdBundle identifiers = tsResult.getKey();
        HistoricalTimeSeries hts = tsResult.getValue();
        HistoricalTimeSeriesKey key = new HistoricalTimeSeriesKey(null, null, identifiers, dataSource, dataProvider, dataField);
        if (hts != null) {
          s_logger.debug("Caching time-series {}", hts);
          _cache.put(new Element(key, hts.getUniqueId()));
          _cache.put(new Element(hts.getUniqueId(), hts));
          hts = getSubSeries(hts, start, includeStart, end, includeEnd);
        }
        result.put(identifiers, hts);
      }
    }
    return result;
  }

  //-------------------------------------------------------------------------
  /**
   * Attempts to retrieve the time-series with the given key from the cache.
   * 
   * @param key  the key, not null
   * @return the time-series, null if no match
   */
  private HistoricalTimeSeries getFromCache(HistoricalTimeSeriesKey key) {
    Element element = _cache.get(key);
    if (element == null || element.getValue() instanceof UniqueId == false) {
      s_logger.debug("Cache miss on {}", key.getExternalIdBundle());
      return null;
    }
    s_logger.debug("Cache hit on {}", key.getExternalIdBundle());
    return getFromCache((UniqueId) element.getValue());
  }

  /**
   * Attempts to retrieve the time-series with the given unique identifier from the cache.
   * 
   * @param uniqueId  the unique identifier, not null
   * @return the time-series, null if no match
   */
  private HistoricalTimeSeries getFromCache(UniqueId uniqueId) {
    Element element = _cache.get(uniqueId);
    if (element == null || element.getValue() instanceof HistoricalTimeSeries == false) {
      s_logger.debug("Cache miss on {}", uniqueId);
      return null;
    }
    s_logger.debug("Cache hit on {}", uniqueId);
    return (HistoricalTimeSeries) element.getValue();
  }

  /**
   * Gets a sub-series based on the supplied dates.
   * 
   * @param hts  the time-series, null returns null
   * @param start  the start date, null will load the earliest date 
   * @param includeStart  whether or not the start date is included in the result
   * @param end  the end date, null will load the latest date
   * @param includeEnd  whether or not the end date is included in the result
   * @return the historical time-series, null if null input
   */
  private HistoricalTimeSeries getSubSeries(
      HistoricalTimeSeries hts, LocalDate start, boolean includeStart, LocalDate end, boolean includeEnd) {
    if (hts == null) {
      return null;
    }
    if (hts.getTimeSeries().isEmpty()) {
      return hts;
    }
    LocalDateDoubleTimeSeries timeSeries = (LocalDateDoubleTimeSeries) hts.getTimeSeries().subSeries(start, includeStart, end, includeEnd);
    return new SimpleHistoricalTimeSeries(hts.getUniqueId(), timeSeries);
  }

}