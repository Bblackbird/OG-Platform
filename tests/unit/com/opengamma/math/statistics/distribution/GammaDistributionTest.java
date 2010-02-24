/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.distribution;

import org.junit.Test;

/**
 * 
 * @author emcleod
 */
public class GammaDistributionTest extends ProbabilityDistributionTestCase {

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeK1() {
    new GammaDistribution(-1, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeK2() {
    new GammaDistribution(-1, 1, ENGINE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeTheta1() {
    new GammaDistribution(1, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNegativeTheta2() {
    new GammaDistribution(1, -1, ENGINE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullEngine() {
    new GammaDistribution(1, 1, null);
  }

  @Test
  public void test() {
    final ProbabilityDistribution<Double> dist = new GammaDistribution(1, 0.5);
    testCDFWithNull(dist);
    testPDFWithNull(dist);
  }
}
