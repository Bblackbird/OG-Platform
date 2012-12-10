/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.sensitivity.blackforex;

import java.util.Set;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.interestrate.InstrumentDerivative;
import com.opengamma.analytics.financial.interestrate.InstrumentDerivativeVisitor;
import com.opengamma.analytics.financial.provider.description.ForexBlackSmileProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.util.ArgumentChecker;

/**
 * For an instrument, computes the sensitivity of a multiple currency value (often the present value) to the parameters used in the curve.
 * The meaning of "parameters" will depend of the way the curve is stored (interpolated yield, function parameters, etc.) and also on the way
 * the parameters sensitivities are aggregated (the same parameter can be used in several curves).
 */
public abstract class AbstractParameterSensitivityForexBlackSmileCalculator {

  /**
   * The sensitivity calculator to compute the sensitivity of the value with respect to the zero-coupon continuously compounded rates at different times (discounting) or forward rates.
   */
  private final InstrumentDerivativeVisitor<ForexBlackSmileProviderInterface, MultipleCurrencyMulticurveSensitivity> _curveSensitivityCalculator;

  /**
   * The constructor from a curve sensitivity calculator.
   * @param curveSensitivityCalculator The calculator.
   */
  public AbstractParameterSensitivityForexBlackSmileCalculator(final InstrumentDerivativeVisitor<ForexBlackSmileProviderInterface, MultipleCurrencyMulticurveSensitivity> curveSensitivityCalculator) {
    ArgumentChecker.notNull(curveSensitivityCalculator, "Sensitivity calculator");
    _curveSensitivityCalculator = curveSensitivityCalculator;
  }

  /**
   * Computes the sensitivity with respect to the parameters.
   * @param instrument The instrument. Not null.
   * @param multicurves The multi-curve provider. Not null.
   * @param curvesSet The set of curves for which the sensitivity will be computed. The multi-curve may contain more curves and other curves can be in the
   * instrument sensitivity but only the one in the set will be in the output. The curve order in the output is the set order.
   * @return The sensitivity (as a ParameterSensitivity).
   */
  public MultipleCurrencyParameterSensitivity calculateSensitivity(final InstrumentDerivative instrument, final ForexBlackSmileProviderInterface multicurves, final Set<String> curvesSet) {
    Validate.notNull(instrument, "null InterestRateDerivative");
    Validate.notNull(multicurves, "null multicurve");
    Validate.notNull(curvesSet, "null curves set");
    MultipleCurrencyMulticurveSensitivity sensitivity = instrument.accept(_curveSensitivityCalculator, multicurves);
    sensitivity = sensitivity.cleaned(); // TODO: for testing purposes mainly. Could be removed after the tests.
    return pointToParameterSensitivity(sensitivity, multicurves, curvesSet);
  }

  /**
   * Computes the sensitivity with respect to the parameters from the point sensitivities to the continuously compounded rate.
   * @param sensitivity The point sensitivity.
   * @param multicurves The multi-curve provider. Not null.
   * @param curvesSet The set of curves for which the sensitivity will be computed. The multi-curve may contain more curves and other curves can be in the
   * instrument sensitivity but only the one in the set will be in the output. The curve order in the output is the set order.
   * @return The sensitivity (as a ParameterSensitivity).
   */
  public abstract MultipleCurrencyParameterSensitivity pointToParameterSensitivity(final MultipleCurrencyMulticurveSensitivity sensitivity, final ForexBlackSmileProviderInterface multicurves,
      final Set<String> curvesSet);

}
