/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.financial.timeseries.analysis.DoubleTimeSeriesStatisticsCalculator;
import com.opengamma.financial.timeseries.returns.TimeSeriesReturnCalculator;
import com.opengamma.financial.timeseries.util.TimeSeriesDataTestUtils;
import com.opengamma.util.timeseries.DoubleTimeSeries;

/**
 * The Treynor ratio is a measure of the excess return with respect to the risk-free rate per unit of systematic risk. The systematic risk is the beta of the 
 * asset or portfolio with respect to the asset.
 * <p>
 * The Treynor ratio is defined as:
 * {@latex.ilb %preamble{\\usepackage{amsmath}}
 * \\begin{eqnarray*}
 * T = \\frac{R_i - R_f}{\\beta_i}
 * \\end{eqnarray*}}   
 * where {@latex.inline $R_i$} is the asset return, {@latex.inline $R_f$} is the risk-free return and {@latex.inline $\\beta_i$} is the portfolio's beta.
 */
public class TreynorRatioCalculator {
  private static final Logger s_logger = LoggerFactory.getLogger(TreynorRatioCalculator.class);
  private final TimeSeriesReturnCalculator _assetReturnCalculator;
  private final DoubleTimeSeriesStatisticsCalculator _expectedAssetReturnCalculator;
  private final DoubleTimeSeriesStatisticsCalculator _expectedRiskFreeReturnCalculator;

  public TreynorRatioCalculator(final TimeSeriesReturnCalculator assetReturnCalculator, final DoubleTimeSeriesStatisticsCalculator expectedAssetReturnCalculator,
      final DoubleTimeSeriesStatisticsCalculator expectedRiskFreeReturnCalculator) {
    Validate.notNull(assetReturnCalculator, "asset return series calculator");
    Validate.notNull(expectedAssetReturnCalculator, "expected asset return calculator");
    Validate.notNull(expectedRiskFreeReturnCalculator, "expected risk free return calculator");
    _assetReturnCalculator = assetReturnCalculator;
    _expectedAssetReturnCalculator = expectedAssetReturnCalculator;
    _expectedRiskFreeReturnCalculator = expectedRiskFreeReturnCalculator;
  }

  /**
   * Calculates the Treynor ratio
   * @param assetPriceTS The asset price time series 
   * @param riskFreeReturnTS The risk-free return time series
   * @param beta The beta of the asset
   * @return The Treynor ratio
   */
  public double evaluate(final DoubleTimeSeries<?> assetPriceTS, final DoubleTimeSeries<?> riskFreeReturnTS, final double beta) {
    TimeSeriesDataTestUtils.testNotNullOrEmpty(assetPriceTS);
    TimeSeriesDataTestUtils.testNotNullOrEmpty(riskFreeReturnTS);
    final Double expectedAssetReturn = _expectedAssetReturnCalculator.evaluate(_assetReturnCalculator.evaluate(assetPriceTS));
    final Double expectedRiskFreeReturn = _expectedRiskFreeReturnCalculator.evaluate(riskFreeReturnTS);
    s_logger.warn(expectedAssetReturn + " " + expectedRiskFreeReturn + " " + beta);
    return (expectedAssetReturn - expectedRiskFreeReturn) / beta;
  }
}
