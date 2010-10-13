/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.function.resolver;

import javax.time.InstantProvider;

import com.opengamma.util.PublicAPI;

/**
 * The function resolver is responsible for matching the requirements of a particular computation target and value requirement
 * to a given function.  It is separated from the FunctionRepository so different implementations can be plugged in and used to
 * match functions given different criteria e.g. Optimized for speed.
 */
@PublicAPI
public interface FunctionResolver {

  CompiledFunctionResolver compile(InstantProvider atInstant);

}
