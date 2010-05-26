/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.sensitivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Collections;

import org.junit.Test;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.pnl.UnderlyingType;

/**
 * 
 */
public class ValueGreekSensitivityTest {
  private static final ValueGreek VALUE_GREEK = new ValueGreek(Greek.DELTA);
  private static final String NAME = "NAME";
  private static final Sensitivity<ValueGreek> SENSITIVITY = new ValueGreekSensitivity(VALUE_GREEK, NAME);

  @Test(expected = NullPointerException.class)
  public void testNullValueGreek() {
    new ValueGreekSensitivity(null, NAME);
  }

  @Test(expected = NullPointerException.class)
  public void testNullIdentifier() {
    new ValueGreekSensitivity(VALUE_GREEK, null);
  }

  @Test
  public void testGetters() {
    assertEquals(VALUE_GREEK, SENSITIVITY.getSensitivity());
    assertEquals(NAME, SENSITIVITY.getIdentifier());
    assertEquals(1, SENSITIVITY.getOrder());
    assertEquals(Collections.singleton(UnderlyingType.SPOT_PRICE), SENSITIVITY.getUnderlyingTypes());
  }

  @Test
  public void testEquals() {
    final Sensitivity<ValueGreek> sensitivity = new ValueGreekSensitivity(VALUE_GREEK, NAME);
    assertFalse(SENSITIVITY.equals(sensitivity));
  }
}
