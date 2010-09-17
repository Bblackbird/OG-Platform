/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calcnode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.view.cache.IdentifierMap;
import com.opengamma.engine.view.calcnode.msg.Init;
import com.opengamma.engine.view.calcnode.msg.Ready;
import com.opengamma.engine.view.calcnode.msg.RemoteCalcNodeMessage;
import com.opengamma.engine.view.calcnode.msg.RemoteCalcNodeMessageVisitor;
import com.opengamma.engine.view.calcnode.stats.FunctionCost;
import com.opengamma.transport.FudgeConnection;
import com.opengamma.transport.FudgeConnectionReceiver;

/**
 * Server end to RemoteNodeClient to receive requests from remote calculation nodes and marshal
 * them into RemoteNodeJobInvokers that a JobDispatcher can then use.
 */
public class RemoteNodeServer implements FudgeConnectionReceiver {

  private static final Logger s_logger = LoggerFactory.getLogger(RemoteNodeServer.class);

  private final JobInvokerRegister _jobInvokerRegister;
  private final IdentifierMap _identifierMap;
  private final ExecutorService _executorService = Executors.newCachedThreadPool();
  private final FunctionCost _functionCost;
  private Set<Capability> _capabilitiesToAdd;

  public RemoteNodeServer(final JobInvokerRegister jobInvokerRegister, final IdentifierMap identifierMap, final FunctionCost functionCost) {
    _jobInvokerRegister = jobInvokerRegister;
    _identifierMap = identifierMap;
    _functionCost = functionCost;
  }

  /**
   * Specify capabilities to add to those explicitly declared by the remote nodes. If the nodes declare these
   * in the initial connection they will be overridden. After the initial connection any changes the node
   * sends will take effect again.
   * 
   * @param parameters Capabilities to add
   */
  public void setCapabilitiesToAdd(final Map<String, Double> parameters) {
    _capabilitiesToAdd = new HashSet<Capability>();
    for (Map.Entry<String, Double> parameter : parameters.entrySet()) {
      _capabilitiesToAdd.add(Capability.parameterInstanceOf(parameter.getKey(), parameter.getValue()));
    }
  }

  protected JobInvokerRegister getJobInvokerRegister() {
    return _jobInvokerRegister;
  }

  protected ExecutorService getExecutorService() {
    return _executorService;
  }

  protected IdentifierMap getIdentifierMap() {
    return _identifierMap;
  }

  protected FunctionCost getFunctionCost() {
    return _functionCost;
  }

  @Override
  public void connectionReceived(final FudgeContext fudgeContext, final FudgeMsgEnvelope message, final FudgeConnection connection) {
    final FudgeDeserializationContext context = new FudgeDeserializationContext(fudgeContext);
    final RemoteCalcNodeMessage remoteCalcNodeMessage = context.fudgeMsgToObject(RemoteCalcNodeMessage.class, message.getMessage());
    remoteCalcNodeMessage.accept(new RemoteCalcNodeMessageVisitor() {

      @Override
      protected void visitUnexpectedMessage(final RemoteCalcNodeMessage message) {
        s_logger.warn("Unexpected message {}", message);
      }

      @Override
      protected void visitReadyMessage(final Ready message) {
        s_logger.info("Remote node connected - {}", connection);
        final RemoteNodeJobInvoker invoker = new RemoteNodeJobInvoker(getExecutorService(), message, connection, getIdentifierMap(), getFunctionCost());
        if (_capabilitiesToAdd != null) {
          invoker.addCapabilities(_capabilitiesToAdd);
        }
        final Init init = new Init();
        // TODO any parameters we need to send to initialise the node
        invoker.sendMessage(init);
        getJobInvokerRegister().registerJobInvoker(invoker);
      }

    });
  }

}
