/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.curve;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class NodalCurveShiftFunctionTest {
  private static final int N = 10;
  private static final double[] X = new double[N];
  private static final double[] Y = new double[N];
  private static final NodalDoublesCurve CURVE;
  private static final NodalCurveShiftFunction F = new NodalCurveShiftFunction();
  private static final double SHIFT = 0.12;
  private static final double EPS = 1e-15;

  static {
    for (int i = 0; i < N; i++) {
      X[i] = i;
      Y[i] = 2 * i + 1;
    }
    CURVE = NodalDoublesCurve.fromSorted(X, Y, "A");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSingleShiftBadX1() {
    F.evaluate(CURVE, 2.4, SHIFT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSingleShiftBadX2() {
    F.evaluate(CURVE, 2.4, SHIFT, "A");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve1() {
    F.evaluate(null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve2() {
    F.evaluate(null, 1, "B");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve3() {
    F.evaluate(null, 1, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve4() {
    F.evaluate(null, 1, 5, "B");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve5() {
    F.evaluate(null, new double[] {1}, new double[] {1});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurve6() {
    F.evaluate(null, new double[] {1}, new double[] {1}, "B");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnequalArrayLength1() {
    F.evaluate(CURVE, new double[] {1}, new double[] {3, 4});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnequalArrayLength2() {
    F.evaluate(CURVE, new double[] {1}, new double[] {3, 4}, "S");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultipleShiftBadX1() {
    final double[] x = new double[] {1, 2, 3, 3.4, 4};
    final double[] y = new double[] {1, 1, 1, 1, 1};
    F.evaluate(CURVE, x, y);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultipleShiftBadX2() {
    final double[] x = new double[] {1, 2, 3, 3.4, 4};
    final double[] y = new double[] {1, 1, 1, 1, 1};
    F.evaluate(CURVE, x, y, "B");
  }

  @Test
  public void testParallel() {
    NodalDoublesCurve shifted = F.evaluate(CURVE, SHIFT);
    double[] x = shifted.getXDataAsPrimitive();
    double[] y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      assertEquals(y[i], Y[i] + SHIFT, EPS);
    }
    assertEquals(shifted.getName(), "PARALLEL_SHIFT_A");
    final String newName = "B";
    shifted = F.evaluate(CURVE, SHIFT, newName);
    x = shifted.getXDataAsPrimitive();
    y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      assertEquals(y[i], Y[i] + SHIFT, EPS);
    }
    assertEquals(shifted.getName(), newName);
  }

  @Test
  public void testSingleShift() {
    final double shiftX = 3;
    NodalDoublesCurve shifted = F.evaluate(CURVE, shiftX, SHIFT);
    double[] x = shifted.getXDataAsPrimitive();
    double[] y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      if (i == 3) {
        assertEquals(y[i], Y[i] + SHIFT, EPS);
      } else {
        assertEquals(y[i], Y[i], EPS);
      }
    }
    assertEquals(shifted.getName(), "SINGLE_SHIFT_A");
    final String newName = "B";
    shifted = F.evaluate(CURVE, shiftX, SHIFT, newName);
    x = shifted.getXDataAsPrimitive();
    y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      if (i == 3) {
        assertEquals(y[i], Y[i] + SHIFT, EPS);
      } else {
        assertEquals(y[i], Y[i], EPS);
      }
    }
    assertEquals(shifted.getName(), newName);
  }

  @Test
  public void testMultipleShift() {
    final double[] shiftX = new double[] {1, 2, 3, 4};
    final double[] shiftY = new double[] {0.1, 0.2, -0.1, -0.2};
    NodalDoublesCurve shifted = F.evaluate(CURVE, shiftX, shiftY);
    double[] x = shifted.getXDataAsPrimitive();
    double[] y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      if (i >= 1 && i <= 4) {
        assertEquals(y[i], Y[i] + shiftY[i - 1], EPS);
      } else {
        assertEquals(y[i], Y[i], EPS);
      }
    }
    assertEquals(shifted.getName(), "MULTIPLE_POINT_SHIFT_A");
    final String newName = "B";
    shifted = F.evaluate(CURVE, shiftX, shiftY, newName);
    x = shifted.getXDataAsPrimitive();
    y = shifted.getYDataAsPrimitive();
    for (int i = 0; i < N; i++) {
      assertEquals(x[i], X[i], EPS);
      if (i >= 1 && i <= 4) {
        assertEquals(y[i], Y[i] + shiftY[i - 1], EPS);
      } else {
        assertEquals(y[i], Y[i], EPS);
      }
    }
    assertEquals(shifted.getName(), newName);
  }

}
