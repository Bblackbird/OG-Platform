/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.sensitivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Class describing a present value curve sensitivity for inflation and multi-curves framework.
 */
public class InflationSensitivity {

  private final MulticurveSensitivity _multicurveSensitivity;
  /**
   * The map containing the sensitivity to the price index. 
   * The map linked the curve (String) to a list of pairs (cash flow time, sensitivity value).
   */
  private final Map<String, List<DoublesPair>> _sensitivityPriceCurve;

  /**
   * Default constructor, creating an empty HashMap for the sensitivity.
   */
  public InflationSensitivity() {
    _multicurveSensitivity = new MulticurveSensitivity();
    _sensitivityPriceCurve = new HashMap<String, List<DoublesPair>>();
  }

  private InflationSensitivity(final MulticurveSensitivity multicurveSensitivity, final Map<String, List<DoublesPair>> sensitivityPriceCurve) {
    _multicurveSensitivity = multicurveSensitivity;
    _sensitivityPriceCurve = sensitivityPriceCurve;
  }

  /**
   * Constructor from a yield discounting map, a forward map and a price index curve of sensitivity. The maps are used directly.
   * @param sensitivityYieldDiscounting The map.
   * @param sensitivityForward The map.
   * @param sensitivityPriceCurve The map.
   */
  private InflationSensitivity(final Map<String, List<DoublesPair>> sensitivityYieldDiscounting, final Map<String, List<ForwardSensitivity>> sensitivityForward,
      final Map<String, List<DoublesPair>> sensitivityPriceCurve) {
    _multicurveSensitivity = MulticurveSensitivity.of(sensitivityYieldDiscounting, sensitivityForward);
    _sensitivityPriceCurve = sensitivityPriceCurve;
  }

  /**
   * Constructor from a yield discounting map of sensitivity. The maps are used directly.
   * @param sensitivityYieldDiscounting The map.
   * @param sensitivityForward The map.
   * @param sensitivityPriceCurve The map.
   * @return The sensitivity.
   */
  public static InflationSensitivity of(final Map<String, List<DoublesPair>> sensitivityYieldDiscounting, final Map<String, List<ForwardSensitivity>> sensitivityForward,
      final Map<String, List<DoublesPair>> sensitivityPriceCurve) {
    ArgumentChecker.notNull(sensitivityYieldDiscounting, "Sensitivity yield curve");
    ArgumentChecker.notNull(sensitivityForward, "Sensitivity forward");
    ArgumentChecker.notNull(sensitivityPriceCurve, "Sensitivity price index curve");
    return new InflationSensitivity(sensitivityYieldDiscounting, sensitivityForward, sensitivityPriceCurve);
  }

  /**
   * Constructor from a yield discounting map of sensitivity. The map is used directly.
   * @param sensitivityYieldDiscounting The map.
   * @return The sensitivity.
   */
  public static InflationSensitivity ofYieldDiscounting(final Map<String, List<DoublesPair>> sensitivityYieldDiscounting) {
    ArgumentChecker.notNull(sensitivityYieldDiscounting, "Sensitivity yield curve");
    return new InflationSensitivity(sensitivityYieldDiscounting, new HashMap<String, List<ForwardSensitivity>>(), new HashMap<String, List<DoublesPair>>());
  }

  /**
   * Constructor from a yield discounting map and a price map. The maps are used directly.
   * @param sensitivityYieldDiscounting The map.
   * @param sensitivityPriceCurve The map.
   * @return The sensitivity.
   */
  public static InflationSensitivity ofYieldDiscountingAndPrice(final Map<String, List<DoublesPair>> sensitivityYieldDiscounting, final Map<String, List<DoublesPair>> sensitivityPriceCurve) {
    ArgumentChecker.notNull(sensitivityYieldDiscounting, "Sensitivity yield curve");
    ArgumentChecker.notNull(sensitivityPriceCurve, "Sensitivity price index curve");
    return new InflationSensitivity(sensitivityYieldDiscounting, new HashMap<String, List<ForwardSensitivity>>(), sensitivityPriceCurve);
  }

  /**
   * Gets the discounting curve sensitivities.
   * @return The sensitivity map
   */
  public Map<String, List<DoublesPair>> getYieldDiscountingSensitivities() {
    return _multicurveSensitivity.getYieldDiscountingSensitivities();
  }

  /**
   * Gets the forward curve sensitivity map.
   * @return The sensitivity map
   */
  public Map<String, List<ForwardSensitivity>> getForwardSensitivities() {
    return _multicurveSensitivity.getForwardSensitivities();
  }

  /**
   * Gets the price index curve sensitivity map.
   * @return The sensitivity map
   */
  public Map<String, List<DoublesPair>> getPriceCurveSensitivities() {
    return _sensitivityPriceCurve;
  }

  /**
   * Create a copy of the sensitivity and add a given sensitivity to it.
   * @param other The sensitivity to add.
   * @return The total sensitivity.
   */
  public InflationSensitivity plus(final InflationSensitivity other) {
    ArgumentChecker.notNull(other, "sensitivity");
    final MulticurveSensitivity resultMulticurve = _multicurveSensitivity.plus(other._multicurveSensitivity);
    final Map<String, List<DoublesPair>> resultPrice = MulticurveSensitivityUtils.plus(_sensitivityPriceCurve, other._sensitivityPriceCurve);
    return new InflationSensitivity(resultMulticurve, resultPrice);
  }

  /**
   * Create a new sensitivity object containing the original sensitivity multiplied by a common factor.
   * @param factor The multiplicative factor.
   * @return The multiplied sensitivity.
   */
  public InflationSensitivity multipliedBy(final double factor) {
    final MulticurveSensitivity resultMulticurve = _multicurveSensitivity.multipliedBy(factor);
    final Map<String, List<DoublesPair>> resultPrice = MulticurveSensitivityUtils.multipliedBy(_sensitivityPriceCurve, factor);
    return new InflationSensitivity(resultMulticurve, resultPrice);
  }

  /**
   * Return a new sensitivity by sorting the times and adding the values at duplicated times.
   * @return The cleaned sensitivity.
   */
  public InflationSensitivity cleaned() {
    final MulticurveSensitivity resultMulticurve = _multicurveSensitivity.cleaned();
    final Map<String, List<DoublesPair>> resultPrice = MulticurveSensitivityUtils.cleaned(_sensitivityPriceCurve);
    return new InflationSensitivity(resultMulticurve, resultPrice);
  }

  @Override
  public String toString() {
    return _multicurveSensitivity.toString() + "\n" + "\n" + _sensitivityPriceCurve.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _multicurveSensitivity.hashCode();
    result = prime * result + _sensitivityPriceCurve.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InflationSensitivity other = (InflationSensitivity) obj;
    if (!ObjectUtils.equals(_multicurveSensitivity, other._multicurveSensitivity)) {
      return false;
    }
    if (!ObjectUtils.equals(_sensitivityPriceCurve, other._sensitivityPriceCurve)) {
      return false;
    }
    return true;
  }

}
