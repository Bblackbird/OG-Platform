/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.future.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.util.time.DateUtil;

/**
 *
 */
public class FXFutureDataBundleTest {
  private static final YieldAndDiscountCurve FOREIGN = new ConstantYieldCurve(0.03);
  private static final YieldAndDiscountCurve DOMESTIC = new ConstantYieldCurve(0.05);
  private static final double SPOT = 1.5;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final FXFutureDataBundle DATA = new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT, DATE);

  @Test(expected = IllegalArgumentException.class)
  public void testNullDomesticCurveConstructor() {
    new FXFutureDataBundle(null, FOREIGN, SPOT, DATE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testForeignCurveConstructor() {
    new FXFutureDataBundle(DOMESTIC, null, -SPOT, DATE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeSpotConstructor() {
    new FXFutureDataBundle(DOMESTIC, FOREIGN, -SPOT, DATE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDateConstructor() {
    new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDomesticCurveBuilder() {
    DATA.withDiscountCurve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullForeignCurveBuilder() {
    DATA.withForeignCurve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeSpotBuilder() {
    DATA.withSpot(-SPOT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDateBuilder() {
    DATA.withDate(null);
  }

  @Test
  public void testEqualsAndHashCode() {
    FutureDataBundle data = new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT, DATE);
    assertEquals(data, DATA);
    assertEquals(data.hashCode(), DATA.hashCode());
    data = new FXFutureDataBundle(new ConstantYieldCurve(0.12), FOREIGN, SPOT, DATE);
    assertFalse(data.equals(DATA));
    data = new FXFutureDataBundle(DOMESTIC, new ConstantYieldCurve(0.07), SPOT, DATE);
    assertFalse(data.equals(DATA));
    data = new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT - 1, DATE);
    assertFalse(data.equals(DATA));
    data = new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT, DATE.plusDays(4));
    assertFalse(data.equals(DATA));

  }

  @Test
  public void testBuilders() {
    final YieldAndDiscountCurve curve = new ConstantYieldCurve(0.02);
    final double spot = 2;
    final ZonedDateTime date = DateUtil.getUTCDate(2010, 2, 1);
    assertEquals(DATA.withDate(date), new FXFutureDataBundle(DOMESTIC, FOREIGN, SPOT, date));
    assertEquals(DATA.withDiscountCurve(curve), new FXFutureDataBundle(curve, FOREIGN, SPOT, DATE));
    assertEquals(DATA.withForeignCurve(curve), new FXFutureDataBundle(DOMESTIC, curve, SPOT, DATE));
    assertEquals(DATA.withSpot(spot), new FXFutureDataBundle(DOMESTIC, FOREIGN, spot, DATE));
  }
}
