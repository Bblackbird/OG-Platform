/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.future.calculator;

import com.opengamma.analytics.financial.instrument.index.IndexON;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.interestrate.future.derivative.FederalFundsFutureSecurity;
import com.opengamma.analytics.financial.provider.description.interestrate.ParameterProviderInterface;
import com.opengamma.util.ArgumentChecker;

/**
 * Computes the price for different types of futures. Calculator using a multi-curve provider.
 */
public final class FuturesPriceMulticurveCalculator extends InstrumentDerivativeVisitorAdapter<ParameterProviderInterface, Double> {

  /**
   * The unique instance of the calculator.
   */
  private static final FuturesPriceMulticurveCalculator INSTANCE = new FuturesPriceMulticurveCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static FuturesPriceMulticurveCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private FuturesPriceMulticurveCalculator() {
  }

  //     -----     Futures     -----

  @Override
  public Double visitFederalFundsFutureSecurity(final FederalFundsFutureSecurity futures, final ParameterProviderInterface multicurve) {
    ArgumentChecker.notNull(futures, "futures");
    ArgumentChecker.notNull(multicurve, "multi-curve provider");
    final IndexON index = futures.getIndex();
    final int nbFixing = futures.getFixingPeriodAccrualFactor().length;
    final double[] rates = new double[nbFixing];
    for (int loopfix = 0; loopfix < nbFixing; loopfix++) {
      rates[loopfix] = multicurve.getMulticurveProvider().getForwardRate(index, futures.getFixingPeriodTime()[loopfix], futures.getFixingPeriodTime()[loopfix + 1],
          futures.getFixingPeriodAccrualFactor()[loopfix]);
    }
    double interest = futures.getAccruedInterest();
    for (int loopfix = 0; loopfix < nbFixing; loopfix++) {
      interest += rates[loopfix] * futures.getFixingPeriodAccrualFactor()[loopfix];
    }
    return 1.0 - interest / futures.getFixingTotalAccrualFactor();
  }

}
