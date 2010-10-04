/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.descriptive;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function2D;

/**
 * 
 */
public class LognormalPearsonKurtosisFromVolatilityCalculator extends Function2D<Double, Double> {
  private static final LognormalFisherKurtosisFromVolatilityCalculator CALCULATOR = new LognormalFisherKurtosisFromVolatilityCalculator();

  @Override
  public Double evaluate(final Double sigma, final Double t) {
    Validate.notNull(sigma, "sigma");
    Validate.notNull(t, "t");
    return CALCULATOR.evaluate(sigma, t) + 3;
  }

}
