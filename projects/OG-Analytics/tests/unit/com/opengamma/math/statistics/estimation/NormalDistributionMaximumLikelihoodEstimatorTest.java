/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.estimation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;

import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 */
public class NormalDistributionMaximumLikelihoodEstimatorTest {
  private static final DistributionParameterEstimator<Double> ESTIMATOR = new NormalDistributionMaximumLikelihoodEstimator();

  @Test(expected = IllegalArgumentException.class)
  public void testNull() {
    ESTIMATOR.evaluate((double[]) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmpty() {
    ESTIMATOR.evaluate(new double[0]);
  }

  @Test
  public void test() {
    final int n = 500000;
    final double eps = 1e-2;
    final double mu = -1.3;
    final double sigma = 0.4;
    final ProbabilityDistribution<Double> p1 = new NormalDistribution(mu, sigma, new MersenneTwister64(MersenneTwister64.DEFAULT_SEED));
    final double[] x = new double[n];
    for (int i = 0; i < n; i++) {
      x[i] = p1.nextRandom();
    }
    final NormalDistribution p2 = (NormalDistribution) ESTIMATOR.evaluate(x);
    assertEquals(p2.getMean(), mu, eps);
    assertEquals(p2.getStandardDeviation(), sigma, eps);
  }
}
