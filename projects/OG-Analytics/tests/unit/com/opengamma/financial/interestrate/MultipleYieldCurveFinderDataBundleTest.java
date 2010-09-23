/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import com.opengamma.financial.interestrate.cash.definition.Cash;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.math.interpolation.LinearInterpolator1D;
import com.opengamma.math.interpolation.LogLinearInterpolator1D;
import com.opengamma.math.interpolation.data.Interpolator1DDataBundle;
import com.opengamma.math.interpolation.sensitivity.FiniteDifferenceInterpolator1DNodeSensitivityCalculator;
import com.opengamma.math.interpolation.sensitivity.Interpolator1DNodeSensitivityCalculator;
import com.opengamma.math.interpolation.sensitivity.LinearInterpolator1DNodeSensitivityCalculator;

/**
 * 
 */
public class MultipleYieldCurveFinderDataBundleTest {
  private static final String CURVE_NAME1 = "Test1";
  private static final String CURVE_NAME2 = "Test2";
  private static final List<InterestRateDerivative> DERIVATIVES;
  private static final double[] TIMES1;
  private static final double[] TIMES2;

  private static final LinkedHashMap<String, double[]> NODES = new LinkedHashMap<String, double[]>();
  private static final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> INTERPOLATORS = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
  private static final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> SENSITIVITY_CALCULATORS = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
  private static final Interpolator1D<Interpolator1DDataBundle> INTERPOLATOR1 = new LinearInterpolator1D();
  private static final Interpolator1DNodeSensitivityCalculator<Interpolator1DDataBundle> CALCULATOR1 = new LinearInterpolator1DNodeSensitivityCalculator();
  private static final Interpolator1D<Interpolator1DDataBundle> INTERPOLATOR2 = new LogLinearInterpolator1D();
  private static final Interpolator1DNodeSensitivityCalculator<Interpolator1DDataBundle> CALCULATOR2 = new FiniteDifferenceInterpolator1DNodeSensitivityCalculator<Interpolator1DDataBundle>(
      INTERPOLATOR2);
  private static final MultipleYieldCurveFinderDataBundle DATA;

