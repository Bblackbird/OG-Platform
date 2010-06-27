/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.rootfinding.newton;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.math.linearalgebra.SVDecompositionColt;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;

/**
 * 
 */
public class JacobianDirectionFunctionTest {
  private static final SVDecompositionColt SV = new SVDecompositionColt();
  private static final JacobianDirectionFunction F = new JacobianDirectionFunction(SV);
  private static final double X0 = 2.4;
  private static final double X1 = 7.6;
  private static final double X2 = 4.5;
  private static final DoubleMatrix2D M = new DoubleMatrix2D(new double[][] {new double[] {X0, 0, 0}, new double[] {0, X1, 0}, new double[] {0, 0, X2}});
  private static final DoubleMatrix1D Y = new DoubleMatrix1D(new double[] {1, 1, 1});

  @Test(expected = IllegalArgumentException.class)
  public void testNull() {
    new JacobianDirectionFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEstimate() {
    F.getDirection(null, Y);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullY() {
    F.getDirection(M, null);
  }

  @Test
  public void test() {
    double eps = 1e-9;
    DoubleMatrix1D direction = F.getDirection(M, Y);
    assertEquals(direction.getEntry(0), 1. / X0, eps);
    assertEquals(direction.getEntry(1), 1. / X1, eps);
    assertEquals(direction.getEntry(2), 1. / X2, eps);
  }
}
