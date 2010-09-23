/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class CappedPowerOptionDefinitionTest {
  private static final double STRIKE = 100;
  private static final double POWER = 2;
  private static final double CAP = 90;
  private static final Expiry EXPIRY = new Expiry(DateUtil.getUTCDate(2010, 6, 1));
  private static final CappedPowerOptionDefinition CALL = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, CAP, true);
  private static final CappedPowerOptionDefinition PUT = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, CAP, false);

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeStrike() {
    new CappedPowerOptionDefinition(-STRIKE, EXPIRY, POWER, CAP, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullExpiry() {
    new CappedPowerOptionDefinition(STRIKE, null, POWER, CAP, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeCap() {
    new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, -CAP, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDataBundle() {
    CALL.getPayoffFunction().getPayoff(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongCapPut() {
    new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, STRIKE + 10, false);
  }

  @Test
  public void testGetters() {
    assertEquals(CALL.getPower(), POWER, 0);
    assertEquals(CALL.getCap(), CAP, 0);
  }

  @Test
  public void testEqualsAndHashCode() {
    OptionDefinition call = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, CAP, true);
    assertEquals(call, CALL);
    assertEquals(call.hashCode(), CALL.hashCode());
    final OptionDefinition put = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, CAP, false);
    assertEquals(put, PUT);
    assertEquals(put.hashCode(), PUT.hashCode());
    assertFalse(CALL.equals(PUT));
    call = new CappedPowerOptionDefinition(STRIKE + 1, EXPIRY, POWER, CAP, true);
    assertFalse(call.equals(CALL));
    call = new CappedPowerOptionDefinition(STRIKE, new Expiry(EXPIRY.getExpiry().minusDays(10)), POWER, CAP, true);
    assertFalse(call.equals(CALL));
    call = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER + 1, CAP, true);
    assertFalse(call.equals(CALL));
    call = new CappedPowerOptionDefinition(STRIKE, EXPIRY, POWER, CAP + 1, true);
    assertFalse(call.equals(CALL));
  }
}
