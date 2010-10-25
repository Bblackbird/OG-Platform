/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial;

import com.opengamma.engine.config.ConfigSource;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.historicaldata.HistoricalDataSource;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.financial.convention.ConventionBundleMaster;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.world.exchange.master.ExchangeSource;
import com.opengamma.financial.world.holiday.master.HolidayMaster;
import com.opengamma.financial.world.holiday.master.HolidaySource;
import com.opengamma.financial.world.region.RegionSource;

/**
 * Utility methods to pull standard objects out of a {@link FunctionExecutionContext}.
 */
public final class OpenGammaExecutionContext {

  /**
   * The name under which an instance of {@link TimeSeriesSource} should be bound.
   */
  public static final String HISTORICAL_DATA_SOURCE_NAME = "historicalDataSource";
  /**
   * The name under which an instance of {@link RegionSource} should be bound.
   */
  public static final String REGION_SOURCE_NAME = "regionSource";
  /**
   * The name under which an instance of {@link HolidayMaster} should be bound.
   */
  public static final String HOLIDAY_SOURCE_NAME = "holidaySource";
  /**
   * The name under which an instance of {@link ConventionBundleMaster} should be bound.
   */
  private static final String CONVENTION_BUNDLE_SOURCE_NAME = "conventionBundleSource";
  /**
   * The name under which an instance of {@link ConfigSource} should be bound.
   */
  public static final String CONFIG_SOURCE_NAME = "configSource";
  /**
   * The name under which an instance of {@link {ExchangeSource} should be bound.
   */
  public static final String EXCHANGE_SOURCE_NAME = "exchangeSource";

  /**
   * Restricted constructor.
   */
  private OpenGammaExecutionContext() {
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code HistoricalDataSource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static HistoricalDataSource getHistoricalDataSource(FunctionExecutionContext context) {
    return (HistoricalDataSource) context.get(HISTORICAL_DATA_SOURCE_NAME);
  }

  /**
   * Stores a {@code HistoricalDataSource} in the context.
   * @param context  the context to store in, not null
   * @param historicalDataProvider  the value to store, not null
   */
  public static void setHistoricalDataSource(FunctionExecutionContext context, HistoricalDataSource historicalDataProvider) {
    context.put(HISTORICAL_DATA_SOURCE_NAME, historicalDataProvider);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code SecuritySource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static SecuritySource getSecuritySource(FunctionExecutionContext context) {
    return context.getSecuritySource();
  }

  /**
   * Stores a {@code SecuritySource} in the context.
   * @param context  the context to store in, not null
   * @param securitySource  the value to store, not null
   */
  public static void setSecuritySource(FunctionExecutionContext context, SecuritySource securitySource) {
    context.setSecuritySource(securitySource);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code ConventionBundleSource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static ConventionBundleSource getConventionBundleSource(FunctionExecutionContext context) {
    return (ConventionBundleSource) context.get(CONVENTION_BUNDLE_SOURCE_NAME);
  }

  /**
   * Stores a {@code ConventionBundleSource} in the context.
   * @param context  the context to store in, not null
   * @param conventionBundleSource  the value to store, not null
   */
  public static void setConventionBundleSource(FunctionExecutionContext context, ConventionBundleSource conventionBundleSource) {
    context.put(CONVENTION_BUNDLE_SOURCE_NAME, conventionBundleSource);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code RegionSource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static RegionSource getRegionSource(FunctionExecutionContext context) {
    return (RegionSource) context.get(REGION_SOURCE_NAME);
  }

  /**
   * Stores a {@code RegionSource} in the context.
   * @param context  the context to store in, not null
   * @param regionSource  the value to store, not null
   */
  public static void setRegionSource(FunctionExecutionContext context, RegionSource regionSource) {
    context.put(REGION_SOURCE_NAME, regionSource);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code HolidaySource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static HolidaySource getHolidaySource(FunctionExecutionContext context) {
    return (HolidaySource) context.get(HOLIDAY_SOURCE_NAME);
  }

  /**
   * Stores a {@code HolidaySource} in the context.
   * @param context  the context to store in, not null
   * @param holidaySource  the value to store, not null
   */
  public static void setHolidaySource(FunctionExecutionContext context, HolidaySource holidaySource) {
    context.put(HOLIDAY_SOURCE_NAME, holidaySource);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code ExchangeSource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static ExchangeSource getExchangeSource(FunctionExecutionContext context) {
    return (ExchangeSource) context.get(EXCHANGE_SOURCE_NAME);
  }

  /**
   * Stores a {@code ExchangeSource} in the context.
   * @param context  the context to store in, not null
   * @param exchangeSource  the value to store, not null
   */
  public static void setExchangeSource(FunctionExecutionContext context, ExchangeSource exchangeSource) {
    context.put(EXCHANGE_SOURCE_NAME, exchangeSource);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets a {@code ConfigSource} from the context.
   * @param context  the context to examine, not null
   * @return the value, null if not found
   */
  public static ConfigSource getConfigSource(FunctionExecutionContext context) {
    return (ConfigSource) context.get(CONFIG_SOURCE_NAME);
  }

  /**
   * Stores a {@code ConfigSource} in the context.
   * @param context  the context to store in, not null
   * @param configSource  the value to store, not null
   */
  public static void setConfigSource(FunctionExecutionContext context, ConfigSource configSource) {
    context.put(CONFIG_SOURCE_NAME, configSource);
  }
}
