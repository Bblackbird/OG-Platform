/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.timeseries.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;

import com.opengamma.financial.timeseries.analysis.AutocorrelationFunctionCalculator;
import com.opengamma.financial.timeseries.analysis.DoubleTimeSeriesStatisticsCalculator;
import com.opengamma.math.statistics.descriptive.MeanCalculator;
import com.opengamma.math.statistics.descriptive.SampleVarianceCalculator;
import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.util.timeseries.DoubleTimeSeries;

/**
 * 
 * @author emcleod
 */
public class MovingAverageTimeSeriesModelTest {
  private static final double MEAN = 0;
  private static final double STD = 0.25;
  private static final MovingAverageTimeSeriesModel MODEL = new MovingAverageTimeSeriesModel(new NormalDistribution(MEAN, STD,
      new MersenneTwister64(MersenneTwister64.DEFAULT_SEED)));
  private static final int ORDER = 2;
  private static final DoubleTimeSeries<Long> MA;
  private static final double[] THETA;
  private static double LIMIT = 3;

  static {
    final int n = 20000;
    final long[] dates = new long[n];
    for (int i = 0; i < n; i++) {
      dates[i] = i;
    }
    THETA = new double[ORDER + 1];
    THETA[0] = 0.;
    for (int i = 1; i <= ORDER; i++) {
      THETA[i] = (i + 1) / 10.;
    }
    MA = MODEL.getSeries(THETA, ORDER, dates);
    LIMIT /= Math.sqrt(n);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadConstructor() {
    new MovingAverageTimeSeriesModel(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullThetas() {
    MODEL.getSeries(null, 2, new long[] { 1 });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyThetas() {
    MODEL.getSeries(new double[0], 2, new long[] { 1 });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeOrder() {
    MODEL.getSeries(new double[] { 0.2 }, -3, new long[] { 1 });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsufficientThetas() {
    MODEL.getSeries(new double[] { 0.2 }, 4, new long[] { 1 });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDates() {
    MODEL.getSeries(new double[] { 0.3 }, 1, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyDates() {
    MODEL.getSeries(new double[] { 0.3 }, 1, new long[0]);
  }

  @Test
  public void testACF() {
    final double eps = 1e-2;
    final Double[] rho = new AutocorrelationFunctionCalculator().evaluate(MA);
    assertEquals(rho[0], 1, 1e-16);
    final double denom = 1 + THETA[1] * THETA[1] + THETA[2] * THETA[2];
    assertEquals(rho[1], (THETA[1] * THETA[2] + THETA[1]) / denom, eps);
    assertEquals(rho[2], THETA[2] / denom, eps);
    for (int i = 1; i <= 20; i++) {
      if (i < ORDER + 1) {
        assertTrue(rho[i] > LIMIT);
      } else {
        assertTrue(rho[i] < LIMIT);
      }
    }
    final Double mean = new DoubleTimeSeriesStatisticsCalculator(new MeanCalculator()).evaluate(MA);
    assertEquals(mean, THETA[0], eps);
    final Double variance = new DoubleTimeSeriesStatisticsCalculator(new SampleVarianceCalculator()).evaluate(MA);
    double sum = 1;
    for (int i = 1; i <= ORDER; i++) {
      sum += THETA[i] * THETA[i];
    }
    assertEquals(variance, sum * STD * STD, eps);
  }
}
