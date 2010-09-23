/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.OpenGammaRuntimeException;

/**
 * 
 */
public class MarketDataRequirementNamesHelper {
  
  public static Set<String> constructValidRequirementNames() {
    Set<String> result = new HashSet<String>();
    
    // All fields are implicitly public static final
    assert MarketDataRequirementNames.class.isInterface();
    
    try {
      for (Field field : MarketDataRequirementNames.class.getFields()) {
        if (String.class.equals(field.getType())) {
          result.add((String) field.get(null));
        }
      }
    } catch (Exception e) { 
      throw new OpenGammaRuntimeException("Error querying fields of " + MarketDataRequirementNames.class);
    }
    
    return result;
  }
  
}
