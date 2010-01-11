/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.timeseries.analysis;

import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.timeseries.DoubleTimeSeries;

/**
 * 
 * @author emcleod
 */
public class TurningPointIIDHypothesis extends IIDHypothesis {
  private final double _criticalValue;

  public TurningPointIIDHypothesis(final double level) {
    if (level <= 0 || level > 1)
      throw new IllegalArgumentException("Level must be between 0 and 1");
    _criticalValue = new NormalDistribution(0, 1).getInverseCDF(1 - level / 2.);
  }

  @Override
  public boolean testIID(final DoubleTimeSeries x) {
    final Double[] data = x.getValues();
    final int n = data.length;
    int t = 0;
    double x0, x1, x2;
    for (int i = 1; i < n - 1; i++) {
      x0 = data[i - 1];
      x1 = data[i];
      x2 = data[i + 1];
      if (x1 > x0 && x1 > x2) {
        t++;
      } else if (x1 < x0 && x1 < x2) {
        t++;
      }
    }
    final double mean = 2 * (n - 2.) / 3.;
    final double std = Math.sqrt((16 * n - 29.) / 90.);
    return Math.abs(t - mean) / std < _criticalValue;
  }
}
