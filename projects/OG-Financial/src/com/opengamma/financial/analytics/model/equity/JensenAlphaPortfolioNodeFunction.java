/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.equity;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;

/**
 * 
 */
public class JensenAlphaPortfolioNodeFunction extends JensenAlphaFunction {

  public JensenAlphaPortfolioNodeFunction(final String returnCalculatorName, final String expectedAssetReturnCalculatorName, final String expectedRiskFreeReturnCalculatorName,
      final String expectedMarketReturnCalculatorName, final String startDate) {
    super(returnCalculatorName, expectedAssetReturnCalculatorName, expectedRiskFreeReturnCalculatorName, expectedMarketReturnCalculatorName, startDate);
  }

  @Override
  public Object getTarget(final ComputationTarget target) {
    return target.getPortfolioNode();
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getType() == ComputationTargetType.PORTFOLIO_NODE;
  }

  @Override
  public String getShortName() {
    return "JensenAlphaPortfolioNodeFunction";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.PORTFOLIO_NODE;
  }

}
