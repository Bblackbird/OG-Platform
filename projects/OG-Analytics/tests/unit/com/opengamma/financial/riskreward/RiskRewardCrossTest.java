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
public class RiskRewardCrossTest {
  private static final double ASSET_STANDARD_DEVIATION = 0.24;
  private static final double MARKET_STANDARD_DEVIATION = 0.17;
  private static final TimeSeriesReturnCalculator RETURN_CALCULATOR = new TimeSeriesReturnCalculator(CalculationMode.LENIENT) {

    @Override
    public DoubleTimeSeries<?> evaluate(final DoubleTimeSeries<?>... x) {
      return x[0];
    }
  };
  private static final DoubleTimeSeriesStatisticsCalculator CALCULATOR = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return x[0][0];
    }

  });
  private static final DoubleTimeSeriesStatisticsCalculator STD_ASSET = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return ASSET_STANDARD_DEVIATION;
    }

  });
  private static final DoubleTimeSeriesStatisticsCalculator STD_MARKET = new DoubleTimeSeriesStatisticsCalculator(new Function<double[], Double>() {

    @Override
    public Double evaluate(final double[]... x) {
      return MARKET_STANDARD_DEVIATION;
    }

  });
  private static final double PERIODS_PER_YEAR = 1;
  private static final long[] T = new long[] {1};
  private static final DoubleTimeSeries<?> RISK_FREE_TS = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.03});
  private static final DoubleTimeSeries<?> ASSET_RETURN_TS = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.174});
  private static final DoubleTimeSeries<?> MARKET_RETURN_TS = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.11});
  private static final DoubleTimeSeries<?> RISK_FREE_RETURN_TS = new FastArrayLongDoubleTimeSeries(DateTimeNumericEncoding.DATE_EPOCH_DAYS, T, new double[] {0.03});
  private static final double RISK_FREE_RETURN = 0.03;
  private static final double ASSET_RETURN = 0.174;
  private static final double MARKET_RETURN = 0.11;
  private static final double BETA = 1.3;
  private static final TotalRiskAlphaCalculator TRA = new TotalRiskAlphaCalculator();
  private static final SharpeRatioCalculator SHARPE_ASSET = new SharpeRatioCalculator(PERIODS_PER_YEAR, CALCULATOR, STD_ASSET);
  private static final SharpeRatioCalculator SHARPE_MARKET = new SharpeRatioCalculator(PERIODS_PER_YEAR, CALCULATOR, STD_MARKET);
  private static final RiskAdjustedPerformanceCalculator RAP = new RiskAdjustedPerformanceCalculator();
  private static final MTwoPerformanceCalculator M2 = new MTwoPerformanceCalculator();
  private static final TreynorRatioCalculator TREYNOR = new TreynorRatioCalculator(RETURN_CALCULATOR, CALCULATOR, CALCULATOR);
  private static final JensenAlphaCalculator JENSEN = new JensenAlphaCalculator();
  private static final MarketRiskAdjustedPerformanceCalculator MRAP = new MarketRiskAdjustedPerformanceCalculator();
  private static final TTwoPerformanceCalculator T2 = new TTwoPerformanceCalculator();
  private static final double EPS = 1e-15;

  @Test
  public void testTRAAndSharpeRatio() {
    final double tra = TRA.calculate(ASSET_RETURN, RISK_FREE_RETURN, MARKET_RETURN, ASSET_STANDARD_DEVIATION, MARKET_STANDARD_DEVIATION);
    final double srAsset = SHARPE_ASSET.evaluate(ASSET_RETURN_TS, RISK_FREE_TS);
    final double srMarket = SHARPE_MARKET.evaluate(MARKET_RETURN_TS, RISK_FREE_TS);
    assertEquals(tra, ASSET_STANDARD_DEVIATION * (srAsset - srMarket), EPS);
  }

  @Test
  public void testRAPAndSharpeRatio() {
    final double rap = RAP.calculate(ASSET_RETURN, RISK_FREE_RETURN, ASSET_STANDARD_DEVIATION, MARKET_STANDARD_DEVIATION);
    final double srAsset = SHARPE_ASSET.evaluate(ASSET_RETURN_TS, RISK_FREE_TS);
    assertEquals(rap, MARKET_STANDARD_DEVIATION * srAsset + RISK_FREE_RETURN, EPS);
  }

  @Test
  public void testM2TRAAndSharpeRatio() {
    final double tra = TRA.calculate(ASSET_RETURN, RISK_FREE_RETURN, MARKET_RETURN, ASSET_STANDARD_DEVIATION, MARKET_STANDARD_DEVIATION);
    final double srAsset = SHARPE_ASSET.evaluate(ASSET_RETURN_TS, RISK_FREE_TS);
    final double srMarket = SHARPE_MARKET.evaluate(MARKET_RETURN_TS, RISK_FREE_TS);
    final double m2 = M2.calculate(ASSET_RETURN, RISK_FREE_RETURN, MARKET_RETURN, ASSET_STANDARD_DEVIATION, MARKET_STANDARD_DEVIATION);
    assertEquals(m2, tra * MARKET_STANDARD_DEVIATION / ASSET_STANDARD_DEVIATION, EPS);
    assertEquals(m2, MARKET_STANDARD_DEVIATION * (srAsset - srMarket), EPS);
  }

  @Test
  public void testJensenAndTreynor() {
    final double ja = JENSEN.calculate(ASSET_RETURN, RISK_FREE_RETURN, BETA, MARKET_RETURN);
    final double trAsset = TREYNOR.evaluate(ASSET_RETURN_TS, RISK_FREE_RETURN_TS, BETA);
    final double trMarket = TREYNOR.evaluate(MARKET_RETURN_TS, RISK_FREE_RETURN_TS, 1);
    assertEquals(trAsset, ja / BETA + trMarket, EPS);
  }

  @Test
  public void testMRAPAndTreynor() {
    final double mrap = MRAP.calculate(ASSET_RETURN, RISK_FREE_RETURN, BETA);
    final double trAsset = TREYNOR.evaluate(ASSET_RETURN_TS, RISK_FREE_RETURN_TS, BETA);
    assertEquals(mrap, trAsset + RISK_FREE_RETURN, EPS);
  }

  @Test
  public void testT2AndJensen() {
    final double ja = JENSEN.calculate(ASSET_RETURN, RISK_FREE_RETURN, BETA, MARKET_RETURN);
    final double t2 = T2.calculate(ASSET_RETURN, RISK_FREE_RETURN, MARKET_RETURN, BETA);
    assertEquals(t2, ja / BETA, EPS);
  }
}
