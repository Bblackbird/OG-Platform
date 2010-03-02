package com.opengamma.math.function.special;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cern.jet.random.engine.MersenneTwister64;
import cern.jet.random.engine.RandomEngine;

import com.opengamma.math.function.Function1D;

public class GammaFunctionTest {
  private static final RandomEngine RANDOM = new MersenneTwister64(MersenneTwister64.DEFAULT_SEED);
  private static final Function1D<Double, Double> GAMMA = new GammaFunction();
  private static final Function1D<Double, Double> LN_GAMMA = new NaturalLogGammaFunction();
  private static final double EPS = 1e-9;

  @Test
  public void test() {
    final double x = RANDOM.nextDouble();
    assertEquals(Math.log(GAMMA.evaluate(x)), LN_GAMMA.evaluate(x), EPS);
  }
}
