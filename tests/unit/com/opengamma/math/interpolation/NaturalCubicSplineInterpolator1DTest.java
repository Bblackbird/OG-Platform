/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import static org.junit.Assert.assertEquals;

import java.util.TreeMap;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.RealPolynomialFunction1D;

/**
 * 
 */
public class NaturalCubicSplineInterpolator1DTest {
  private static final RandomEngine RANDOM = new MersenneTwister64(MersenneTwister64.DEFAULT_SEED);

  private static final double[] COEFF = new double[] {-0.4, 0.05, 0.2, 1.};

  private static final Interpolator1D<Interpolator1DCubicSplineDataBundle, InterpolationResult> INTERPOLATOR = new NaturalCubicSplineInterpolator1D();
  private static final Function1D<Double, Double> CUBIC = new RealPolynomialFunction1D(COEFF);
  private static final double EPS = 1e-2;
  private static final Interpolator1DCubicSplineDataBundle MODEL;

  static {
    final TreeMap<Double, Double> data = new TreeMap<Double, Double>();
    for (int i = 0; i < 12; i++) {
      final double x = i / 10.;
      data.put(x, CUBIC.evaluate(x));
    }
    MODEL = INTERPOLATOR.getDataBundle(data);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullInputMap() {
    INTERPOLATOR.interpolate((Interpolator1DCubicSplineDataBundle) null, 3.);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullInterpolateValue() {
    INTERPOLATOR.interpolate(MODEL, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHighValue() {
    INTERPOLATOR.interpolate(MODEL, 15.);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLowValue() {
    INTERPOLATOR.interpolate(MODEL, -12.);
  }

  @Test
  public void test() {
    for (int i = 0; i < 100; i++) {
      final double x = RANDOM.nextDouble();
      assertEquals(CUBIC.evaluate(x), INTERPOLATOR.interpolate(MODEL, x).getResult(), EPS);
    }
  }
}
