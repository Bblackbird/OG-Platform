/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.descriptive;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.util.ArgumentChecker;

/**
 * 
 */
public class SampleMomentCalculator extends Function1D<double[], Double> {
  private final int _n;

  public SampleMomentCalculator(final int n) {
    ArgumentChecker.notNegative(n, "n");
    _n = n;
  }

  @Override
  public Double evaluate(final double[] x) {
    Validate.notNull(x, "x");
    ArgumentChecker.notEmpty(x, "x");
    if (x.length == 0) {
      throw new IllegalArgumentException("Array was empty");
    }
    if (_n == 0) {
      return 1.;
    }
    double sum = 0;
    for (final Double d : x) {
      sum += Math.pow(d, _n);
    }
    return sum / (x.length - 1);
  }

}
