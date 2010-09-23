/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode;

/**
 * Callback interface to receive the result state of a {@link JobInvoker#invoke} call.
 */
public interface JobInvocationReceiver {

  void jobCompleted(CalculationJobResult result);

  void jobFailed(JobInvoker jobInvoker, String computeNodeId, Exception exception);

}
