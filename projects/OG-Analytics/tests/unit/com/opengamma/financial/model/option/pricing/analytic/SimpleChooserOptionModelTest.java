/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.SimpleChooserOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class SimpleChooserOptionModelTest {
  private static final double EPS = 1e-4;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2009, 1, 1);
  private static final Expiry CHOOSE_DATE = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.25));
  private static final Expiry UNDERLYING_EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final double STRIKE = 50;
  private static final double SPOT = 50;
  private static final SimpleChooserOptionDefinition DEFINITION = new SimpleChooserOptionDefinition(CHOOSE_DATE, STRIKE, UNDERLYING_EXPIRY);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(new ConstantYieldCurve(0.08), 0.08, new ConstantVolatilitySurface(0.25), SPOT, DATE);
  private static final AnalyticOptionModel<SimpleChooserOptionDefinition, StandardOptionDataBundle> MODEL = new SimpleChooserOptionModel();
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> BSM = new BlackScholesMertonModel();

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(DEFINITION).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void test() {
    double strike = SPOT;
    final StandardOptionDataBundle data = DATA.withVolatilitySurface(new ConstantVolatilitySurface(0));
    SimpleChooserOptionDefinition chooser = new SimpleChooserOptionDefinition(new Expiry(DATE), strike, UNDERLYING_EXPIRY);
    OptionDefinition vanilla = new EuropeanVanillaOptionDefinition(strike, UNDERLYING_EXPIRY, true);
    assertEquals(MODEL.getPricingFunction(chooser).evaluate(DATA), BSM.getPricingFunction(vanilla).evaluate(DATA), 1e-9);
    strike = SPOT / 2;
    chooser = new SimpleChooserOptionDefinition(new Expiry(DATE), strike, UNDERLYING_EXPIRY);
    vanilla = new EuropeanVanillaOptionDefinition(strike, UNDERLYING_EXPIRY, true);
    assertEquals(MODEL.getPricingFunction(chooser).evaluate(DATA), BSM.getPricingFunction(vanilla).evaluate(DATA), 1e-9);
    chooser = new SimpleChooserOptionDefinition(CHOOSE_DATE, strike, UNDERLYING_EXPIRY);
    assertEquals(MODEL.getPricingFunction(chooser).evaluate(data), BSM.getPricingFunction(vanilla).evaluate(data), 1e-9);
    strike = SPOT * 2;
    chooser = new SimpleChooserOptionDefinition(new Expiry(DATE), strike, UNDERLYING_EXPIRY);
    vanilla = new EuropeanVanillaOptionDefinition(strike, UNDERLYING_EXPIRY, false);
    assertEquals(MODEL.getPricingFunction(chooser).evaluate(DATA), BSM.getPricingFunction(vanilla).evaluate(DATA), 1e-9);
    chooser = new SimpleChooserOptionDefinition(CHOOSE_DATE, strike, UNDERLYING_EXPIRY);
    assertEquals(MODEL.getPricingFunction(chooser).evaluate(data), BSM.getPricingFunction(vanilla).evaluate(data), 1e-9);
    assertEquals(MODEL.getGreeks(DEFINITION, DATA, Sets.newHashSet(Greek.FAIR_PRICE)).get(Greek.FAIR_PRICE), 6.1071, EPS);
  }
}
