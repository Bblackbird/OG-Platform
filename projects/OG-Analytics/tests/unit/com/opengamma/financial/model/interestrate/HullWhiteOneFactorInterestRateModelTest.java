/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.interestrate;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.interestrate.definition.HullWhiteOneFactorDataBundle;
import com.opengamma.financial.model.volatility.curve.VolatilityCurve;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class HullWhiteOneFactorInterestRateModelTest {
  private static final double YEARS = 11.3;
  private static final double T = 1.23;
  private static final ZonedDateTime TODAY = DateUtil.getUTCDate(2010, 8, 1);
  private static final ZonedDateTime START = DateUtil.getDateOffsetWithYearFraction(TODAY, T);
  private static final ZonedDateTime MATURITY = DateUtil.getDateOffsetWithYearFraction(START, YEARS);
  private static final double RATE = 0.056;
  private static final double VOL = 0.01;
  private static final double SPEED = 0.13;
  private static final YieldAndDiscountCurve R = new YieldCurve(ConstantDoublesCurve.from(RATE));
  private static final VolatilityCurve SIGMA = new VolatilityCurve(ConstantDoublesCurve.from(VOL));
  private static final HullWhiteOneFactorInterestRateModel MODEL = new HullWhiteOneFactorInterestRateModel();
  private static final double EPS = 1e-8;

  @Test(expected = IllegalArgumentException.class)
  public void testNullTime() {
    MODEL.getDiscountBondFunction(null, MATURITY);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullMaturity() {
    MODEL.getDiscountBondFunction(START, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getDiscountBondFunction(START, MATURITY).evaluate((HullWhiteOneFactorDataBundle) null);
  }

  @Test
  public void test() {
    HullWhiteOneFactorDataBundle data = new HullWhiteOneFactorDataBundle(R, SIGMA, TODAY, SPEED);
    assertEquals(MODEL.getDiscountBondFunction(START, START).evaluate(data), 1, EPS);
    data = new HullWhiteOneFactorDataBundle(R, new VolatilityCurve(ConstantDoublesCurve.from(0)), TODAY, SPEED);
    assertEquals(Math.log(MODEL.getDiscountBondFunction(START, MATURITY).evaluate(data)), -RATE * YEARS, EPS);
    data = new HullWhiteOneFactorDataBundle(R, SIGMA, TODAY, 200);
    assertEquals(Math.log(MODEL.getDiscountBondFunction(START, MATURITY).evaluate(data)), -RATE * YEARS, EPS);
  }
}
