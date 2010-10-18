/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.yield;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.opengamma.util.ArgumentChecker;

/**
 * Factory to obtain instances of {@code YieldConvention}.
 */
public final class YieldConventionFactory {

  /**
   * Singleton instance.
   */
  public static final YieldConventionFactory INSTANCE = new YieldConventionFactory();

  /**
   * Map of convention name to convention.
   */
  private final Map<String, YieldConvention> _conventionMap = new HashMap<String, YieldConvention>();

  /**
   * Creates the factory.
   */
  private YieldConventionFactory() {
    store(SimpleYieldConvention.US_STREET);
    store(SimpleYieldConvention.US_IL_REAL);
    store(SimpleYieldConvention.US_IL_REAL);
    store(SimpleYieldConvention.US_IL_REAL, "U.S. I/L REAL YLD");
    store(SimpleYieldConvention.US_STREET, "STREET CONVENTION");
    store(SimpleYieldConvention.US_TREASURY_EQUIVILANT);
    store(SimpleYieldConvention.JGB_SIMPLE);
    store(SimpleYieldConvention.MONEY_MARKET);
    store(SimpleYieldConvention.TRUE);
  }

  /**
   * Stores the convention.
   * @param convention  the convention to store, not null
   */
  private void store(final YieldConvention convention) {
    ArgumentChecker.notNull(convention, "YieldConvention");
    _conventionMap.put(convention.getConventionName().toLowerCase(Locale.ENGLISH), convention);
  }
  
  /**
   * Stores the convention with an alternative string name
   * @param convention  the convention to store, not null
   * @param name the alternative name for the convention, not null
   */
  private void store(final YieldConvention convention, final String name) {
    ArgumentChecker.notNull(convention, "YieldConvention");
    _conventionMap.put(name.toLowerCase(Locale.ENGLISH), convention);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a convention by name.
   * Matching is case insensitive.
   * @param name  the name, not null
   * @return the convention, null if not found
   */
  public YieldConvention getYieldConvention(final String name) {
    ArgumentChecker.notNull(name, "name");
    return _conventionMap.get(name.toLowerCase(Locale.ENGLISH));
  }

}
