/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.surface;

import java.util.HashMap;
import java.util.Map;

import com.opengamma.math.interpolation.Interpolator2D;
import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class VolatilityInterpolator2D extends Interpolator2D {
  private final Interpolator2D _interpolator;

  public VolatilityInterpolator2D(final Interpolator2D interpolator) {
    _interpolator = interpolator;
  }

  @Override
  public Double interpolate(final Map<DoublesPair, Double> data, final DoublesPair value) {
    final Map<DoublesPair, Double> variances = new HashMap<DoublesPair, Double>();
    for (final Map.Entry<DoublesPair, Double> entry : data.entrySet()) {
      variances.put(entry.getKey(), entry.getValue() * entry.getValue());
    }
    return Math.sqrt(_interpolator.interpolate(variances, value));
  }

}
