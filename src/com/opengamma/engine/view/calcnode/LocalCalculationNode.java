/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode;

import java.util.concurrent.ExecutorService;

import com.opengamma.engine.ComputationTargetResolver;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionRepository;
import com.opengamma.engine.view.cache.ViewComputationCacheSource;
import com.opengamma.util.InetAddressUtils;

/**
 * 
 */
public class LocalCalculationNode extends AbstractCalculationNode {

  private static int s_nodeUniqueID;

  private static synchronized String createNodeId() {
    final StringBuilder sb = new StringBuilder();
    sb.append(InetAddressUtils.getLocalHostName());
    sb.append('/');
    sb.append(System.getProperty("opengamma.node.id", "0"));
    sb.append('/');
    sb.append(++s_nodeUniqueID);
    return sb.toString();
  }

  public LocalCalculationNode(ViewComputationCacheSource cacheSource, FunctionRepository functionRepository, FunctionExecutionContext functionExecutionContext,
      ComputationTargetResolver targetResolver, ViewProcessorQuerySender calcNodeQuerySender, ExecutorService writeBehindExecutorService) {
    super(cacheSource, functionRepository, functionExecutionContext, targetResolver, calcNodeQuerySender, createNodeId(), writeBehindExecutorService);
  }

}
