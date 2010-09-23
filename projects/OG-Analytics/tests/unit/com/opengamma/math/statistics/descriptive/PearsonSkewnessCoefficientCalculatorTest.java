/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.descriptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 */
public class PearsonSkewnessCoefficientCalculatorTest {
  private static final double STD = 2.;
  private static final Function1D<double[], Double> FIRST = new PearsonFirstSkewnessCoefficientCalculator();
  private static final Function1D<double[], Double> SECOND = new PearsonSecondSkewnessCoefficientCalculator();
  private static final ProbabilityDistribution<Double> NORMAL = new NormalDistribution(0, STD, new MersenneTwister64(MersenneTwister64.DEFAULT_SEED));
  private static final double[] NORMAL_DATA = new double[50000];
  static {
    for (int i = 0; i < 50000; i++) {
      NORMAL_DATA[i] = ((int) (100 * NORMAL.nextRandom())) / 100.;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullFirst() {
    FIRST.evaluate((double[]) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSecond() {
    SECOND.evaluate((double[]) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyFirst() {
    FIRST.evaluate(new double[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptySecond() {
    SECOND.evaluate(new double[0]);
  }

  @Test
  public void test() {
    final Double t1 = FIRST.evaluate(NORMAL_DATA);
    final Double t2 = SECOND.evaluate(NORMAL_DATA);
    assertEquals(t1, 0, 0.1);
    assertEquals(t2, 0, 0.1);
  }
}
