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
 * @author emcleod
 */
public class SampleAutocorrelationIIDHypothesisTest extends IIDHypothesisTestCase {
  private static final IIDHypothesis SAMPLE_ACF = new SampleAutocorrelationIIDHypothesis(0.05, 100);

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeLevel() {
    new SampleAutocorrelationIIDHypothesis(-0.1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHighLevel() {
    new SampleAutocorrelationIIDHypothesis(1.5, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testZeroLag() {
    new SampleAutocorrelationIIDHypothesis(0.05, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsufficientData() {
    SAMPLE_ACF.evaluate(RANDOM.subSeries(RANDOM.getTime(0), RANDOM.getTime(3)));
  }

  @Test
  public void test() {
    super.testNullTS(SAMPLE_ACF);
    super.testEmptyTS(SAMPLE_ACF);
    assertTrue(SAMPLE_ACF.evaluate(RANDOM));
    assertFalse(SAMPLE_ACF.evaluate(SIGNAL));
    assertFalse(SAMPLE_ACF.evaluate(INCREASING));
  }
}
