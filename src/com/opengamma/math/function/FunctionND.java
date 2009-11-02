/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.function;

/**
 * 
 * @author emcleod
 * 
 * @param <S>
 * @param <T>
 */
public abstract class FunctionND<S, T> implements Function<S, T> {
  private final int _dimension;

  public FunctionND(final int dimension) {
    _dimension = dimension;
  }

  public T evaluate(final S... x) {
    if (x.length != _dimension)
      throw new IllegalArgumentException("Number of variables " + x.length + " does not match dimension of function " + _dimension);
    return evaluateFunction(x);
  }

  public int getDimension() {
    return _dimension;
  }

  protected abstract T evaluateFunction(S[] x);

}
