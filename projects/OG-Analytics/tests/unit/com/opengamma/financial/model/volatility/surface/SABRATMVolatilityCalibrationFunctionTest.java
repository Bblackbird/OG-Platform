/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.surface;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.SABRDataBundle;
import com.opengamma.math.function.RealPolynomialFunction1D;
import com.opengamma.math.rootfinding.CubicRealRootFinder;
import com.opengamma.math.rootfinding.QuadraticRealRootFinder;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class SABRATMVolatilityCalibrationFunctionTest {
  private static final double BETA = 1;
  private static final double RHO = 0.3;
  private static final double ATM_SIGMA = 0.42;
  private static final double KSI = 0.5;
  private static final double T = 0.5;
  private static final double F = 100;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 8, 1);
  private static final SABRDataBundle DATA = new SABRDataBundle(new ConstantYieldCurve(0.0), 0., new ConstantVolatilitySurface(ATM_SIGMA), F, DATE, 0, BETA, RHO, KSI);
  private static final SABRATMVolatilityCalibrationFunction FUNCTION = new SABRATMVolatilityCalibrationFunction();
  private static final OptionDefinition OPTION = new EuropeanVanillaOptionDefinition(100, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, T)), true);
  private static final CubicRealRootFinder CUBIC_ROOT_FINDER = new CubicRealRootFinder();
  private static final QuadraticRealRootFinder QUADRATIC_ROOT_FINDER = new QuadraticRealRootFinder();

  @Test(expected = IllegalArgumentException.class)
  public void testNullOption() {
    FUNCTION.calibrate(null, DATA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    FUNCTION.calibrate(OPTION, null);
  }

  @Test
  public void testZeroVolOfVol() {
    final double beta = 0.5;
    final SABRDataBundle data = DATA.withVolOfVol(0).withBeta(beta);
    final double f1 = Math.pow(F, 1 - beta);
    final double a0 = -ATM_SIGMA * f1;
    final double a1 = 1;
    final double a2 = 0;
    final double a3 = (1 - beta) * (1 - beta) * T / 24 / f1 / f1;
    final Double[] roots = CUBIC_ROOT_FINDER.getRoots(new RealPolynomialFunction1D(new double[] {a0, a1, a2, a3}));
    assertEquals(roots[0], FUNCTION.calibrate(OPTION, data).getAlpha(), 1e-9);
  }

  @Test
  public void testBetaEqualsOne() {
    final double a0 = -ATM_SIGMA;
    final double a1 = 1 + (2 - 3 * RHO * RHO) * KSI * KSI * T / 24;
    final double a2 = RHO * KSI * T / 4;
    final Double[] roots = QUADRATIC_ROOT_FINDER.getRoots(new RealPolynomialFunction1D(new double[] {a0, a1, a2}));
    assertEquals(FUNCTION.calibrate(OPTION, DATA).getAlpha(), roots[1], 1e-9);
  }
}
