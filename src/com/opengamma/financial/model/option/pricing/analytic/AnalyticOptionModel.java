/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import java.util.List;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResult;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.greeks.GreekVisitor;
import com.opengamma.financial.greeks.SingleGreekResult;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.pricing.FiniteDifferenceGreekVisitor;
import com.opengamma.financial.model.option.pricing.OptionModel;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.NormalProbabilityDistribution;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 * @author emcleod
 * 
 */
public abstract class AnalyticOptionModel<T extends OptionDefinition, U extends StandardOptionDataBundle> implements OptionModel<T, U> {

  public abstract Function1D<U, Double> getPricingFunction(T definition);

  public GreekVisitor<GreekResult<?>> getGreekVisitor(final Function1D<U, Double> pricingFunction, final U data, final T definition) {
    return new AnalyticOptionModelFiniteDifferenceGreekVisitor<U, T>(pricingFunction, data, definition);
  }

  @Override
  public GreekResultCollection getGreeks(final T definition, final U data, final List<Greek> requiredGreeks) {
    final Function1D<U, Double> pricingFunction = getPricingFunction(definition);
    final GreekResultCollection results = new GreekResultCollection();
    final GreekVisitor<GreekResult<?>> visitor = getGreekVisitor(pricingFunction, data, definition);
    for (final Greek greek : requiredGreeks) {
      final GreekResult<?> result = greek.accept(visitor);
      results.put(greek, result);
    }
    return results;
  }

  protected double getD1(final double s, final double k, final double t, final double sigma, final double b) {
    return (Math.log(s / k) + t * (b + sigma * sigma / 2)) / (sigma * Math.sqrt(t));
  }

  protected double getD2(final double d1, final double sigma, final double t) {
    return d1 - sigma * Math.sqrt(t);
  }

  protected double getDF(final double r, final double b, final double t) {
    return Math.exp(t * (b - r));
  }

  protected class AnalyticOptionModelFiniteDifferenceGreekVisitor<S extends StandardOptionDataBundle, R extends OptionDefinition> extends FiniteDifferenceGreekVisitor<S, R> {
    private static final double EPS = 1e-3;
    private final S _data;
    private final R _definition;
    private final ProbabilityDistribution<Double> _normal = new NormalProbabilityDistribution(0, 1);

    public AnalyticOptionModelFiniteDifferenceGreekVisitor(final Function1D<S, Double> pricingFunction, final S data, final R definition) {
      super(pricingFunction, data, definition);
      _data = data;
      _definition = definition;
    }

    @Override
    public GreekResult<?> visitDZetaDVol() {
      final double s = _data.getSpot();
      final double k = _definition.getStrike();
      final double t = _definition.getTimeToExpiry(_data.getDate());
      final double b = _data.getCostOfCarry();
      final double sigma = _data.getVolatility(t, k);
      final int sign = _definition.isCall() ? 1 : -1;
      final double nUp = _normal.getCDF(sign * getD2(getD1(s, k, t, sigma + EPS, b), sigma + EPS, t));
      final double nDown = _normal.getCDF(sign * getD2(getD1(s, k, t, sigma - EPS, b), sigma - EPS, t));
      return new SingleGreekResult((nUp - nDown) / (2 * EPS));
    }
  }
}
