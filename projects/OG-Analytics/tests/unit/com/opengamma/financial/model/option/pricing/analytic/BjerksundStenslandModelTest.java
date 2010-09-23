/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import org.junit.Test;

/**
 * 
 */
public class BjerksundStenslandModelTest extends AmericanAnalyticOptionModelTest {

  @Test
  public void test() {
    super.test(new BjerksundStenslandModel(), 1e-4);
  }
}
