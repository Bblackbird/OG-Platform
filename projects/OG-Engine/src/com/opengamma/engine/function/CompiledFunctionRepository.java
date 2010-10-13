/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.function;

import java.util.Collection;

import javax.time.Instant;

/**
 * A container for the {@link CompiledFunctionDefinition} instances available
 * to a particular environment at a particular time.
 */
public interface CompiledFunctionRepository {

  Collection<CompiledFunctionDefinition> getAllFunctions();

  CompiledFunctionDefinition getDefinition(String uniqueIdentifier);

  FunctionInvoker getInvoker(String uniqueIdentifier);

  FunctionCompilationContext getCompilationContext();

  /**
   * Returns the earliest time at which all functions can be successfully executed, or {@code null}
   * if no functions have a limit.
   * 
   * @return timestamp, not {@code null}.
   */
  Instant getEarliestInvocationTime();

  /**
   * Returns the latest time at which all functions can be successfully executed, or {@code null}
   * if no functions have a limit.
   * 
   * @return timestamp, not {@code null}.
   */
  Instant getLatestInvocationTime();

}
