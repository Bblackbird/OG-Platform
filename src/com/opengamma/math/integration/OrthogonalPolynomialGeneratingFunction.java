/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

/**
 * 
 * @author emcleod
 */
public abstract class OrthogonalPolynomialGeneratingFunction implements GeneratingFunction<Double, GaussianQuadratureFunction> {
  private final int _maxIter;

  public OrthogonalPolynomialGeneratingFunction() {
    this(10000);
  }

  public OrthogonalPolynomialGeneratingFunction(final int maxIter) {
    _maxIter = maxIter;
  }

  protected int getMaxIterations() {
    return _maxIter;
  }
}
