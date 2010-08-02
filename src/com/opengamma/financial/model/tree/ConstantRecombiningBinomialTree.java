/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.tree;

import java.lang.reflect.Array;

/**
 * 
 * @param <T>
 */
public class ConstantRecombiningBinomialTree<T> extends RecombiningBinomialTree<T> {
  private final T _value;

  public ConstantRecombiningBinomialTree(final T value) {
    super(make2DArray(value));
    _value = value;
  }

  @SuppressWarnings("unchecked")
  private static <T> T[][] make2DArray(final T value) {
    final T[][] arr = (T[][]) Array.newInstance(value.getClass(), 1, 1);
    arr[0][0] = value;
    return arr;
  }

  @Override
  public T getNode(final int step, final int node) {
    return _value;
  }

}
