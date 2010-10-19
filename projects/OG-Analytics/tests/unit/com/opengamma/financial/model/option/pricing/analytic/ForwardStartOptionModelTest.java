/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.option.Moneyness;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.ForwardStartOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class ForwardStartOptionModelTest {
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final double R = 0.08;
  private static final YieldCurve CURVE = new YieldCurve(ConstantDoublesCurve.from(R));
  private static final ConstantVolatilitySurface SURFACE = new ConstantVolatilitySurface(0.3);
  private static final double B = 0.04;
  private static final double SPOT = 60;
  private static final double PERCENT = 0.1;
  private static final ZonedDateTime START = DateUtil.getDateOffsetWithYearFraction(DATE, 0.25);
  private static final ZonedDateTime EXPIRY = DateUtil.getDateOffsetWithYearFraction(DATE, 1);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(CURVE, B, SURFACE, SPOT, DATE);
  private static final ForwardStartOptionDefinition FORWARD = new ForwardStartOptionDefinition(new Expiry(EXPIRY), true, new Expiry(START), PERCENT, Moneyness.OTM);
  private static final ForwardStartOptionDefinition NOW = new ForwardStartOptionDefinition(new Expiry(EXPIRY), true, new Expiry(DATE), PERCENT, Moneyness.OTM);
  private static final ForwardStartOptionDefinition END = new ForwardStartOptionDefinition(new Expiry(EXPIRY), true, new Expiry(EXPIRY), PERCENT, Moneyness.OTM);
  private static final EuropeanVanillaOptionDefinition VANILLA = new EuropeanVanillaOptionDefinition(SPOT * (1 + PERCENT), new Expiry(EXPIRY), true);
  private static final AnalyticOptionModel<ForwardStartOptionDefinition, StandardOptionDataBundle> MODEL = new ForwardStartOptionModel();
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> BSM = new BlackScholesMertonModel();

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(FORWARD).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void test() {
    assertEquals(MODEL.getPricingFunction(END).evaluate(DATA), 0, 0);
    assertEquals(MODEL.getPricingFunction(FORWARD).evaluate(DATA.withVolatilitySurface(new ConstantVolatilitySurface(1e-9))), 0, 0);
    assertEquals(MODEL.getPricingFunction(NOW).evaluate(DATA), BSM.getPricingFunction(VANILLA).evaluate(DATA), 1e-4);
    assertEquals(MODEL.getPricingFunction(FORWARD).evaluate(DATA), 4.4064, 1e-4);
  }
}
