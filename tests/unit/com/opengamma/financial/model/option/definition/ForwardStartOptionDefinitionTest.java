/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.option.Moneyness;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class ForwardStartOptionDefinitionTest {
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getUTCDate(2010, 7, 1));
  private static final Expiry START = new Expiry(DateUtil.getUTCDate(2010, 6, 1));
  private static final double PERCENT = 0.4;
  private static final Moneyness MONEYNESS = Moneyness.ATM;
  private static final double SPOT = 100;
  private static final ForwardStartOptionDefinition ATM_CALL = new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, MONEYNESS);
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(new ConstantYieldCurve(0.03), 0, new ConstantVolatilitySurface(0.2), SPOT, DATE);

  @Test(expected = IllegalArgumentException.class)
  public void testNullStartTime() {
    new ForwardStartOptionDefinition(EXPIRY, true, null, PERCENT, MONEYNESS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativePercent() {
    new ForwardStartOptionDefinition(EXPIRY, true, START, -PERCENT, MONEYNESS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullMoneyness() {
    new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongStartTime() {
    new ForwardStartOptionDefinition(START, true, EXPIRY, PERCENT, MONEYNESS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadPayoffTime() {
    ATM_CALL.getPayoffFunction().getPayoff(DATA.withDate(DateUtil.getUTCDate(2009, 1, 1)), 0.);
  }

  @Test
  public void testGetters() {
    assertEquals(ATM_CALL.getStartTime(), START);
    assertEquals(ATM_CALL.getPercent(), PERCENT, 0);
    assertEquals(ATM_CALL.getMoneyness(), MONEYNESS);
  }

  @Test
  public void testHashCodeAndEquals() {
    ForwardStartOptionDefinition other = new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, MONEYNESS);
    assertEquals(other, ATM_CALL);
    assertEquals(other.hashCode(), ATM_CALL.hashCode());
    other = new ForwardStartOptionDefinition(new Expiry(DateUtil.getUTCDate(2011, 1, 1)), true, START, PERCENT, MONEYNESS);
    assertFalse(other.equals(ATM_CALL));
    other = new ForwardStartOptionDefinition(EXPIRY, false, START, PERCENT, MONEYNESS);
    assertFalse(other.equals(ATM_CALL));
    other = new ForwardStartOptionDefinition(EXPIRY, true, new Expiry(DATE), PERCENT, MONEYNESS);
    assertFalse(other.equals(ATM_CALL));
    other = new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT + 0.1, MONEYNESS);
    assertFalse(other.equals(ATM_CALL));
    other = new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, Moneyness.OTM);
    assertFalse(other.equals(ATM_CALL));
  }

  @Test
  public void testAlpha() {
    assertEquals(ATM_CALL.getAlpha(), 1, 0);
    assertEquals(new ForwardStartOptionDefinition(EXPIRY, false, START, PERCENT, MONEYNESS).getAlpha(), 1, 0);
    assertEquals(new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, Moneyness.ITM).getAlpha(), 0.6, 0);
    assertEquals(new ForwardStartOptionDefinition(EXPIRY, false, START, PERCENT, Moneyness.ITM).getAlpha(), 1.4, 0);
    assertEquals(new ForwardStartOptionDefinition(EXPIRY, true, START, PERCENT, Moneyness.OTM).getAlpha(), 1.4, 0);
    assertEquals(new ForwardStartOptionDefinition(EXPIRY, false, START, PERCENT, Moneyness.OTM).getAlpha(), 0.6, 0);
  }
}
