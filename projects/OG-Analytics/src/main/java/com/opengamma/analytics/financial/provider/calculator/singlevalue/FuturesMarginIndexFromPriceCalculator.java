/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.singlevalue;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorAdapter;
import com.opengamma.analytics.financial.interestrate.future.derivative.YieldAverageBondFuturesSecurity;

/**
 * Computes the par rate for different instrument. The meaning of "par rate" is instrument dependent.
 */
public final class FuturesMarginIndexFromPriceCalculator extends InstrumentDerivativeVisitorAdapter<Double, Double> {

  /**
   * The unique instance of the calculator.
   */
  private static final FuturesMarginIndexFromPriceCalculator INSTANCE = new FuturesMarginIndexFromPriceCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static FuturesMarginIndexFromPriceCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private FuturesMarginIndexFromPriceCalculator() {
  }

  //     -----     Futures     -----

  @Override
  public Double visitYieldAverageBondFuturesSecurity(final YieldAverageBondFuturesSecurity futures, final Double quotedPrice) {
    final double yield = 1.0d - quotedPrice;
    final double dirtyPrice = dirtyPriceFromYield(yield, futures.getCouponRate(), futures.getTenor(), futures.getNumberCouponPerYear());
    return dirtyPrice * futures.getNotional();
  }

  /**
   * The dirty price from the standard yield.
   * @param yield The yield
   * @param coupon The coupon
   * @param tenor The tenor (in year)
   * @param couponPerYear Number of coupon per year.
   * @return The price.
   */
  private double dirtyPriceFromYield(final double yield, final double coupon, final int tenor, final int couponPerYear) {
    final double v = 1.0d + yield / couponPerYear;
    final int n = tenor * couponPerYear;
    final double vn = Math.pow(v, -n);
    return coupon / yield * (1 - vn) + vn;
  }

}
