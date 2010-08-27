/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.differentiation;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix1D;

/**
 * Differentiates a scalar field (i.e. there is a scalar value for every point in some vector space) with respect to the vector space using finite difference. For a function y =f(<b>x</b>) 
 * where <b>x</b> is a n-dimensional vector and y is a scalar this produces the gradient function, <b>g</b>(<b>x</b>), i.e. a function that returns the gradient for each point <b>x</b>,
 * where <b>g</b> is the n-dimensional vector dy/dx_i
 */
public class ScalarFieldFirstOrderDifferentiator implements Derivative<DoubleMatrix1D, Double, DoubleMatrix1D> {

  private static final double DEFAULT_EPS = 1e-5;
  private static final double MIN_EPS = Math.sqrt(Double.MIN_NORMAL);

  private static FiniteDifferenceType DIFF_TYPE = FiniteDifferenceType.CENTRAL;

  private final double _eps;
  private final double _twoEps;
  private final FiniteDifferenceType _differenceType;

  public ScalarFieldFirstOrderDifferentiator() {
    this(DIFF_TYPE, DEFAULT_EPS);
  }

  /**
   * Approximates the derivative of a scalar function by finite difference. If the size of the domain is very small (i.e. << 1) or very large (>>1), consider rescaling first
   * @param differenceType FORWARD, BACKWARD, or CENTRAL - unless you have a compelling reason not to (e.g. you are at the edge of the function domain) use CENTRAL 
   * @param eps the step size used to approximate the derivative - Do NOT use too small a value, the result will most likely be dominated by noise (i.e. nonsense). 
   * Use around 1e-5 times the domain size. 
   * 
   */
  public ScalarFieldFirstOrderDifferentiator(final FiniteDifferenceType differenceType, final double eps) {
    Validate.notNull(differenceType);
    if (eps < MIN_EPS) {
      throw new IllegalArgumentException("eps is too small. A good value is 1e-5*size of domain. The minimum value is "
          + MIN_EPS);
    }
    _differenceType = differenceType;
    _eps = eps;
    _twoEps = 2 * _eps;
  }

  @Override
  public Function1D<DoubleMatrix1D, DoubleMatrix1D> derivative(final Function1D<DoubleMatrix1D, Double> function) {
    Validate.notNull(function);
    switch (_differenceType) {
      case FORWARD:
        return new Function1D<DoubleMatrix1D, DoubleMatrix1D>() {

          @SuppressWarnings("synthetic-access")
          @Override
          public DoubleMatrix1D evaluate(DoubleMatrix1D x) {
            double y = function.evaluate(x);
            int n = x.getNumberOfElements();
            double[] xData = x.getData();
            double oldValue;
            double[] res = new double[n];
            for (int i = 0; i < n; i++) {
              oldValue = xData[i];
              xData[i] += _eps;
              res[i] = (function.evaluate(x) - y) / _eps; // x has been changed via the access to the underlying data
              xData[i] = oldValue;
            }
            return new DoubleMatrix1D(res);
          }
        };
      case CENTRAL:
        return new Function1D<DoubleMatrix1D, DoubleMatrix1D>() {

          @SuppressWarnings("synthetic-access")
          @Override
          public DoubleMatrix1D evaluate(DoubleMatrix1D x) {
            int n = x.getNumberOfElements();
            double[] xData = x.getData();
            double oldValue;
            double up, down;
            double[] res = new double[n];
            for (int i = 0; i < n; i++) {
              oldValue = xData[i];
              xData[i] += _eps;
              up = function.evaluate(x); // x has been changed via the access to the underlying data
              xData[i] -= _twoEps;
              down = function.evaluate(x);
              res[i] = (up - down) / _twoEps;
              xData[i] = oldValue;
            }
            return new DoubleMatrix1D(res);
          }
        };
      case BACKWARD:
        return new Function1D<DoubleMatrix1D, DoubleMatrix1D>() {

          @SuppressWarnings("synthetic-access")
          @Override
          public DoubleMatrix1D evaluate(DoubleMatrix1D x) {
            double y = function.evaluate(x);
            int n = x.getNumberOfElements();
            double[] xData = x.getData();
            double oldValue;
            double[] res = new double[n];
            for (int i = 0; i < n; i++) {
              oldValue = xData[i];
              xData[i] -= _eps;
              res[i] = (y - function.evaluate(new DoubleMatrix1D(xData))) / _eps; // x has been changed via the access to the underlying data
              xData[i] = oldValue;
            }
            return new DoubleMatrix1D(res);
          }
        };
    }
    throw new IllegalArgumentException("Can only handle forward, backward and central differencing");
  }

}
