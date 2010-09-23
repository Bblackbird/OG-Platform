/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

/**
 * Base interface for deciding whether an updated value should be treated as a delta. For example, we might only be
 * interested in changes up to the fourth decimal place for some type of {@link Number}.
 *
 * @param <T> The type for delta comparisons.
 */
public interface DeltaComparer<T> {
  
  /**
   * Indicates whether the difference between two values is sufficient to be treated as a delta.
   * 
   * @param  previousValue
   *         The previous computed value
   *         
   * @param  newValue
   *         The new computed value
   *         
   * @return <tt>true</tt> if {@code newValue} should be treated as a delta, otherwise <tt>false</tt>   
   */
  boolean isDelta(T previousValue, T newValue);
}
