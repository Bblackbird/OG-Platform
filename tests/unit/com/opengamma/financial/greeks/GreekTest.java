/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.greeks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.opengamma.financial.pnl.UnderlyingType;

/**
 * 
 */
public class GreekTest {
  private static final String NAME = "GREEK";
  private static final Underlying UNDERLYING = new NthOrderUnderlying(1, UnderlyingType.SPOT_PRICE);

  @Test
  public void test() {
    final Greek greek = new MyGreek(UNDERLYING, NAME);
    assertEquals(greek.toString(), NAME);
    assertEquals(greek.getUnderlying(), UNDERLYING);
    Greek other = new MyGreek(UNDERLYING, NAME);
    assertEquals(other, greek);
    assertEquals(other.hashCode(), greek.hashCode());
    other = new MyGreek(new NthOrderUnderlying(2, UnderlyingType.SPOT_PRICE), NAME);
    assertFalse(other.equals(greek));
    other = new MyGreek(UNDERLYING, "OTHER");
  }

  private static class MyGreek extends Greek {

    public MyGreek(final Underlying underlying, final String name) {
      super(underlying, name);
    }

    @Override
    public <T> T accept(final GreekVisitor<T> visitor) {
      return null;
    }

  }
}
