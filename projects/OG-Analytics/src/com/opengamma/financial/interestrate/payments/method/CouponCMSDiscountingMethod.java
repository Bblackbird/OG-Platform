/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.payments.method;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.ParRateCalculator;
import com.opengamma.financial.interestrate.ParRateCurveSensitivityCalculator;
import com.opengamma.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.financial.interestrate.PresentValueSensitivityCalculator;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.payments.CouponCMS;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;

/**
 *  Pricing and sensitivities of a CMS coupon by discounting (no convexity adjustment).
 */
public final class CouponCMSDiscountingMethod {
  private static final CouponCMSDiscountingMethod INSTANCE = new CouponCMSDiscountingMethod();

  /**
   * @return A static instance
   */
  public static CouponCMSDiscountingMethod getInstance() {
    return INSTANCE;
  }

  private CouponCMSDiscountingMethod() {
  }

  /**
   * Compute the present value of a CMS coupon by discounting (no convexity adjustment).
   * @param cmsCoupon The CMS coupon.
   * @param curves The yield curves. Should contain the discounting and forward curves associated. 
   * @return The coupon price.
   */
  public double presentValue(final CouponCMS cmsCoupon, final YieldCurveBundle curves) {
    Validate.notNull(cmsCoupon);
    Validate.notNull(curves);
    final ParRateCalculator parRate = ParRateCalculator.getInstance();
    final double swapRate = parRate.visitFixedCouponSwap(cmsCoupon.getUnderlyingSwap(), curves);
    final YieldAndDiscountCurve fundingCurve = curves.getCurve(cmsCoupon.getFundingCurveName());
    final double paymentDiscountFactor = fundingCurve.getDiscountFactor(cmsCoupon.getPaymentTime());
    final double pv = swapRate * cmsCoupon.getPaymentYearFraction() * cmsCoupon.getNotional() * paymentDiscountFactor;
    return pv;
  }

  /**
   * Compute the present value sensitivity to the yield curves of a CMS coupon by discounting (no convexity adjustment).
   * @param cmsCoupon The CMS coupon.
   * @param curves The yield curves. Should contain the discounting and forward curves associated. 
   * @return The present value curve sensitivity.
   */
  public InterestRateCurveSensitivity presentValueSensitivity(final CouponCMS cmsCoupon, final YieldCurveBundle curves) {
    Validate.notNull(cmsCoupon);
    Validate.notNull(curves);
    final ParRateCalculator parRateCal = ParRateCalculator.getInstance();
    final double swapRate = parRateCal.visitFixedCouponSwap(cmsCoupon.getUnderlyingSwap(), curves);
    final String fundingCurveName = cmsCoupon.getFundingCurveName();
    final YieldAndDiscountCurve fundingCurve = curves.getCurve(fundingCurveName);
    final double paymentTime = cmsCoupon.getPaymentTime();
    final double paymentDiscountFactor = fundingCurve.getDiscountFactor(paymentTime);
    final ParRateCurveSensitivityCalculator parRateSensCal = ParRateCurveSensitivityCalculator.getInstance();
    final InterestRateCurveSensitivity swapRateSens = new InterestRateCurveSensitivity(parRateSensCal.visit(cmsCoupon.getUnderlyingSwap(), curves));
    final InterestRateCurveSensitivity payDFSens = new InterestRateCurveSensitivity(PresentValueSensitivityCalculator.discountFactorSensitivity(fundingCurveName, fundingCurve, paymentTime));
    InterestRateCurveSensitivity result = swapRateSens.multiply(paymentDiscountFactor);
    result = result.add(payDFSens.multiply(swapRate));
    result = result.multiply(cmsCoupon.getNotional() * cmsCoupon.getPaymentYearFraction());
    return result;

  }
}
