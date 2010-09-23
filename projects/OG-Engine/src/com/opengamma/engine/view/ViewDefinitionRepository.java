/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.util.Set;

/**
 * Allows modules, specifically the {@link ViewProcessor}, access to
 * {@link ViewDefinition}s.
 *
 * @author kirk
 */
public interface ViewDefinitionRepository {
  
  Set<String> getDefinitionNames();
  
  ViewDefinition getDefinition(String definitionName);

}
