/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.descriptive.robust;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 */
public class InterquartileRangeCalculatorTest {
  private static final Function1D<Double[], Double> IQR = new InterquartileRangeCalculator();
  private static final RandomEngine RANDOM = new MersenneTwister64(MersenneTwister64.DEFAULT_SEED);
  private static final ProbabilityDistribution<Double> NORMAL = new NormalDistribution(0, 1);
  private static final Double[] UNIFORM_DATA;
  private static final Double[] NORMAL_DATA;
  private static final double EPS = 1e-2;
  static {
    final int n = 500000;
    UNIFORM_DATA = new Double[n];
    NORMAL_DATA = new Double[n];
    for (int i = 0; i < n; i++) {
      UNIFORM_DATA[i] = RANDOM.nextDouble();
      NORMAL_DATA[i] = NORMAL.nextRandom();
    }
  }

  @Test(expected = NullPointerException.class)
  public void testNull() {
    IQR.evaluate((Double[]) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsufficientData() {
    IQR.evaluate(new Double[] { 1., 2. });
  }

  @Test
  public void test() {
    final Double[] x1 = new Double[] { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10. };
    final Double[] x2 = new Double[] { 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12., 13. };
    assertEquals(IQR.evaluate(x1), 5, 1e-15);
    assertEquals(IQR.evaluate(x2), 6, 1e-15);
    assertEquals(IQR.evaluate(UNIFORM_DATA), 0.5, EPS);
    assertEquals(IQR.evaluate(NORMAL_DATA), 2 * NORMAL.getInverseCDF(0.75), EPS);
  }
}
