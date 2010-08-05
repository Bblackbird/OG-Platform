/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import static org.junit.Assert.assertEquals;

import java.util.TreeMap;

import org.junit.Test;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;

/**
 * 
 */
public class LogLinearInterpolator1DTest {
  private static final Interpolator1D<Interpolator1DDataBundle> LINEAR = new LinearInterpolator1D();
  private static final Interpolator1D<Interpolator1DDataBundle> INTERPOLATOR = new LogLinearInterpolator1D();
  private static final Function1D<Double, Double> FUNCTION = new Function1D<Double, Double>() {

    @Override
    public Double evaluate(final Double x) {
      return 2 * x - 7;
    }
  };
  private static final Interpolator1DDataBundle MODEL;
  private static final Interpolator1DDataBundle TRANSFORMED_MODEL;
  private static final double EPS = 1e-9;

  static {
    final TreeMap<Double, Double> data = new TreeMap<Double, Double>();
    final TreeMap<Double, Double> transformedData = new TreeMap<Double, Double>();
    double x;
    for (int i = 0; i < 10; i++) {
      x = Double.valueOf(i);
      data.put(x, FUNCTION.evaluate(x));
      transformedData.put(x, Math.log(FUNCTION.evaluate(x)));
    }
    MODEL = LINEAR.getDataBundle(data);
    TRANSFORMED_MODEL = INTERPOLATOR.getDataBundle(transformedData);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDataBundle() {
    INTERPOLATOR.interpolate(null, 3.4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    INTERPOLATOR.interpolate(MODEL, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLowValue() {
    INTERPOLATOR.interpolate(MODEL, -2.);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHgihValue() {
    INTERPOLATOR.interpolate(MODEL, 12.);
  }

  @Test
  public void test() {
    assertEquals(Math.exp(INTERPOLATOR.interpolate(MODEL, 3.4)), LINEAR.interpolate(TRANSFORMED_MODEL, 3.4), EPS);
  }
}
