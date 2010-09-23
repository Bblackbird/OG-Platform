/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.interpolation.data.Interpolator1DDoubleQuadraticDataBundle;

/**
 * 
 */
public class Extrapolator1DTest {
  private static final RandomEngine RANDOM = new MersenneTwister64(MersenneTwister64.DEFAULT_SEED);
  private static final Interpolator1D<Interpolator1DDoubleQuadraticDataBundle> INTERPOLATOR = new DoubleQuadraticInterpolator1D();
  private static final LinearExtrapolator1D<Interpolator1DDoubleQuadraticDataBundle> LINEAR_EXTRAPOLATOR = new LinearExtrapolator1D<Interpolator1DDoubleQuadraticDataBundle>(INTERPOLATOR);
  private static final FlatExtrapolator1D<Interpolator1DDoubleQuadraticDataBundle> FLAT_EXTRAPOLATOR = new FlatExtrapolator1D<Interpolator1DDoubleQuadraticDataBundle>();
  private static final Interpolator1DDoubleQuadraticDataBundle DATA;

  private static final double[] X_DATA = new double[] {0, 0.4, 1.0, 1.8, 2.8, 5};
  private static final double[] Y_DATA = new double[] {3., 4., 3.1, 2., 7., 2.};

  private static final double[] X_TEST = new double[] {-1.0, 6.0};
  private static final double[] Y_TEST = new double[] {-1.1, -5.272727273};

  static {
    DATA = INTERPOLATOR.getDataBundleFromSortedArrays(X_DATA, Y_DATA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInterpolator1() {
    new LinearExtrapolator1D<Interpolator1DDataBundle>(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData1() {
    LINEAR_EXTRAPOLATOR.interpolate(null, 1.4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue1() {
    LINEAR_EXTRAPOLATOR.interpolate(DATA, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData2() {
    FLAT_EXTRAPOLATOR.interpolate(null, 1.4);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue2() {
    FLAT_EXTRAPOLATOR.interpolate(DATA, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueInRange1() {
    FLAT_EXTRAPOLATOR.interpolate(DATA, 1.2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValueInRange2() {
    LINEAR_EXTRAPOLATOR.interpolate(DATA, 1.2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDataBundleType1() {
    FLAT_EXTRAPOLATOR.getDataBundle(X_DATA, Y_DATA);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDataBundleType2() {
    FLAT_EXTRAPOLATOR.getDataBundleFromSortedArrays(X_DATA, Y_DATA);
  }

  @Test
  public void testDataBundleType3() {
    assertEquals(INTERPOLATOR.getDataBundle(new double[] {1, 2, 3}, new double[] {1, 2, 3}).getClass(), Interpolator1DDoubleQuadraticDataBundle.class);
  }

  @Test
  public void testDataBundleType4() {
    assertEquals(INTERPOLATOR.getDataBundleFromSortedArrays(new double[] {1, 2, 3}, new double[] {1, 2, 3}).getClass(), Interpolator1DDoubleQuadraticDataBundle.class);
  }

  @Test
  public void testFlatExtrapolation() {
    for (int i = 0; i < 100; i++) {
      final double x = RANDOM.nextDouble() * 20.0 - 10;
      if (x < 0) {
        assertEquals(3.0, FLAT_EXTRAPOLATOR.interpolate(DATA, x), 1e-12);
      } else if (x > 5.0) {
        assertEquals(2.0, FLAT_EXTRAPOLATOR.interpolate(DATA, x), 1e-12);
      }
    }
  }

  @Test
  public void testLinearExtrapolation() {
    for (int i = 0; i < X_TEST.length; i++) {
      assertEquals(LINEAR_EXTRAPOLATOR.interpolate(DATA, X_TEST[i]), Y_TEST[i], 1e-6);
    }
  }
}
