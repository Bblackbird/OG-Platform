/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing;

import java.util.Set;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.model.option.definition.OptionDefinition;

/**
 * 
 * @param <T>
 * @param <U>
 */
public interface OptionModel<T extends OptionDefinition, U> {

  GreekResultCollection getGreeks(T definition, U data, Set<Greek> requiredGreeks);

}
