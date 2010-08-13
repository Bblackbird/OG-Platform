/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic.twoasset;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.option.definition.twoasset.EuropeanExchangeAssetOptionDefinition;
import com.opengamma.financial.model.option.definition.twoasset.StandardTwoAssetOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class EuropeanExchangeAssetOptionModelTest {
  private static final double S1 = 22;
  private static final double S2 = 20;
  private static final double Q1 = 1;
  private static final double Q2 = 1;
  private static final double B1 = 0.04;
  private static final double B2 = 0.06;
  private static final ConstantYieldCurve R = new ConstantYieldCurve(0.1);
  private static final ConstantVolatilitySurface SIGMA1 = new ConstantVolatilitySurface(0.2);
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final EuropeanExchangeAssetOptionModel MODEL = new EuropeanExchangeAssetOptionModel();
  private static final Expiry EXPIRY1 = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.1));
  private static final Expiry EXPIRY2 = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final EuropeanExchangeAssetOptionDefinition OPTION1 = new EuropeanExchangeAssetOptionDefinition(EXPIRY1, Q1, Q2);
  private static final EuropeanExchangeAssetOptionDefinition OPTION2 = new EuropeanExchangeAssetOptionDefinition(EXPIRY2, Q1, Q2);

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(new EuropeanExchangeAssetOptionDefinition(EXPIRY1, Q1, Q2)).evaluate((StandardTwoAssetOptionDataBundle) null);
  }

  @Test
  public void test() {
    final double eps = 1e-4;
    StandardTwoAssetOptionDataBundle data = new StandardTwoAssetOptionDataBundle(R, B1, B2, SIGMA1, new ConstantVolatilitySurface(0.15), S1, S2, -0.5, DATE);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.1251, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.7619, eps);
    data = data.withCorrelation(0);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.0446, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.4793, eps);
    data = data.withCorrelation(0.5);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 1.9736, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.1378, eps);
    data = data.withSecondVolatilitySurface(new ConstantVolatilitySurface(0.2));
    data = data.withCorrelation(-0.5);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.1986, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.9881, eps);
    data = data.withCorrelation(0.);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.0913, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.6496, eps);
    data = data.withCorrelation(0.5);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 1.9891, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.2306, eps);
    data = data.withSecondVolatilitySurface(new ConstantVolatilitySurface(0.25));
    data = data.withCorrelation(-0.5);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.2827, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 3.2272, eps);
    data = data.withCorrelation(0.);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.1520, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.8472, eps);
    data = data.withCorrelation(0.5);
    assertEquals(MODEL.getPricingFunction(OPTION1).evaluate(data), 2.0189, eps);
    assertEquals(MODEL.getPricingFunction(OPTION2).evaluate(data), 2.3736, eps);
  }
}
