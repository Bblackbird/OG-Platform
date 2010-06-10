/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.BatesGeneralizedJumpDiffusionModelOptionDataBundle;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class BatesGeneralizedJumpDiffusionModelTest {
  private static final AnalyticOptionModel<OptionDefinition, BatesGeneralizedJumpDiffusionModelOptionDataBundle> MODEL = new BatesGeneralizedJumpDiffusionModel();
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> BSM = new BlackScholesMertonModel();
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(0.08);
  private static final double B = 0.08;
  private static final VolatilitySurface SURFACE = new ConstantVolatilitySurface(0.25);
  private static final double SPOT = 100;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2009, 1, 1);
  private static final Expiry EXPIRY1 = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.1));
  private static final Expiry EXPIRY2 = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.25));
  private static final Expiry EXPIRY3 = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final double EPS1 = 1e-2;
  private static final double EPS2 = 1e-9;

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(new EuropeanVanillaOptionDefinition(100, EXPIRY1, true)).evaluate((BatesGeneralizedJumpDiffusionModelOptionDataBundle) null);
  }

  @Test
  public void test() {
    OptionDefinition call = new EuropeanVanillaOptionDefinition(80, EXPIRY1, true);
    BatesGeneralizedJumpDiffusionModelOptionDataBundle data = new BatesGeneralizedJumpDiffusionModelOptionDataBundle(CURVE, B, SURFACE, SPOT, DATE, 0., -0.04, 0.);
    assertEquals(BSM.getPricingFunction(call).evaluate(data), MODEL.getPricingFunction(call).evaluate(data), EPS2);
    call = new EuropeanVanillaOptionDefinition(80, EXPIRY1, true);
    data = data.withLambda(1.).withDelta(0.1);
    assertEquals(20.67, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(90, EXPIRY2, true);
    data = data.withLambda(5.);
    assertEquals(14.13, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(100, EXPIRY3, true);
    data = data.withLambda(10.);
    assertEquals(13.62, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    data = data.withDelta(0.25);
    data = data.withLambda(1.);
    call = new EuropeanVanillaOptionDefinition(90, EXPIRY1, true);
    assertEquals(11.57, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(100, EXPIRY2, true);
    data = data.withLambda(5.);
    assertEquals(12.25, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(110, EXPIRY3, true);
    data = data.withLambda(10.);
    assertEquals(20.43, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    data = data.withDelta(0.5);
    data = data.withLambda(1.);
    call = new EuropeanVanillaOptionDefinition(100, EXPIRY1, true);
    assertEquals(5.18, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(110, EXPIRY2, true);
    data = data.withLambda(5.);
    assertEquals(16.52, MODEL.getPricingFunction(call).evaluate(data), EPS1);
    call = new EuropeanVanillaOptionDefinition(120, EXPIRY3, true);
    data = data.withLambda(10.);
    assertEquals(37.03, MODEL.getPricingFunction(call).evaluate(data), EPS1);
  }
}
