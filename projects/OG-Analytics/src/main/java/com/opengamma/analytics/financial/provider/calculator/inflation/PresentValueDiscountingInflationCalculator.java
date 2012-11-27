/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.calculator.inflation;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitorSameMethodAdapter;
import com.opengamma.analytics.financial.interestrate.annuity.derivative.Annuity;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationZeroCouponInterpolation;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationZeroCouponInterpolationGearing;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationZeroCouponMonthly;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationZeroCouponMonthlyGearing;
import com.opengamma.analytics.financial.interestrate.inflation.method.CouponInflationZeroCouponInterpolationDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.inflation.method.CouponInflationZeroCouponInterpolationGearingDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.inflation.method.CouponInflationZeroCouponMonthlyDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.inflation.method.CouponInflationZeroCouponMonthlyGearingDiscountingMethod;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Payment;
import com.opengamma.analytics.financial.provider.calculator.discounting.PresentValueDiscountingCalculator;
import com.opengamma.analytics.financial.provider.description.InflationProviderInterface;
import com.opengamma.util.money.MultipleCurrencyAmount;

/**
 * Calculates the present value of an inflation instruments by discounting for a given MarketBundle
 */
public final class PresentValueDiscountingInflationCalculator extends InstrumentDerivativeVisitorSameMethodAdapter<InflationProviderInterface, MultipleCurrencyAmount> {

  /**
   * The unique instance of the calculator.
   */
  private static final PresentValueDiscountingInflationCalculator INSTANCE = new PresentValueDiscountingInflationCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static PresentValueDiscountingInflationCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private PresentValueDiscountingInflationCalculator() {
  }

  /**
   * Pricing method for zero-coupon with monthly reference index.
   */
  private static final CouponInflationZeroCouponMonthlyDiscountingMethod METHOD_ZC_MONTHLY = new CouponInflationZeroCouponMonthlyDiscountingMethod();
  /**
   * Pricing method for zero-coupon with interpolated reference index.
   */
  private static final CouponInflationZeroCouponInterpolationDiscountingMethod METHOD_ZC_INTERPOLATION = new CouponInflationZeroCouponInterpolationDiscountingMethod();
  /**
   * Pricing method for zero-coupon with monthly reference index.
   */
  private static final CouponInflationZeroCouponMonthlyGearingDiscountingMethod METHOD_ZC_MONTHLY_GEARING = new CouponInflationZeroCouponMonthlyGearingDiscountingMethod();
  /**
   * Pricing method for zero-coupon with interpolated reference index.
   */
  private static final CouponInflationZeroCouponInterpolationGearingDiscountingMethod METHOD_ZC_INTERPOLATION_GEARING = new CouponInflationZeroCouponInterpolationGearingDiscountingMethod();

  private static final PresentValueDiscountingCalculator PVDC = PresentValueDiscountingCalculator.getInstance();

  @Override
  public MultipleCurrencyAmount visit(final InstrumentDerivative derivative, final InflationProviderInterface multicurves) {
    try {
      return derivative.accept(this, multicurves);
    } catch (final Exception e) {
      return derivative.accept(PVDC, multicurves.getMulticurveProvider());
    }
  }

  @Override
  public MultipleCurrencyAmount visitGenericAnnuity(final Annuity<? extends Payment> annuity, final InflationProviderInterface market) {
    Validate.notNull(annuity);
    MultipleCurrencyAmount pv = MultipleCurrencyAmount.of(annuity.getCurrency(), 0.0);
    for (final Payment p : annuity.getPayments()) {
      pv = pv.plus(p.accept(this, market));
    }
    return pv;
  }

  @Override
  public MultipleCurrencyAmount visitCouponInflationZeroCouponMonthly(final CouponInflationZeroCouponMonthly coupon, final InflationProviderInterface market) {
    return METHOD_ZC_MONTHLY.presentValue(coupon, market);
  }

  @Override
  public MultipleCurrencyAmount visitCouponInflationZeroCouponInterpolation(final CouponInflationZeroCouponInterpolation coupon, final InflationProviderInterface market) {
    return METHOD_ZC_INTERPOLATION.presentValue(coupon, market);
  }

  @Override
  public MultipleCurrencyAmount visitCouponInflationZeroCouponMonthlyGearing(final CouponInflationZeroCouponMonthlyGearing coupon, final InflationProviderInterface market) {
    return METHOD_ZC_MONTHLY_GEARING.presentValue(coupon, market);
  }

  @Override
  public MultipleCurrencyAmount visitCouponInflationZeroCouponInterpolationGearing(final CouponInflationZeroCouponInterpolationGearing coupon, final InflationProviderInterface market) {
    return METHOD_ZC_INTERPOLATION_GEARING.presentValue(coupon, market);
  }

  @Override
  public MultipleCurrencyAmount visit(final InstrumentDerivative derivative) {
    throw new UnsupportedOperationException();
  }

}
