/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.livedata;

import java.util.HashSet;
import java.util.Set;

import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.util.ArgumentChecker;

/**
 * 
 *
 * @author kirk
 */
public class FixedLiveDataAvailabilityProvider implements
    LiveDataAvailabilityProvider {
  private final Set<ValueRequirement> _availableRequirements = new HashSet<ValueRequirement>();

  @Override
  public boolean isAvailable(ValueRequirement requirement) {
    return _availableRequirements.contains(requirement);
  }
  
  public void addRequirement(ValueRequirement requirement) {
    ArgumentChecker.checkNotNull(requirement, "Value requirement");
    _availableRequirements.add(requirement);
  }

}
