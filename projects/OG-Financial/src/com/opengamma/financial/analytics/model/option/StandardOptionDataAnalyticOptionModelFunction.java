/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.option;

import java.util.HashSet;
import java.util.Set;

import javax.time.calendar.Clock;
import javax.time.calendar.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.security.Security;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.id.IdentifierBundle;

/**
 * 
 */
//TODO urgently needs a rename
public abstract class StandardOptionDataAnalyticOptionModelFunction extends AnalyticOptionModelFunction {
  private static final Logger s_logger = LoggerFactory.getLogger(StandardOptionDataAnalyticOptionModelFunction.class);

  @SuppressWarnings("unchecked")
  @Override
  protected StandardOptionDataBundle getDataBundle(final SecuritySource secMaster, final Clock relevantTime, final OptionSecurity option, final FunctionInputs inputs) {
    final ZonedDateTime now = relevantTime.zonedDateTime();
    final Security underlying = secMaster.getSecurity(new IdentifierBundle(option.getUnderlyingIdentifier()));
    final Double spotAsObject = (Double) inputs.getValue(getUnderlyingMarketDataRequirement(underlying.getUniqueIdentifier()));
    if (spotAsObject == null) {
      s_logger.warn("Didn't have market value for {}", option.getUnderlyingIdentifier());
      throw new NullPointerException("No spot value for underlying instrument.");
    }
    final double spot = spotAsObject;
    final YieldAndDiscountCurve discountCurve = (YieldAndDiscountCurve) inputs.getValue(getYieldCurveMarketDataRequirement(option.getCurrency().getUniqueIdentifier()));
    final VolatilitySurface volatilitySurface = (VolatilitySurface) inputs.getValue(getVolatilitySurfaceMarketDataRequirement(option.getUniqueIdentifier()));
    final double b = (Double) inputs.getValue(getCostOfCarryMarketDataRequirement(option.getUniqueIdentifier()));
    return new StandardOptionDataBundle(discountCurve, b, volatilitySurface, spot, now);
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    if (canApplyTo(context, target)) {
      final OptionSecurity option = (OptionSecurity) target.getSecurity();
      final SecuritySource secMaster = context.getSecuritySource();
      final Security underlying = secMaster.getSecurity(new IdentifierBundle(option.getUnderlyingIdentifier()));
      final Set<ValueRequirement> requirements = new HashSet<ValueRequirement>();
      requirements.add(getUnderlyingMarketDataRequirement(underlying.getUniqueIdentifier()));
      requirements.add(getYieldCurveMarketDataRequirement(option.getCurrency().getUniqueIdentifier()));
      requirements.add(getVolatilitySurfaceMarketDataRequirement(option.getUniqueIdentifier()));
      requirements.add(getCostOfCarryMarketDataRequirement(option.getUniqueIdentifier()));
      return requirements;
    }
    return null;
  }

}
