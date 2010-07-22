/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.option;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.option.definition.SupershareOptionDefinition;
import com.opengamma.financial.model.option.pricing.analytic.AnalyticOptionModel;
import com.opengamma.financial.model.option.pricing.analytic.SupershareOptionModel;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.financial.security.option.SupersharePayoffStyle;

/**
 * 
 */
public class SupershareOptionModelFunction extends StandardOptionDataAnalyticOptionModelFunction {
  private final AnalyticOptionModel<SupershareOptionDefinition, StandardOptionDataBundle> _model = new SupershareOptionModel();

  @SuppressWarnings("unchecked")
  @Override
  protected AnalyticOptionModel<SupershareOptionDefinition, StandardOptionDataBundle> getModel() {
    return _model;
  }

  @Override
  protected OptionDefinition getOptionDefinition(final OptionSecurity option) {
    final SupersharePayoffStyle payoff = (SupersharePayoffStyle) option.getPayoffStyle();
    return new SupershareOptionDefinition(option.getExpiry(), payoff.getLowerBound(), payoff.getUpperBound());
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    if (target.getSecurity() instanceof OptionSecurity && ((OptionSecurity) target.getSecurity()).getPayoffStyle() instanceof SupersharePayoffStyle) {
      return true;
    }
    return false;
  }

  @Override
  public String getShortName() {
    return "SupershareOptionModel";
  }

}
