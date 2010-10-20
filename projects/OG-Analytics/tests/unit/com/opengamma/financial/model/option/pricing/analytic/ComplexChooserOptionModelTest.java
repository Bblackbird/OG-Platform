/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.option.definition.ComplexChooserOptionDefinition;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class ComplexChooserOptionModelTest {
  private static final YieldAndDiscountCurve CURVE = new YieldCurve(ConstantDoublesCurve.from(0.1));
  private static final double B = 0.05;
  private static final VolatilitySurface SURFACE = new ConstantVolatilitySurface(0.35);
  private static final double SPOT = 50;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final double CHOOSE_TIME = 0.25;
  private static final double CALL_LIFE = 0.5;
  private static final double PUT_LIFE = 7. / 12;
  private static final Expiry CHOOSE_DATE = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, CHOOSE_TIME));
  private static final Expiry CALL_EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, CALL_LIFE));
  private static final Expiry PUT_EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, PUT_LIFE));
  private static final double CALL_STRIKE = 55;
  private static final double PUT_STRIKE = 48;
  @SuppressWarnings("unused")
  private static final OptionDefinition CALL = new EuropeanVanillaOptionDefinition(CALL_STRIKE, CALL_EXPIRY, true);
  @SuppressWarnings("unused")
  private static final OptionDefinition PUT = new EuropeanVanillaOptionDefinition(PUT_STRIKE, PUT_EXPIRY, false);
  private static final ComplexChooserOptionDefinition CHOOSER = new ComplexChooserOptionDefinition(CHOOSE_DATE, CALL_STRIKE, CALL_EXPIRY, PUT_STRIKE, PUT_EXPIRY);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(CURVE, B, SURFACE, SPOT, DATE);
  private static final AnalyticOptionModel<ComplexChooserOptionDefinition, StandardOptionDataBundle> MODEL = new ComplexChooserOptionModel();
  @SuppressWarnings("unused")
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> BSM = new BlackScholesMertonModel();

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(CHOOSER).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void test() {
    // TODO test wrt BSM
    // final double spot = 2;
    // final StandardOptionDataBundle data = DATA.withSpot(spot);
    // final ComplexChooserOptionDefinition chooser = new ComplexChooserOptionDefinition(new Expiry(DATE), CALL_STRIKE, CALL_EXPIRY, PUT_STRIKE, PUT_EXPIRY);
    // assertEquals(MODEL.getPricingFunction(chooser).evaluate(data), BSM.getPricingFunction(
    // new EuropeanVanillaOptionDefinition(CALL_STRIKE, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, CALL_LIFE)), true)).evaluate(data), 0);
    assertEquals(MODEL.getPricingFunction(CHOOSER).evaluate(DATA), 6.0508, 1e-4);
  }
}
