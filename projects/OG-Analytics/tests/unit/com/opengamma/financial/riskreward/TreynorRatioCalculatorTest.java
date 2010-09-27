/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.timeseries.analysis.DoubleTimeSeriesStatisticsCalculator;
import com.opengamma.financial.timeseries.returns.TimeSeriesReturnCalculator;
import com.opengamma.math.function.Function;
import com.opengamma.util.CalculationMode;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.fast.DateTimeNumericEncoding;
import com.opengamma.util.timeseries.fast.longint.FastArrayLongDoubleTimeSeries;

/**
 * 
 */
public class TreynorRatioCalculatorTest {
  private static final long[] T = new long[] {1};
  private static final double BETA = 0.7;
  private static final DoubleTimeSeries<?> ASSET_RETURN = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.12});
  private static final DoubleTimeSeries<?> RISK_FREE = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.03});
  private static final DoubleTimeSeriesStatisticsCalculator EXPECTED_RETURN = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return x[0][0];
    }

  });
  private static final TimeSeriesReturnCalculator RETURN = new TimeSeriesReturnCalculator(CalculationMode.LENIENT) {

    @Override
    public DoubleTimeSeries<?> evaluate(final DoubleTimeSeries<?>... x) {
      return x[0];
    }
  };
  private static final TreynorRatioCalculator TREYNOR = new TreynorRatioCalculator(RETURN, EXPECTED_RETURN, EXPECTED_RETURN);

  @Test(expected = IllegalArgumentException.class)
  public void testNullReturnCalculator() {
    new TreynorRatioCalculator(null, EXPECTED_RETURN, EXPECTED_RETURN);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullAssetReturnCalculator() {
    new TreynorRatioCalculator(RETURN, null, EXPECTED_RETURN);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullRiskFreeReturnCalculator() {
    new TreynorRatioCalculator(RETURN, EXPECTED_RETURN, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullTS1() {
    TREYNOR.evaluate(null, RISK_FREE, BETA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullTS2() {
    TREYNOR.evaluate(ASSET_RETURN, null, BETA);
  }

  @Test
  public void test() {
    assertEquals(TREYNOR.evaluate(ASSET_RETURN, RISK_FREE, BETA), 0.1286, 1e-4);
  }
}
