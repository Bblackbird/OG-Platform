/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.riskfactor.option;

import org.apache.commons.lang.NotImplementedException;

import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.security.Security;
import com.opengamma.engine.security.SecurityMaster;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.pnl.UnderlyingType;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.id.IdentifierBundle;

/**
 * 
 * 
 *
 * @author elaine
 */
public class UnderlyingTypeToValueRequirementMapper {

  public static ValueRequirement getValueRequirement(SecurityMaster secMaster, final UnderlyingType underlying, final Security security) {
    if (security instanceof OptionSecurity) {
      final OptionSecurity option = (OptionSecurity) security;
      Security optionUnderlying = secMaster.getSecurity(new IdentifierBundle(option.getUnderlyingIdentifier()));
      switch (underlying) {
        case SPOT_PRICE:
          
          return new ValueRequirement(ValueRequirementNames.MARKET_DATA_HEADER, ComputationTargetType.SECURITY, optionUnderlying.getUniqueIdentifier());
        case SPOT_VOLATILITY:
          throw new NotImplementedException("Don't know how to get spot volatility for " + option.getUniqueIdentifier());
        case IMPLIED_VOLATILITY:
          throw new NotImplementedException("Don't know how to get implied volatility for " + option.getUniqueIdentifier());
        case INTEREST_RATE:
          return new ValueRequirement(ValueRequirementNames.YIELD_CURVE, ComputationTargetType.PRIMITIVE, option
              .getUniqueIdentifier());
        case COST_OF_CARRY:
          throw new NotImplementedException("Don't know how to get cost of carry for " + option.getUniqueIdentifier());
        default:
          throw new NotImplementedException("Don't know how to get ValueRequirement for " + underlying);
      }
    }
    throw new NotImplementedException("Can only get ValueRequirements for options (was " + security + ")");
  }
}
