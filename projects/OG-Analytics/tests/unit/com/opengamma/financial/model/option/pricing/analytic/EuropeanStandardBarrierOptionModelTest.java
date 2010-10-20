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
import com.opengamma.financial.model.option.definition.Barrier;
import com.opengamma.financial.model.option.definition.Barrier.BarrierType;
import com.opengamma.financial.model.option.definition.Barrier.KnockType;
import com.opengamma.financial.model.option.definition.EuropeanStandardBarrierOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class EuropeanStandardBarrierOptionModelTest {
  private static final double SPOT = 100;
  private static final double REBATE = 3;
  private static final YieldAndDiscountCurve R = new YieldCurve(ConstantDoublesCurve.from(0.08));
  private static final double B = 0.04;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final AnalyticOptionModel<EuropeanStandardBarrierOptionDefinition, StandardOptionDataBundle> MODEL = new EuropeanStandardBarrierOptionModel();
  private static final double EPS = 1e-4;

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getPricingFunction(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getPricingFunction(new EuropeanStandardBarrierOptionDefinition(SPOT, EXPIRY, true, new Barrier(KnockType.IN, BarrierType.DOWN, SPOT))).evaluate((StandardOptionDataBundle) null);
  }

  @Test
  public void testZeroVol() {
    final double delta = 10;
    final StandardOptionDataBundle data = new StandardOptionDataBundle(new YieldCurve(ConstantDoublesCurve.from(0.)), 0, new ConstantVolatilitySurface(0.), SPOT, DATE);
    Barrier barrier = new Barrier(KnockType.OUT, BarrierType.DOWN, 95);
    EuropeanStandardBarrierOptionDefinition option = new EuropeanStandardBarrierOptionDefinition(SPOT - delta, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 10, 0);
    barrier = new Barrier(KnockType.IN, BarrierType.UP, 105);
    option = new EuropeanStandardBarrierOptionDefinition(SPOT - delta, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), REBATE, 0);
  }

  @Test
  public void test() {
    final StandardOptionDataBundle data = new StandardOptionDataBundle(R, B, new ConstantVolatilitySurface(0.25), SPOT, DATE);
    Barrier barrier = new Barrier(KnockType.OUT, BarrierType.DOWN, 95);
    EuropeanStandardBarrierOptionDefinition option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 9.0246, EPS);
    barrier = new Barrier(KnockType.OUT, BarrierType.UP, 105);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 2.6789, EPS);
    barrier = new Barrier(KnockType.OUT, BarrierType.DOWN, 95);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, false, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 2.2798, EPS);
    barrier = new Barrier(KnockType.OUT, BarrierType.UP, 105);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, false, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 3.7760, EPS);
    barrier = new Barrier(KnockType.IN, BarrierType.DOWN, 95);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 7.7627, EPS);
    barrier = new Barrier(KnockType.IN, BarrierType.UP, 105);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, true, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 14.1112, EPS);
    barrier = new Barrier(KnockType.IN, BarrierType.DOWN, 95);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, false, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 2.9586, EPS);
    barrier = new Barrier(KnockType.IN, BarrierType.UP, 105);
    option = new EuropeanStandardBarrierOptionDefinition(90, EXPIRY, false, barrier, REBATE);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 1.4653, EPS);
  }
}
