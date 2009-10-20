/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.interpolation;

import com.opengamma.math.function.Function1D;

/**
 * 
 * @author emcleod
 */
public class ThinPlateSplineRadialBasisFunction extends Function1D<Double, Double> {
  private final double _scaleFactor;

  public ThinPlateSplineRadialBasisFunction() {
    _scaleFactor = 1;
  }

  public ThinPlateSplineRadialBasisFunction(final double scaleFactor) {
    if (scaleFactor <= 0)
      throw new IllegalArgumentException("Scale factor must be greater than zero");
    _scaleFactor = scaleFactor;
  }

  @Override
  public Double evaluate(final Double x) {
    return x * x * Math.log(x / _scaleFactor);
  }

}
