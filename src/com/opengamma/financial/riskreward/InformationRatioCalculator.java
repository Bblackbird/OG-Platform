/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

/**
 * The information ratio is a measure of the risk-adjusted return of an asset that is defined as the expected active return (the excess return of an asset over that a benchmark portfolio)
 * divided by the tracking error (the standard deviation of the active return).
 * {@latex.ilb %preamble{\\usepackage{amsmath}}
 * \\begin{eqnarray*}
 * IR = \\frac{E[R_i - R_b]}{\\sigma_i} = \\frac{E[R_i - R_b]}{\\sqrt{var[R_f - R_b]}}
 * \\end{eqnarray*}}  
 * where {@latex.inline $R_i$} is the asset return, {@latex.inline $R_b$} is the benchmark return and {@latex.inline $\\sigma_i$} is the standard deviation of the active return. 
 */
public class InformationRatioCalculator {

  /**
   * Calculates the information ratio
   * @param assetReturn The return of the asset
   * @param benchmarkReturn The return of the benchmark portfolio
   * @param assetStandardDeviation The standard deviation of the returns of the asset
   * @return The information ratio
   */
  public double calculate(final double assetReturn, final double benchmarkReturn, final double assetStandardDeviation) {
    return (assetReturn - benchmarkReturn) / assetStandardDeviation;
  }
}
