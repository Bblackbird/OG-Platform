/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.transport;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.mapping.FudgeDeserializationContext;

/**
 * Similar to {@link ByteArrayRequestReceiver}, except that all messages are
 * extracted to a {@link FudgeMsgEnvelope} before transmission.
 *
 * @author kirk
 */
public interface FudgeRequestReceiver {
  
  FudgeFieldContainer requestReceived(FudgeDeserializationContext context, FudgeMsgEnvelope requestEnvelope);

}
