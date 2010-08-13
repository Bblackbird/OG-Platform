/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.transport;

/**
 * Bi-directional and asymmetric connection for Fudge messages. Comprises a FudgeMessageReceiver and FudgeMessageSender.
 */
public interface FudgeConnection {

  void setFudgeMessageReceiver(FudgeMessageReceiver receiver);

  FudgeMessageSender getFudgeMessageSender();

}
