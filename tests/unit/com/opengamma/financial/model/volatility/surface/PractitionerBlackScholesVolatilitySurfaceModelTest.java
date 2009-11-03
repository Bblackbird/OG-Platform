/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.surface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantInterestRateDiscountCurve;
import com.opengamma.financial.model.interestrate.curve.DiscountCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.pricing.analytic.AnalyticOptionModel;
import com.opengamma.financial.model.option.pricing.analytic.BlackScholesMertonModel;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 * @author emcleod
 */
public class PractitionerBlackScholesVolatilitySurfaceModelTest {
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> BSM = new BlackScholesMertonModel();
  private static final VolatilitySurfaceModel<OptionDefinition, StandardOptionDataBundle> MODEL = new PractitionerBlackScholesVolatilitySurfaceModel(false);
  private static final DiscountCurve CURVE = new ConstantInterestRateDiscountCurve(0.04);
  private static final double B = 0.03;
  private static final double SPOT = 100;
  private static final boolean IS_CALL = true;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2009, 1, 1);
  private static final Expiry[] EXPIRY = new Expiry[] { new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.25)),
      new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5)), new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.75)),
      new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 1)) };
  private static final double[] OFFSET = new double[] { 0.05, 0.1, 0.125 };
  private static final Expiry[] TEST_EXPIRY = new Expiry[] { new Expiry(DateUtil.getDateOffsetWithYearFraction(EXPIRY[0].getExpiry(), OFFSET[0])),
      new Expiry(DateUtil.getDateOffsetWithYearFraction(EXPIRY[1].getExpiry(), OFFSET[1])), new Expiry(DateUtil.getDateOffsetWithYearFraction(EXPIRY[2].getExpiry(), OFFSET[2])) };
  private static final double[] STRIKE = new double[] { 80, 86, 100, 101, 110 };
  private static final double[] TEST_STRIKE = new double[] { 85, 95, 104 };
  private static final double EPS = 1e-9;

  @Test(expected = IllegalArgumentException.class)
  public void testPriceInput() {
    MODEL.getSurface(null, new StandardOptionDataBundle(null, null, null, null, null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDataInput() {
    MODEL.getSurface(Collections.<OptionDefinition, Double> emptyMap(), null);
  }

  public void testFlatSurface() {
    final Map<OptionDefinition, Double> prices = new HashMap<OptionDefinition, Double>();
    final double sigma = 0.3;
    OptionDefinition definition;
    final StandardOptionDataBundle data = new StandardOptionDataBundle(CURVE, B, new ConstantVolatilitySurface(sigma), SPOT, DATE);
    try {
      MODEL.getSurface(prices, data);
      fail();
    } catch (final IllegalArgumentException e) {
      // Expected
    }
    for (final Expiry expiry : EXPIRY) {
      for (final double strike : STRIKE) {
        definition = new EuropeanVanillaOptionDefinition(strike, expiry, IS_CALL);
        prices.put(definition, BSM.getPricingFunction(definition).evaluate(data));
      }
    }
    final VolatilitySurface surface = MODEL.getSurface(prices, data);
    for (final Expiry expiry : TEST_EXPIRY) {
      for (final double strike : TEST_STRIKE) {
        assertEquals(surface.getVolatility(DateUtil.getDifferenceInYears(DATE, expiry.getExpiry()), strike), sigma, EPS);
      }
    }
  }

  @Test
  public void testUnifomlyVaryingSurface() {
    final Map<OptionDefinition, Double> prices = new HashMap<OptionDefinition, Double>();
    OptionDefinition definition;
    StandardOptionDataBundle data = new StandardOptionDataBundle(CURVE, B, null, SPOT, DATE);
    final double diff = 0.09;
    final double startSigma = 0.18;
    final double[] sigma = new double[] { startSigma, startSigma + diff, startSigma + 2 * diff, startSigma + 3 * diff };
    for (int i = 0; i < sigma.length; i++) {
      for (final double strike : STRIKE) {
        definition = new EuropeanVanillaOptionDefinition(strike, EXPIRY[i], IS_CALL);
        data = data.withVolatilitySurface(new ConstantVolatilitySurface(sigma[i]));
        prices.put(definition, BSM.getPricingFunction(definition).evaluate(data));
      }
    }
    final VolatilitySurface surface = MODEL.getSurface(prices, data);
    double result;
    Expiry expiry;
    for (int i = 0; i < TEST_EXPIRY.length; i++) {
      expiry = TEST_EXPIRY[i];
      result = sigma[i] + 4 * diff * DateUtil.getDifferenceInYears(EXPIRY[i], expiry);
      for (final double strike : TEST_STRIKE) {
        assertEquals(surface.getVolatility(DateUtil.getDifferenceInYears(DATE, expiry.getExpiry()), strike), result, EPS);
      }
    }
  }
}
