/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.minimization;

import static com.opengamma.math.UtilFunctions.square;
import static com.opengamma.math.matrix.MatrixAlgebraFactory.OG_ALGEBRA;

import com.opengamma.math.ConvergenceException;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.util.ArgumentChecker;

/**
 * Standard version of powell's method. It is a good general purpose minimiser if you don't have the analytic form of the functions gradient 
 */
public class ConjugateDirectionVectorMinimizer implements VectorMinimizer {

  private static final double SMALL = Double.MIN_NORMAL;
  private final double _eps;
  private final int _maxInterations;
  private final LineSearch _lineSearch;

  public ConjugateDirectionVectorMinimizer(final ScalarMinimizer minimizer) {
    ArgumentChecker.notNull(minimizer, "minimizer");
    _lineSearch = new LineSearch(minimizer);
    _eps = 1e-8;
    _maxInterations = 100;
  }

  public ConjugateDirectionVectorMinimizer(final ScalarMinimizer minimizer, double tolerance, int maxInterations) {
    ArgumentChecker.notNull(minimizer, "minimizer");
    if (tolerance < SMALL || tolerance > 1.0) {
      throw new IllegalArgumentException("Tolerance must be greater than " + SMALL + " and less than 1.0");
    }
    if (maxInterations < 1) {
      throw new IllegalArgumentException("Need at lest one interation");
    }
    _lineSearch = new LineSearch(minimizer);
    _eps = tolerance;
    _maxInterations = maxInterations;
  }

  @Override
  public DoubleMatrix1D minimize(final Function1D<DoubleMatrix1D, Double> function, final DoubleMatrix1D startPosition) {
    int n = startPosition.getNumberOfElements();
    DoubleMatrix1D[] directionSet = getDefaultDirectionSet(n);

    DoubleMatrix1D x0 = startPosition;
    for (int count = 0; count < _maxInterations; count++) {
      double delta = 0.0;
      int indexDelta = 0;
      double startValue = function.evaluate(x0);
      double f1 = startValue;
      double f2 = 0;

      DoubleMatrix1D x = x0;
      for (int i = 0; i < n; i++) {
        DoubleMatrix1D direction = directionSet[i];
        double lambda = _lineSearch.minimise(function, direction, x);
        x = (DoubleMatrix1D) OG_ALGEBRA.add(x, OG_ALGEBRA.scale(direction, lambda));
        f2 = function.evaluate(x);
        double temp = (f1 - f2); // LineSearch should return this
        if (temp > delta) {
          delta = temp;
          indexDelta = i;
        }
        f1 = f2;
      }

      if ((startValue - f2) < _eps * (Math.abs(startValue) + Math.abs(f2)) / 2.0 + SMALL) {
        return x;
      }

      DoubleMatrix1D deltaX = (DoubleMatrix1D) OG_ALGEBRA.subtract(x, x0);
      DoubleMatrix1D extrapolatedPoint = (DoubleMatrix1D) OG_ALGEBRA.add(x, deltaX);

      double extrapValue = function.evaluate(extrapolatedPoint);
      // Powell's condition for updating the direction set
      if (extrapValue < startValue && (2 * (startValue - 2 * f2 * extrapValue) * square(startValue - f2 - delta)) < (square(startValue - extrapValue) * delta)) {
        double lambda = _lineSearch.minimise(function, deltaX, x);
        x = (DoubleMatrix1D) OG_ALGEBRA.add(x, OG_ALGEBRA.scale(deltaX, lambda));
        directionSet[indexDelta] = directionSet[n - 1];
        directionSet[n - 1] = deltaX;
      }

      x0 = x;
    }
    String s = "ConjugateDirection Failed to converge after " + _maxInterations + " interations, with a tolerance of " + _eps + " Final position reached was " + x0.toString();
    throw new ConvergenceException(s);
  }

  DoubleMatrix1D[] getDefaultDirectionSet(int dim) {
    DoubleMatrix1D[] res = new DoubleMatrix1D[dim];
    for (int i = 0; i < dim; i++) {
      double[] temp = new double[dim];
      temp[i] = 1.0;
      res[i] = new DoubleMatrix1D(temp);
    }
    return res;
  }

}
