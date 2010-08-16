/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import com.opengamma.financial.interestrate.cash.definition.Cash;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.LinearInterpolator1D;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.interpolation.sensitivity.Interpolator1DNodeSensitivityCalculator;
import com.opengamma.math.interpolation.sensitivity.LinearInterpolator1DNodeSensitivityCalculator;
import com.opengamma.math.matrix.DoubleMatrix1D;

/**
 * 
 */
public class MultipleYieldCurveFinderFunctionTest {

  private static final String CURVE_NAME = "Test";
  private static final List<InterestRateDerivative> DERIVATIVES;
  private static final double[] SIMPLE_RATES;
  private static final double[] CONTINUOUS_RATES;
  private static final double[] TIMES;

  private static final InterestRateDerivativeVisitor<Double> CALCULATOR = ParRateDifferenceCalculator.getInstance();

  private static final Interpolator1D<Interpolator1DDataBundle> INTERPOLATOR = new LinearInterpolator1D();
  private static final Function1D<DoubleMatrix1D, DoubleMatrix1D> FINDER;
  private static final LinkedHashMap<String, double[]> NODES = new LinkedHashMap<String, double[]>();
  private static final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> INTERPOLATORS = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
  private static final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> SENSITIVITY_CALCULATORS = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
  private static final MultipleYieldCurveFinderDataBundle DATA;

  static {
    final int n = 10;
    DERIVATIVES = new ArrayList<InterestRateDerivative>();
    SIMPLE_RATES = new double[n];
    CONTINUOUS_RATES = new double[n];
    TIMES = new double[n];
    double t;
    for (int i = 0; i < n; i++) {
      t = i / 10.;
      SIMPLE_RATES[i] = Math.random() * 0.05;
      DERIVATIVES.add(new Cash(t, SIMPLE_RATES[i], CURVE_NAME));
      CONTINUOUS_RATES[i] = (t == 0 ? SIMPLE_RATES[i] : Math.log(1 + SIMPLE_RATES[i] * t) / t);
      TIMES[i] = t;
    }
    NODES.put(CURVE_NAME, TIMES);
    INTERPOLATORS.put(CURVE_NAME, INTERPOLATOR);
    SENSITIVITY_CALCULATORS.put(CURVE_NAME, new LinearInterpolator1DNodeSensitivityCalculator());
    DATA = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
    FINDER = new MultipleYieldCurveFinderFunction(DATA, CALCULATOR);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    new MultipleYieldCurveFinderFunction(null, CALCULATOR);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCalculator() {
    new MultipleYieldCurveFinderFunction(DATA, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullVector() {
    FINDER.evaluate((DoubleMatrix1D) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMismatchingVector() {
    FINDER.evaluate(new DoubleMatrix1D(new double[] {1, 2, 3, 4, 5, 6, 7, 8}));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNodeNumber() {
    final List<InterestRateDerivative> list = new ArrayList<InterestRateDerivative>();
    list.add(new Cash(1, 0.01, CURVE_NAME));
    list.add(new Cash(0.5, 0.01, CURVE_NAME));
    new MultipleYieldCurveFinderFunction(new MultipleYieldCurveFinderDataBundle(list, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS), CALCULATOR);
  }

  @Test
  public void test() {
    final DoubleMatrix1D results = FINDER.evaluate(new DoubleMatrix1D(CONTINUOUS_RATES));
    for (final double r : results.getData()) {
      assertEquals(0.0, r, 1e-14);
    }
  }
}
