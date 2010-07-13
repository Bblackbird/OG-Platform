/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.function.special;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;

/**
 * 
 */
public class HeavisideFunction extends Function1D<Double, Double> {

  @Override
  public Double evaluate(final Double x) {
    Validate.notNull(x);
    if (x < 0) {
      return 0.;
    }
    return 1.;
  }

}
