/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.timeseries.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class BoxLjungPortmanteauIIDHypothesisTest extends IIDHypothesisTestCase {
  private static final IIDHypothesis BOX_LJUNG = new BoxLjungPortmanteauIIDHypothesis(0.05, 20);

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeLevel() {
    new BoxLjungPortmanteauIIDHypothesis(-0.1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHighLevel() {
    new BoxLjungPortmanteauIIDHypothesis(1.5, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroLag() {
    new BoxLjungPortmanteauIIDHypothesis(0.05, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsufficientData() {
    BOX_LJUNG.evaluate(RANDOM.subSeries(RANDOM.getTime(0), RANDOM.getTime(3)));
  }

  @Test
  public void test() {
    super.testNullTS(BOX_LJUNG);
    super.testEmptyTS(BOX_LJUNG);
    assertTrue(BOX_LJUNG.evaluate(RANDOM));
    assertFalse(BOX_LJUNG.evaluate(SIGNAL));
    assertFalse(BOX_LJUNG.evaluate(INCREASING));
  }
}
