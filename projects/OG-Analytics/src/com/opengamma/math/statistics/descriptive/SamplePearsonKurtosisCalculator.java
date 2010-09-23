/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.descriptive;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;

/**
 * 
 */
public class SamplePearsonKurtosisCalculator extends Function1D<double[], Double> {
  private final Function1D<double[], Double> _kurtosis = new SampleFisherKurtosisCalculator();

  @Override
  public Double evaluate(final double[] x) {
    Validate.notNull(x, "x");
    if (x.length < 2) {
      throw new IllegalArgumentException("Need at least two points to calculate kurtosis");
    }
    return _kurtosis.evaluate(x) + 3;
  }
}
