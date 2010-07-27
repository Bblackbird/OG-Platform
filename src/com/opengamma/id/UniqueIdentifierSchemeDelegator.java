/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.id;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.opengamma.util.ArgumentChecker;

/**
 * Delegator that switches between multiple implementations based on the scheme of a unique identifier.
 * <p>
 * This class can be used on its own, however it is best used by creating a subclass.
 * 
 * @param <T>  the type of the delegate
 */
public class UniqueIdentifierSchemeDelegator<T> {

  /**
   * The default delegate.
   */
  private final T _defaultDelegate;
  /**
   * The map of registered delegates.
   */
  private final ConcurrentMap<String, T> _schemeToDelegateMap = new ConcurrentHashMap<String, T>();

  /**
   * Creates an instance specifying the default delegate.
   * @param defaultDelegate  the delegate to use when no scheme matches, not null
   */
  protected UniqueIdentifierSchemeDelegator(final T defaultDelegate) {
    ArgumentChecker.notNull(defaultDelegate, "defaultDelegate");
    _defaultDelegate = defaultDelegate;
  }

  /**
   * Creates an instance specifying the default delegate.
   * @param defaultDelegate  the delegate to use when no scheme matches, not null
   * @param delegates  the map of delegates by scheme to switch on, not null
   */
  protected UniqueIdentifierSchemeDelegator(final T defaultDelegate, final Map<String, T> delegates) {
    ArgumentChecker.notNull(defaultDelegate, "defaultDelegate");
    ArgumentChecker.notNull(delegates, "delegates");
    _defaultDelegate = defaultDelegate;
    for (Map.Entry<String, T> delegate : delegates.entrySet()) {
      registerDelegate(delegate.getKey(), delegate.getValue());
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the default delegate.
   * @return the default delegate, not null
   */
  protected T getDefaultDelegate() {
    return _defaultDelegate;
  }

  /**
   * Gets the map of registered delegates.
   * @return the registered delegates, unmodifiable, not null
   */
  protected Map<String, T> getDelegates() {
    return Collections.unmodifiableMap(_schemeToDelegateMap);
  }

  //-------------------------------------------------------------------------
  /**
   * Chooses the delegate for a specific unique identifier.
   * @param uid  the unique identifier, not null
   * @return the delegate, not null
   */
  protected T chooseDelegate(final UniqueIdentifier uid) {
    final T delegate = _schemeToDelegateMap.get(uid.getScheme());
    return (delegate != null) ? delegate : _defaultDelegate;
  }

  //-------------------------------------------------------------------------
  /**
   * Registers a delegate based on a scheme.
   * @param scheme  the scheme to match, not null
   * @param delegate  the delegate to use, not null
   */
  public void registerDelegate(final String scheme, final T delegate) {
    ArgumentChecker.notNull(scheme, "scheme");
    ArgumentChecker.notNull(delegate, "delegate");
    _schemeToDelegateMap.put(scheme, delegate);
  }

  /**
   * Removes a delegate from those being used.
   * @param scheme  the scheme to remove, not null
   */
  public void removeDelegate(final String scheme) {
    ArgumentChecker.notNull(scheme, "scheme");
    _schemeToDelegateMap.remove(scheme);
  }

}
