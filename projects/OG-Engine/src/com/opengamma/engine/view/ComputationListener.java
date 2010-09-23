/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import com.opengamma.livedata.msg.UserPrincipal;

/**
 * 
 */
public interface ComputationListener {
  
  /**
   * @return The user associated with this listener. This value must be final
   * - it must not change during the lifetime of the listener.
   */
  UserPrincipal getUser();
  
  // PL 27/5/2010: add methods to inform listener of losing/gaining access to a view
  // due to user permission changes

}
