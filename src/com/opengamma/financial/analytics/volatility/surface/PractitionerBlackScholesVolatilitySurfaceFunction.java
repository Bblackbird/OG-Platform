/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.volatility.surface;

import java.util.HashSet;
import java.util.Set;

import javax.time.calendar.Clock;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.function.FunctionInvoker;
import com.opengamma.engine.security.Security;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.livedata.normalization.MarketDataRequirementNames;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * 
 */
public class PractitionerBlackScholesVolatilitySurfaceFunction extends AbstractFunction implements FunctionInvoker {

  @SuppressWarnings("unchecked")
  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final ZonedDateTime now = Clock.system(TimeZone.UTC).zonedDateTime();
    final OptionSecurity option = (OptionSecurity) target.getSecurity();
    final SecuritySource securityMaster = executionContext.getSecurityMaster();
    final Security underlying = securityMaster.getSecurity(new IdentifierBundle(option.getUnderlyingIdentifier()));
    final ValueRequirement underlyingPriceRequirement = getPriceRequirement(underlying.getUniqueIdentifier());
    final ValueRequirement discountCurveDataRequirement = getDiscountCurveMarketDataRequirement(option.getCurrency().getUniqueIdentifier());
    final YieldAndDiscountCurve discountCurve = (YieldAndDiscountCurve) inputs.getValue(discountCurveDataRequirement);
    final double spotPrice = (Double) inputs.getValue(underlyingPriceRequirement);
    final Expiry expiry = option.getExpiry();
    final double t = DateUtil.getDifferenceInYears(now, expiry.getExpiry().toInstant());
    final double b = discountCurve.getInterestRate(t); // TODO cost-of-carry model
    @SuppressWarnings("unused")
    final StandardOptionDataBundle data = new StandardOptionDataBundle(discountCurve, b, null, spotPrice, now);
    // TODO Map<OptionDefinition, Double> of options that will be used to form surface
    final VolatilitySurface surface = null; // TODO
    final ValueSpecification specification = createResultSpecification(option);
    final ComputedValue result = new ComputedValue(specification, surface);
    return Collections.singleton(result);
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.SECURITY) {
      return false;
    }
    if (target.getSecurity() instanceof OptionSecurity) {
      return true;
    }
    return false;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target) {
    if (canApplyTo(context, target)) {
      final OptionSecurity option = (OptionSecurity) target.getSecurity();
      // TODO: need most liquid options on same underlying OR all options around the strike + time to expiry of this
      // option
      // TODO: need to make sure that these options surround the time to expiry and strike of this option
      // TODO: the surface need only be calculated once per _underlying_, not individual option (as long as point 2
      // above holds)
      final Set<ValueRequirement> optionRequirements = new HashSet<ValueRequirement>();
      final SecuritySource securityMaster = context.getSecurityMaster();
      final Security underlying = securityMaster.getSecurity(new IdentifierBundle(option.getUnderlyingIdentifier()));
      optionRequirements.add(getPriceRequirement(underlying.getUniqueIdentifier()));
      optionRequirements.add(getDiscountCurveMarketDataRequirement(option.getCurrency().getUniqueIdentifier()));
      // TODO: add the other stuff
      return optionRequirements;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    if (canApplyTo(context, target)) {
      return Collections.singleton(createResultSpecification(target.getSecurity()));
    }
    return null;
  }

  @Override
  public String getShortName() {
    return "PractitionerBlackScholesMertonVolatilitySurface";
  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.SECURITY;
  }

  private ValueSpecification createResultSpecification(final Security security) {
    final ValueRequirement resultRequirement = new ValueRequirement(ValueRequirementNames.VOLATILITY_SURFACE, ComputationTargetType.SECURITY, security.getUniqueIdentifier());
    final ValueSpecification resultSpec = new ValueSpecification(resultRequirement);
    return resultSpec;
  }

  private ValueRequirement getPriceRequirement(final UniqueIdentifier uid) {
    return new ValueRequirement(MarketDataRequirementNames.INDICATIVE_VALUE, ComputationTargetType.SECURITY, uid);
  }

  private ValueRequirement getDiscountCurveMarketDataRequirement(final UniqueIdentifier uid) {
    return new ValueRequirement(ValueRequirementNames.YIELD_CURVE, ComputationTargetType.PRIMITIVE, uid);
  }
}
