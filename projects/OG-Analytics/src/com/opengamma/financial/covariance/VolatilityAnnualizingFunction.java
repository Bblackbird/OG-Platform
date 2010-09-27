/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.covariance;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function;

/**
 * 
 */
public class VolatilityAnnualizingFunction implements Function<Double, Double> {
  private final double _periodsPerYear;

  public VolatilityAnnualizingFunction(final double periodsPerYear) {
    Validate.isTrue(periodsPerYear > 0);
    _periodsPerYear = periodsPerYear;
  }

  @Override
  public Double evaluate(final Double... x) {
    Validate.notNull(x, "x");
    Validate.notEmpty(x, "x");
    Validate.notNull(x[0], "x");
    return Math.sqrt(_periodsPerYear / x[0]);
  }

}
