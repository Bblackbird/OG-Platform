/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.curve;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;

import org.junit.Test;

import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.LinearInterpolator1D;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;

/**
 * 
 */
public class ConstantDoublesCurveTest {
  private static final double Y1 = 20;
  private static final double Y2 = 21;
  private static final String NAME1 = "a";
  private static final String NAME2 = "b";
  private static final ConstantDoublesCurve CURVE = new ConstantDoublesCurve(Y1, NAME1);

  @Test(expected = UnsupportedOperationException.class)
  public void testGetXData() {
    CURVE.getXData();
  }

  @Test
  public void testEqualsAndHashCode() {
    ConstantDoublesCurve other = new ConstantDoublesCurve(Y1, NAME1);
    assertEquals(CURVE, other);
    assertEquals(CURVE.hashCode(), other.hashCode());
    other = new ConstantDoublesCurve(Y2, NAME1);
    assertFalse(CURVE.equals(other));
    other = new ConstantDoublesCurve(Y1);
    assertFalse(CURVE.equals(other));
    other = new ConstantDoublesCurve(Y1, NAME2);
    assertFalse(CURVE.equals(other));
  }

  @Test
  public void testGetters() {
    assertEquals(CURVE.getName(), NAME1);
    assertEquals(CURVE.getYValue(30.1), Y1, 0);
    assertEquals(CURVE.size(), 1);
    assertArrayEquals(CURVE.getYData(), new Double[] {Y1});
  }

  @Test
  public void testStaticConstruction() {
    ConstantDoublesCurve curve = new ConstantDoublesCurve(Y1);
    ConstantDoublesCurve other = ConstantDoublesCurve.from(Y1);
    assertArrayEquals(curve.getYData(), other.getYData());
    assertFalse(curve.getName().equals(other.getName()));
    curve = new ConstantDoublesCurve(Y1, NAME1);
    other = ConstantDoublesCurve.from(Y1, NAME1);
    assertEquals(curve, other);
  }

  @Test
  public void testConvert() {
    final double eps = 1e-15;
    final double[] x = new double[] {0, 1, 2};
    final double[] y = new double[] {Y1, Y1, Y1};
    final LinearInterpolator1D interpolator = new LinearInterpolator1D();
    DoublesCurve other = CURVE.toNodalDoubleDoubleCurve(x);
    assertArrayEquals(other.getXDataAsPrimitive(), x, eps);
    assertArrayEquals(other.getYDataAsPrimitive(), y, eps);
    other = CURVE.toInterpolatedDoubleDoubleCurve(x, interpolator);
    assertArrayEquals(other.getXDataAsPrimitive(), x, eps);
    assertArrayEquals(other.getYDataAsPrimitive(), y, eps);
    other = CURVE.toInterpolatedDoubleDoubleCurve(x, Collections.<Double, Interpolator1D<? extends Interpolator1DDataBundle>> singletonMap(Double.POSITIVE_INFINITY, interpolator));
    assertArrayEquals(other.getXDataAsPrimitive(), x, eps);
    assertArrayEquals(other.getYDataAsPrimitive(), y, eps);
  }
}
