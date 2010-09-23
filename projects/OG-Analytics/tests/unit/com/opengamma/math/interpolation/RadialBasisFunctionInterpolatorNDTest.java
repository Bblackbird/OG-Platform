/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import java.util.Arrays;

import org.junit.Test;

import com.opengamma.math.function.Function1D;

/**
 * 
 */
public class RadialBasisFunctionInterpolatorNDTest extends InterpolatorNDTestCase {
  private static final Function1D<Double, Double> UNIFORM_WEIGHT_FUNCTION = new MultiquadraticRadialBasisFunction();
  private static final InterpolatorND INTERPOLATOR = new RadialBasisFunctionInterpolatorND(UNIFORM_WEIGHT_FUNCTION, false);

  @Test(expected = IllegalArgumentException.class)
  public void testNullFunction() {
    new RadialBasisFunctionInterpolatorND(null, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    INTERPOLATOR.interpolate(null, Arrays.asList(4.));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue() {
    INTERPOLATOR.interpolate(FLAT_DATA, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongDimension() {
    INTERPOLATOR.interpolate(FLAT_DATA, Arrays.asList(2., 3., 4., 5.));
  }

  @Test
  public void testInputs() {
    super.testData(INTERPOLATOR);
  }
}
