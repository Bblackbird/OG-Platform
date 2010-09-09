/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.curve;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.math.function.Function;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.interpolation.LinearInterpolator1D;

/**
 * 
 */
public class CurveSpreadFunctionTest {
  private static final CurveSpreadFunction ADD = new AddCurveSpreadFunction();
  private static final CurveSpreadFunction DIVIDE = new DivideCurveSpreadFunction();
  private static final CurveSpreadFunction MULTIPLY = new MultiplyCurveSpreadFunction();
  private static final CurveSpreadFunction SUBTRACT = new SubtractCurveSpreadFunction();
  private static final double[] X = new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
  private static final double[] Y1 = new double[] {2, 4, 6, 8, 10, 12, 14, 16, 18};
  private static final double[] Y2 = new double[] {1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1};
  private static final NodalDoubleDoubleCurve NODAL1 = NodalDoubleDoubleCurve.of(X, Y1);
  private static final NodalDoubleDoubleCurve NODAL2 = NodalDoubleDoubleCurve.of(X, Y2);
  private static final InterpolatedDoubleDoubleCurve INTERPOLATED1 = InterpolatedDoubleDoubleCurve.of(X, Y1, new LinearInterpolator1D());
  private static final InterpolatedDoubleDoubleCurve INTERPOLATED2 = InterpolatedDoubleDoubleCurve.of(X, Y2, new LinearInterpolator1D());
  private static final ConstantDoubleDoubleCurve CONSTANT1 = ConstantDoubleDoubleCurve.of(2);
  private static final ConstantDoubleDoubleCurve CONSTANT2 = ConstantDoubleDoubleCurve.of(1.1);
  private static final Function1D<Double, Double> F1 = new Function1D<Double, Double>() {

    @Override
    public Double evaluate(final Double x) {
      return x * x;
    }

  };
  private static final Function1D<Double, Double> F2 = new Function1D<Double, Double>() {

    @Override
    public Double evaluate(final Double x) {
      return 3 * x;
    }

  };
  private static final FunctionalDoubleDoubleCurve FUNCTIONAL1 = FunctionalDoubleDoubleCurve.of(F1);
  private static final FunctionalDoubleDoubleCurve FUNCTIONAL2 = FunctionalDoubleDoubleCurve.of(F2);

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurves1() {
    ADD.evaluate((Curve<Double, Double>[]) null);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCurves1() {
    ADD.evaluate(new Curve[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurves2() {
    ADD.evaluate((Curve<Double, Double>[]) null);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCurves2() {
    ADD.evaluate(new Curve[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurves3() {
    ADD.evaluate((Curve<Double, Double>[]) null);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCurves3() {
    ADD.evaluate(new Curve[0]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurves4() {
    ADD.evaluate((Curve<Double, Double>[]) null);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyCurves4() {
    ADD.evaluate(new Curve[0]);
  }

  @Test
  public void testOperationName() {
    assertEquals(ADD.getOperationName(), "+");
    assertEquals(DIVIDE.getOperationName(), "/");
    assertEquals(MULTIPLY.getOperationName(), "*");
    assertEquals(SUBTRACT.getOperationName(), "-");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testNodal() {
    final Curve<Double, Double>[] curves = new Curve[] {NODAL1, NODAL2};
    Function<Double, Double> f = ADD.evaluate(curves);
    try {
      f.evaluate(1.1);
    } catch (final IllegalArgumentException e) {
    }
    assertEquals(f.evaluate(3.), Y1[2] + Y2[2], 0);
    f = DIVIDE.evaluate(curves);
    try {
      f.evaluate(1.1);
    } catch (final IllegalArgumentException e) {
    }
    assertEquals(f.evaluate(3.), Y1[2] / Y2[2], 0);
    f = MULTIPLY.evaluate(curves);
    try {
      f.evaluate(1.1);
    } catch (final IllegalArgumentException e) {
    }
    assertEquals(f.evaluate(3.), Y1[2] * Y2[2], 0);
    f = SUBTRACT.evaluate(curves);
    try {
      f.evaluate(1.1);
    } catch (final IllegalArgumentException e) {
    }
    assertEquals(f.evaluate(3.), Y1[2] - Y2[2], 0);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConstant() {
    final Curve<Double, Double>[] curves = new Curve[] {CONSTANT1, CONSTANT2};
    final double y1 = 2;
    final double y2 = 1.1;
    Function<Double, Double> f = ADD.evaluate(curves);
    assertEquals(f.evaluate(3.), y1 + y2, 0);
    f = DIVIDE.evaluate(curves);
    assertEquals(f.evaluate(3.), y1 / y2, 0);
    f = MULTIPLY.evaluate(curves);
    assertEquals(f.evaluate(3.), y1 * y2, 0);
    f = SUBTRACT.evaluate(curves);
    assertEquals(f.evaluate(3.), y1 - y2, 0);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testFunctional() {
    final double x = 3.5;
    final Curve<Double, Double>[] curves = new Curve[] {FUNCTIONAL1, FUNCTIONAL2};
    Function<Double, Double> f = ADD.evaluate(curves);
    assertEquals(f.evaluate(x), F1.evaluate(x) + F2.evaluate(x), 0);
    f = DIVIDE.evaluate(curves);
    assertEquals(f.evaluate(x), F1.evaluate(x) / F2.evaluate(x), 0);
    f = MULTIPLY.evaluate(curves);
    assertEquals(f.evaluate(x), F1.evaluate(x) * F2.evaluate(x), 0);
    f = SUBTRACT.evaluate(curves);
    assertEquals(f.evaluate(x), F1.evaluate(x) - F2.evaluate(x), 0);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testInterpolated() {
    final double x = 3.5;
    final Curve<Double, Double>[] curves = new Curve[] {INTERPOLATED1, INTERPOLATED2};
    Function<Double, Double> f = ADD.evaluate(curves);
    assertEquals(f.evaluate(x), INTERPOLATED1.getYValue(x) + INTERPOLATED2.getYValue(x), 0);
    f = DIVIDE.evaluate(curves);
    assertEquals(f.evaluate(x), INTERPOLATED1.getYValue(x) / INTERPOLATED2.getYValue(x), 0);
    f = MULTIPLY.evaluate(curves);
    assertEquals(f.evaluate(x), INTERPOLATED1.getYValue(x) * INTERPOLATED2.getYValue(x), 0);
    f = SUBTRACT.evaluate(curves);
    assertEquals(f.evaluate(x), INTERPOLATED1.getYValue(x) - INTERPOLATED2.getYValue(x), 0);
  }
}
