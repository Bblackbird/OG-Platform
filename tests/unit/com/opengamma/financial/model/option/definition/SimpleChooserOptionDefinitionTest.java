/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.model.interestrate.curve.ConstantInterestRateDiscountCurve;
import com.opengamma.financial.model.option.pricing.analytic.AnalyticOptionModel;
import com.opengamma.financial.model.option.pricing.analytic.BlackScholesMertonModel;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class SimpleChooserOptionDefinitionTest {
  private static final double STRIKE = 100;
  private static final double DIFF = 20;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.1));
  private static final Expiry UNDERLYING_EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 1));
  private static final SimpleChooserOptionDefinition CHOOSER = new SimpleChooserOptionDefinition(EXPIRY, STRIKE, UNDERLYING_EXPIRY);
  private static final OptionDefinition VANILLA_CALL = new EuropeanVanillaOptionDefinition(STRIKE, UNDERLYING_EXPIRY, true);
  private static final OptionDefinition VANILLA_PUT = new EuropeanVanillaOptionDefinition(STRIKE, UNDERLYING_EXPIRY, false);
  private static final AnalyticOptionModel<OptionDefinition, StandardOptionDataBundle> MODEL = new BlackScholesMertonModel();
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(new ConstantInterestRateDiscountCurve(0.06), 0., new ConstantVolatilitySurface(0.15), STRIKE, DATE);
  private static final Set<Greek> PRICE = Sets.newHashSet(Greek.FAIR_PRICE);
  private static final double EPS = 1e-15;

  @Test(expected = IllegalArgumentException.class)
  public void testNullChooseDate() {
    new SimpleChooserOptionDefinition(EXPIRY, STRIKE, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLateChooseDate() {
    new SimpleChooserOptionDefinition(EXPIRY, STRIKE, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.05)));
  }

  @Test
  public void testExerciseFunction() {
    final OptionExerciseFunction<StandardOptionDataBundle> exercise = CHOOSER.getExerciseFunction();
    assertFalse(exercise.shouldExercise(DATA, STRIKE - DIFF));
    assertFalse(exercise.shouldExercise(DATA, STRIKE + DIFF));
  }

  @Test
  public void testPayoffFunction() {
    StandardOptionDataBundle data = DATA.withSpot(STRIKE + DIFF);
    final OptionPayoffFunction<StandardOptionDataBundle> payoff = CHOOSER.getPayoffFunction();
    assertEquals(MODEL.getGreeks(VANILLA_CALL, data, PRICE).get(Greek.FAIR_PRICE), payoff.getPayoff(data, null), EPS);
    data = DATA.withSpot(STRIKE - DIFF);
    assertEquals(MODEL.getGreeks(VANILLA_PUT, data, PRICE).get(Greek.FAIR_PRICE), payoff.getPayoff(data, null), EPS);
  }

  @Test
  public void testGetters() {
    assertEquals(CHOOSER.getCallDefinition(), VANILLA_CALL);
    assertEquals(CHOOSER.getPutDefinition(), VANILLA_PUT);
  }

  @Test
  public void testHashCodeAndEquals() {
    final SimpleChooserOptionDefinition definition1 = new SimpleChooserOptionDefinition(EXPIRY, STRIKE, UNDERLYING_EXPIRY);
    assertEquals(definition1, CHOOSER);
    assertEquals(definition1.hashCode(), CHOOSER.hashCode());
    final SimpleChooserOptionDefinition definition2 = new SimpleChooserOptionDefinition(new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5)), STRIKE, UNDERLYING_EXPIRY);
    final SimpleChooserOptionDefinition definition3 = new SimpleChooserOptionDefinition(EXPIRY, STRIKE + DIFF, UNDERLYING_EXPIRY);
    final SimpleChooserOptionDefinition definition4 = new SimpleChooserOptionDefinition(EXPIRY, STRIKE, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.3)));
    assertFalse(CHOOSER.equals(definition2));
    assertFalse(CHOOSER.equals(definition3));
    assertFalse(CHOOSER.equals(definition4));
  }
}
