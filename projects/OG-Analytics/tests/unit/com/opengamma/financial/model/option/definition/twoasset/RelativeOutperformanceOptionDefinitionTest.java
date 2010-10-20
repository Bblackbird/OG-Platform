/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition.twoasset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class RelativeOutperformanceOptionDefinitionTest {
  private static final double K = 1;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 1));
  private static final YieldAndDiscountCurve R = new YieldCurve(ConstantDoublesCurve.from(0.2));
  private static final double B1 = 0.02;
  private static final double B2 = 0.05;
  private static final double S1 = 100;
  private static final double S2 = 120;
  private static final VolatilitySurface SIGMA1 = new ConstantVolatilitySurface(0.4);
  private static final VolatilitySurface SIGMA2 = new ConstantVolatilitySurface(0.6);
  private static final double RHO = 0;
  private static final StandardTwoAssetOptionDataBundle DATA = new StandardTwoAssetOptionDataBundle(R, B1, B2, SIGMA1, SIGMA2, S1, S2, RHO, DATE);

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    new RelativeOutperformanceOptionDefinition(K, EXPIRY, true).getPayoffFunction().getPayoff(null, null);
  }

  @Test
  public void testExercise() {
    RelativeOutperformanceOptionDefinition option = new RelativeOutperformanceOptionDefinition(K, EXPIRY, true);
    assertFalse(option.getExerciseFunction().shouldExercise(DATA, null));
    option = new RelativeOutperformanceOptionDefinition(K, EXPIRY, false);
    assertFalse(option.getExerciseFunction().shouldExercise(DATA, null));
    final StandardTwoAssetOptionDataBundle data = DATA.withFirstCostOfCarry(180);
    option = new RelativeOutperformanceOptionDefinition(K, EXPIRY, true);
    assertFalse(option.getExerciseFunction().shouldExercise(data, null));
    option = new RelativeOutperformanceOptionDefinition(K, EXPIRY, false);
    assertFalse(option.getExerciseFunction().shouldExercise(DATA, null));
  }

  @Test
  public void testPayoff() {
    final RelativeOutperformanceOptionDefinition call = new RelativeOutperformanceOptionDefinition(K, EXPIRY, true);
    final RelativeOutperformanceOptionDefinition put = new RelativeOutperformanceOptionDefinition(K, EXPIRY, false);
    final double eps = 1e-12;
    assertEquals(call.getPayoffFunction().getPayoff(DATA, null), 0, eps);
    assertEquals(put.getPayoffFunction().getPayoff(DATA, null), 1. / 6, eps);
    final StandardTwoAssetOptionDataBundle data = DATA.withSecondSpot(80);
    assertEquals(call.getPayoffFunction().getPayoff(data, null), 1. / 4, eps);
    assertEquals(put.getPayoffFunction().getPayoff(data, null), 0, eps);
  }
}
