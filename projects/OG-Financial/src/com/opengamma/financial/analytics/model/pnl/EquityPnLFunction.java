/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.pnl;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.financial.timeseries.returns.TimeSeriesReturnCalculator;
import com.opengamma.financial.timeseries.returns.TimeSeriesReturnCalculatorFactory;
import com.opengamma.util.timeseries.DoubleTimeSeries;

/**
 * 
 */
public class EquityPnLFunction extends AbstractFunction.NonCompiledInvoker {

  private final TimeSeriesReturnCalculator _returnCalculator;

  public EquityPnLFunction(final String returnCalculatorName) {
    _returnCalculator = TimeSeriesReturnCalculatorFactory.getReturnCalculator(returnCalculatorName);
  }
  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final Position position = target.getPosition();
    final Object fairValueObj = inputs.getValue(new ValueRequirement(ValueRequirementNames.FAIR_VALUE, ComputationTargetType.SECURITY, position.getSecurity().getUniqueIdentifier()));
    final Object returnSeriesObj = inputs.getValue(new ValueRequirement(ValueRequirementNames.PRICE_SERIES, ComputationTargetType.SECURITY, position.getSecurity().getUniqueIdentifier()));
    if (fairValueObj != null && returnSeriesObj != null) {
      final Double fairValue = (Double) fairValueObj;
      final DoubleTimeSeries<?> returnSeries = _returnCalculator.evaluate((DoubleTimeSeries<?>) returnSeriesObj);
      final ValueSpecification valueSpecification = new ValueSpecification(new ValueRequirement(ValueRequirementNames.PNL_SERIES, position), getUniqueIdentifier());
      //TODO how do we get dividend data for an equity?
      final ComputedValue result = new ComputedValue(valueSpecification, returnSeries.multiply(fairValue).multiply(position.getQuantity().doubleValue()));
      return Sets.newHashSet(result);
    }
    return null;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getType() == ComputationTargetType.POSITION && target.getPosition().getSecurity() instanceof EquitySecurity;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target) {
    if (canApplyTo(context, target)) {
      final Position position = target.getPosition();
      final EquitySecurity equity = (EquitySecurity) position.getSecurity();
      final Set<ValueRequirement> requirements = new HashSet<ValueRequirement>();
      requirements.add(new ValueRequirement(ValueRequirementNames.FAIR_VALUE, ComputationTargetType.SECURITY, equity.getUniqueIdentifier()));
      requirements.add(new ValueRequirement(ValueRequirementNames.PRICE_SERIES, ComputationTargetType.SECURITY, equity.getUniqueIdentifier()));
      // TODO need to consider fx here?
      return requirements;
    }
    return null;
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    if (canApplyTo(context, target)) {
      return Sets.newHashSet(new ValueSpecification(new ValueRequirement(ValueRequirementNames.PNL_SERIES, target.getPosition()), getUniqueIdentifier()));
    }
    return null;
  }

  @Override
  public String getShortName() {
    return "EquityPnL";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.POSITION;
  }

}
