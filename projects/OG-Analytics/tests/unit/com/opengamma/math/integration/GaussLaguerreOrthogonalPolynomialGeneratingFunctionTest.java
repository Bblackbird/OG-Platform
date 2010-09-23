/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

import org.junit.Test;

/**
 * 
 */
public class GaussLaguerreOrthogonalPolynomialGeneratingFunctionTest extends OrthogonalPolynomialGeneratingFunctionTestCase {
  private static final double[] X2 = new double[] {0.585786, 3.41421};
  private static final double[] W2 = new double[] {0.853553, 0.146447};
  private static final double[] X3 = new double[] {0.415775, 2.29428, 6.28995};
  private static final double[] W3 = new double[] {0.711093, 0.278518, 0.0103893};
  private static final double[] X4 = new double[] {0.322548, 1.74576, 4.53662, 9.39507};
  private static final double[] W4 = new double[] {0.603154, 0.357419, 0.0388879, 0.000539295};
  private static final double[] X5 = new double[] {0.26356, 1.4134, 3.59643, 7.08581, 12.6408};
  private static final double[] W5 = new double[] {0.521756, 0.398667, 0.0759424, 0.00361176, 0.00002337};
  private static final GeneratingFunction<Double, GaussianQuadratureFunction> F = new GaussLaguerreOrthogonalPolynomialGeneratingFunction(0);
  private static final Double[] PARAMS = new Double[] {-1., 1.};

  @Test
  public void test() {
    testInputsFixedLimits(F, PARAMS);
    testResults(F.generate(2, PARAMS), X2, W2);
    testResults(F.generate(3, PARAMS), X3, W3);
    testResults(F.generate(4, PARAMS), X4, W4);
    testResults(F.generate(5, PARAMS), X5, W5);
  }
}