  static {
    final int n = 10;
    DERIVATIVES = new ArrayList<InterestRateDerivative>();
    TIMES1 = new double[n];
    TIMES2 = new double[n];
    for (int i = 0; i < n; i++) {
      final double t1 = i / 10.;
      final double t2 = t1 + 0.005;
      DERIVATIVES.add(new Cash(t1, Math.random(), CURVE_NAME1));
      DERIVATIVES.add(new Cash(t2, Math.random(), CURVE_NAME2));
      TIMES1[i] = t1;
      TIMES2[i] = t2;
    }
    NODES.put(CURVE_NAME1, TIMES1);
    INTERPOLATORS.put(CURVE_NAME1, INTERPOLATOR1);
    SENSITIVITY_CALCULATORS.put(CURVE_NAME1, CALCULATOR1);
    NODES.put(CURVE_NAME2, TIMES2);
    INTERPOLATORS.put(CURVE_NAME2, INTERPOLATOR2);
    SENSITIVITY_CALCULATORS.put(CURVE_NAME2, CALCULATOR2);
    DATA = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullDerivatives() {
    new MultipleYieldCurveFinderDataBundle(null, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNodes() {
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, null, INTERPOLATORS, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullInterpolators() {
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, null, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSensitivityCalculators() {
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNameClash() {
    final YieldCurveBundle bundle = new YieldCurveBundle();
    final YieldAndDiscountCurve curve = new ConstantYieldCurve(0.05);
    bundle.setCurve(CURVE_NAME1, curve);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, bundle, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyDerivatives() {
    new MultipleYieldCurveFinderDataBundle(new ArrayList<InterestRateDerivative>(), null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCurveAlreadyPresent() {
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, new YieldCurveBundle(Collections.<String, YieldAndDiscountCurve> singletonMap(CURVE_NAME1, new ConstantYieldCurve(2.))), NODES, INTERPOLATORS,
        SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongSize1() {
    final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
    interpolators.put(CURVE_NAME2, INTERPOLATOR1);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, interpolators, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongSize2() {
    final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> calculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
    calculators.put(CURVE_NAME2, CALCULATOR1);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, calculators);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNames1() {
    final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
    interpolators.put(CURVE_NAME2, INTERPOLATOR1);
    interpolators.put(CURVE_NAME1, INTERPOLATOR2);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, interpolators, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNames2() {
    final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> calculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
    calculators.put(CURVE_NAME2, CALCULATOR1);
    calculators.put(CURVE_NAME1, CALCULATOR2);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, calculators);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue1() {
    final LinkedHashMap<String, double[]> nodes = new LinkedHashMap<String, double[]>();
    nodes.put(CURVE_NAME1, TIMES1);
    nodes.put(CURVE_NAME2, null);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, nodes, INTERPOLATORS, SENSITIVITY_CALCULATORS);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue2() {
    final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
    interpolators.put(CURVE_NAME2, INTERPOLATOR1);
    interpolators.put(CURVE_NAME1, null);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, interpolators, SENSITIVITY_CALCULATORS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullValue3() {
    final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> calculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
    calculators.put(CURVE_NAME2, CALCULATOR1);
    calculators.put(CURVE_NAME1, null);
    new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, calculators);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName1() {
    DATA.getCurveNodePointsForCurve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName2() {
    DATA.getInterpolatorForCurve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName3() {
    DATA.getSensitivityCalculatorForName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongName1() {
    DATA.getCurveNodePointsForCurve("X");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongName2() {
    DATA.getInterpolatorForCurve("Y");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongName3() {
    DATA.getSensitivityCalculatorForName("Z");
  }

  @Test
  public void testGetters() {
    final List<String> names = new ArrayList<String>();
    for (final String name : NODES.keySet()) {
      names.add(name);
    }
    assertEquals(DATA.getCurveNames(), names);
    assertArrayEquals(DATA.getCurveNodePointsForCurve(CURVE_NAME1), TIMES1, 0);
    assertArrayEquals(DATA.getCurveNodePointsForCurve(CURVE_NAME2), TIMES2, 0);
    for (int i = 0; i < DERIVATIVES.size(); i++) {
      assertEquals(DERIVATIVES.get(i), DATA.getDerivative(i));
    }
    assertEquals(DATA.getDerivatives(), DERIVATIVES);
    assertEquals(DATA.getInterpolatorForCurve(CURVE_NAME1), INTERPOLATOR1);
    assertEquals(DATA.getInterpolatorForCurve(CURVE_NAME2), INTERPOLATOR2);
    assertEquals(DATA.getKnownCurves(), null);
    assertEquals(DATA.getSensitivityCalculatorForName(CURVE_NAME1), CALCULATOR1);
    assertEquals(DATA.getSensitivityCalculatorForName(CURVE_NAME2), CALCULATOR2);
    assertEquals(DATA.getTotalNodes(), TIMES1.length * 2);
    assertEquals(DATA.getUnknownCurveInterpolators(), INTERPOLATORS);
    assertEquals(DATA.getUnknownCurveNodePoints(), NODES);
    assertEquals(DATA.getUnknownCurveNodeSensitivityCalculators(), SENSITIVITY_CALCULATORS);
  }

  @Test
  public void testEqualsAndHashCode() {
    MultipleYieldCurveFinderDataBundle other = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
    assertEquals(DATA, other);
    assertEquals(DATA.hashCode(), other.hashCode());
    final List<InterestRateDerivative> derivatives = new ArrayList<InterestRateDerivative>(DERIVATIVES);
    derivatives.set(0, new Cash(1000, 0.05, CURVE_NAME1));
    other = new MultipleYieldCurveFinderDataBundle(derivatives, null, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
    assertFalse(other.equals(DATA));
    final YieldCurveBundle knownCurves = new YieldCurveBundle();
    other = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, knownCurves, NODES, INTERPOLATORS, SENSITIVITY_CALCULATORS);
    assertFalse(other.equals(DATA));
    final LinkedHashMap<String, double[]> nodes = new LinkedHashMap<String, double[]>();
    nodes.put(CURVE_NAME1, TIMES1);
    nodes.put(CURVE_NAME2, TIMES1);
    other = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, nodes, INTERPOLATORS, SENSITIVITY_CALCULATORS);
    assertFalse(other.equals(DATA));
    final LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>> interpolators = new LinkedHashMap<String, Interpolator1D<? extends Interpolator1DDataBundle>>();
    interpolators.put(CURVE_NAME1, INTERPOLATOR1);
    interpolators.put(CURVE_NAME2, INTERPOLATOR1);
    other = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, interpolators, SENSITIVITY_CALCULATORS);
    assertFalse(other.equals(DATA));
    final LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>> calculators = new LinkedHashMap<String, Interpolator1DNodeSensitivityCalculator<? extends Interpolator1DDataBundle>>();
    calculators.put(CURVE_NAME1, CALCULATOR1);
    calculators.put(CURVE_NAME2, CALCULATOR1);
    other = new MultipleYieldCurveFinderDataBundle(DERIVATIVES, null, NODES, INTERPOLATORS, calculators);
    assertFalse(other.equals(DATA));
  }
}
