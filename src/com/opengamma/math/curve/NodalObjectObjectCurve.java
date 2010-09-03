/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.curve;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.opengamma.util.tuple.Pair;

/**
 * 
 * @param <T> Type of the x data
 * @param <U> Type of the y data
 */
public class NodalObjectObjectCurve<T extends Comparable<T>, U> extends ObjectObjectCurve<T, U> {

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final T[] xData, final U[] yData) {
    return new NodalObjectObjectCurve<T, U>(xData, yData, false);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final Map<T, U> data) {
    return new NodalObjectObjectCurve<T, U>(data, false);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final Set<Pair<T, U>> data) {
    return new NodalObjectObjectCurve<T, U>(data, false);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final T[] xData, final U[] yData, final String name) {
    return new NodalObjectObjectCurve<T, U>(xData, yData, false, name);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final Map<T, U> data, final String name) {
    return new NodalObjectObjectCurve<T, U>(data, false, name);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> of(final Set<Pair<T, U>> data, final String name) {
    return new NodalObjectObjectCurve<T, U>(data, false, name);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final T[] xData, final U[] yData) {
    return new NodalObjectObjectCurve<T, U>(xData, yData, true);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final Map<T, U> data) {
    return new NodalObjectObjectCurve<T, U>(data, true);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final Set<Pair<T, U>> data) {
    return new NodalObjectObjectCurve<T, U>(data, true);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final T[] xData, final U[] yData, final String name) {
    return new NodalObjectObjectCurve<T, U>(xData, yData, true, name);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final Map<T, U> data, final String name) {
    return new NodalObjectObjectCurve<T, U>(data, true, name);
  }

  public static <T extends Comparable<T>, U> NodalObjectObjectCurve<T, U> ofSorted(final Set<Pair<T, U>> data, final String name) {
    return new NodalObjectObjectCurve<T, U>(data, true, name);
  }

  public NodalObjectObjectCurve(final T[] xData, final U[] yData, final boolean isSorted) {
    super(xData, yData, isSorted);
  }

  public NodalObjectObjectCurve(final Map<T, U> data, final boolean isSorted) {
    super(data, isSorted);
  }

  public NodalObjectObjectCurve(final Set<Pair<T, U>> data, final boolean isSorted) {
    super(data, isSorted);
  }

  public NodalObjectObjectCurve(final T[] xData, final U[] yData, final boolean isSorted, final String name) {
    super(xData, yData, isSorted, name);
  }

  public NodalObjectObjectCurve(final Map<T, U> data, final boolean isSorted, final String name) {
    super(data, isSorted, name);
  }

  public NodalObjectObjectCurve(final Set<Pair<T, U>> data, final boolean isSorted, final String name) {
    super(data, isSorted, name);
  }

  @Override
  public U getYValue(final T x) {
    Validate.notNull(x, "x");
    final int index = Arrays.binarySearch(getXData(), x);
    if (index < 0) {
      throw new IllegalArgumentException("Curve does not contain data for x point " + x);
    }
    return getYData()[index];
  }

}
