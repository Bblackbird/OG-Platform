/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.object;

import java.math.BigDecimal;

import org.junit.Ignore;

/**
 * 
 *
 * @author jim
 */
@Ignore
public abstract class BigDecimalObjectTimeSeriesTest<DATE_TYPE> extends ObjectTimeSeriesTest<DATE_TYPE, BigDecimal> {
  @Override
  public BigDecimal[] testValues() {
    return new BigDecimal[] { BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4),
                              BigDecimal.valueOf(5), BigDecimal.valueOf(6) };
  }
    
  @Override
  public BigDecimal[] emptyValues() {
    return new BigDecimal[] {};
  };
}
