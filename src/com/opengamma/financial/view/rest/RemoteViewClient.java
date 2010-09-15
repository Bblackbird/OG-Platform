/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.view.rest;

import java.net.URI;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.ws.rs.core.UriBuilder;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsgEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.opengamma.engine.view.ComputationResultListener;
import com.opengamma.engine.view.DeltaComputationResultListener;
import com.opengamma.engine.view.View;
import com.opengamma.engine.view.ViewComputationResultModel;
import com.opengamma.engine.view.ViewDeltaResultModel;
import com.opengamma.engine.view.client.ViewClient;
import com.opengamma.engine.view.client.ViewClientState;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.transport.ByteArrayFudgeMessageReceiver;
import com.opengamma.transport.FudgeMessageReceiver;
import com.opengamma.transport.jms.JmsByteArrayMessageDispatcher;
import com.opengamma.util.rest.FudgeRestClient;

/**
 * Provides access to a remote {@link ViewClient}.
 */
public class RemoteViewClient implements ViewClient {

  private static final Logger s_logger = LoggerFactory.getLogger(RemoteViewClient.class);
  
  private final View _view;
  private final URI _baseUri;
  private final FudgeRestClient _client;
  
  private final ReentrantLock _listenerLock = new ReentrantLock();
  private DeltaComputationResultListener _deltaListener;
  private DefaultMessageListenerContainer _deltaListenerContainer;
  private ComputationResultListener _resultListener;
  private DefaultMessageListenerContainer _resultListenerContainer;
  
  private final FudgeContext _fudgeContext;
  private final JmsTemplate _jmsTemplate;
  
  public RemoteViewClient(View view, URI baseUri, FudgeContext fudgeContext, JmsTemplate jmsTemplate) {
    _view = view;
    _baseUri = baseUri;
    _client = FudgeRestClient.create();
    _fudgeContext = fudgeContext;
    _jmsTemplate = jmsTemplate;
  }

  //-------------------------------------------------------------------------
  @Override
  public UniqueIdentifier getUniqueIdentifier() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_UNIQUE_IDENTIFIER);
    return _client.access(uri).get(UniqueIdentifier.class);
  }
  
  @Override
  public View getView() {
    return _view;
  }
  
  @Override
  public UserPrincipal getUser() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_USER);
    return _client.access(uri).get(UserPrincipal.class);
  }

  @Override
  public boolean isResultAvailable() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_RESULT_AVAILABLE);
    return _client.access(uri).get(boolean.class);
  }
  
  @Override
  public ViewComputationResultModel getLatestResult() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_LATEST_RESULT);
    return _client.access(uri).get(ViewComputationResultModel.class);
  }
  
  @Override
  public ViewClientState getState() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_STATE);
    return _client.access(uri).get(ViewClientState.class);
  }

  @Override
  public void runOneCycle() {
    // TOOD: implement
  }
  
  @Override
  public void startLive() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_START);
    _client.access(uri).post();
  }

  @Override
  public void pauseLive() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_PAUSE);
    _client.access(uri).post();
  }
  
  @Override
  public void stopLive() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_STOP);
    _client.access(uri).post();    
  }
  
  @Override
  public void shutdown() {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_SHUTDOWN);
    _client.access(uri).post();
  }

  @Override
  public void setDeltaResultListener(DeltaComputationResultListener newListener) {
    _listenerLock.lock();
    try {
      DeltaComputationResultListener oldListener = _deltaListener;
      _deltaListener = newListener;
      if (oldListener == null && newListener != null) {
        // Set up subscription
        URI uri = getUri(_baseUri, DataViewClientResource.PATH_START_JMS_RESULT_STREAM);
        String topicName = _client.access(uri).post(String.class);
        initDeltaListener(topicName);
      } else if (oldListener != null && newListener == null) {
        URI uri = getUri(_baseUri, DataViewClientResource.PATH_STOP_JMS_DELTA_STREAM);
        _client.access(uri).post();
        tearDownDeltaListener();
      }
    } finally {
      _listenerLock.unlock();
    }
  }

  private void initDeltaListener(final String topicName) {
    s_logger.info("Set up Delta JMS subscription to {}", topicName);
    _deltaListenerContainer = new DefaultMessageListenerContainer();
    _deltaListenerContainer.setConnectionFactory(_jmsTemplate.getConnectionFactory());
    _deltaListenerContainer.setMessageListener(new JmsByteArrayMessageDispatcher(new ByteArrayFudgeMessageReceiver(new FudgeMessageReceiver() {
      @Override
      public void messageReceived(FudgeContext fudgeContext, FudgeMsgEnvelope msgEnvelope) {
        s_logger.debug("Delta message received on {}", topicName);
        ViewDeltaResultModel resultModel = null;
        try {
          resultModel = fudgeContext.fromFudgeMsg(ViewDeltaResultModel.class, msgEnvelope.getMessage());
        } catch (Exception e) {
          s_logger.warn("Disregarding delta message because couldn't parse: {}", msgEnvelope.getMessage());
          s_logger.warn("Underlying parse error", e);
          return;
        }
        dispatchDeltaResult(resultModel);
      }
    }, _fudgeContext)));
    _deltaListenerContainer.setDestinationName(topicName);
    _deltaListenerContainer.setPubSubDomain(true);
    _deltaListenerContainer.setExceptionListener(new ExceptionListener() {
      @Override
      public void onException(JMSException exception) {
        s_logger.warn("Error in Delta receiver", exception);
      }
    });
    _deltaListenerContainer.afterPropertiesSet();
    _deltaListenerContainer.start();
  }
  
  private void dispatchDeltaResult(ViewDeltaResultModel deltaResult) {
    s_logger.debug("Received a delta result {}", deltaResult.getResultTimestamp());
    DeltaComputationResultListener listener = _deltaListener;
    if (listener != null) {
      listener.deltaResultAvailable(deltaResult);
    }
  }
  
  private void tearDownDeltaListener() {
    _deltaListenerContainer.stop();
    _deltaListenerContainer.destroy();
    _deltaListenerContainer = null;
  }

  @Override
  public void setResultListener(ComputationResultListener newListener) {
    _listenerLock.lock();
    try {
      ComputationResultListener oldListener = _resultListener;
      _resultListener = newListener;
      if (oldListener == null && newListener != null) {
        // Set up subscription
        URI uri = getUri(_baseUri, DataViewClientResource.PATH_START_JMS_RESULT_STREAM);
        String topicName = _client.access(uri).post(String.class);
        initResultListener(topicName);
      } else if (oldListener != null && newListener == null) {
        URI uri = getUri(_baseUri, DataViewClientResource.PATH_STOP_JMS_RESULT_STREAM);
        _client.access(uri).post();
        tearDownResultListener();
      }
    } finally {
      _listenerLock.unlock();
    }
  }
  
  private void initResultListener(final String topicName) {
    s_logger.info("Set up result JMS subscription to {}", topicName);
    _resultListenerContainer = new DefaultMessageListenerContainer();
    _resultListenerContainer.setConnectionFactory(_jmsTemplate.getConnectionFactory());
    _resultListenerContainer.setMessageListener(new JmsByteArrayMessageDispatcher(new ByteArrayFudgeMessageReceiver(new FudgeMessageReceiver() {
      @Override
      public void messageReceived(FudgeContext fudgeContext, FudgeMsgEnvelope msgEnvelope) {
        s_logger.debug("Result message received on {}", topicName);
        ViewComputationResultModel resultModel = null;
        try {
          resultModel = fudgeContext.fromFudgeMsg(ViewComputationResultModel.class, msgEnvelope.getMessage());
        } catch (Exception e) {
          s_logger.warn("Disregarding result message because couldn't parse: {}", msgEnvelope.getMessage());
          s_logger.warn("Underlying parse error", e);
          return;
        }
        dispatchResult(resultModel);
      }
    }, _fudgeContext)));
    _resultListenerContainer.setDestinationName(topicName);
    _resultListenerContainer.setPubSubDomain(true);
    _resultListenerContainer.setExceptionListener(new ExceptionListener() {
      @Override
      public void onException(JMSException exception) {
        s_logger.warn("Error in result receiver", exception);
      }
    });
    _resultListenerContainer.afterPropertiesSet();
    _resultListenerContainer.start();
  }
  
  private void dispatchResult(ViewComputationResultModel result) {
    s_logger.debug("Received a computation result {}", result.getResultTimestamp());
    ComputationResultListener listener = _resultListener;
    if (listener != null) {
      listener.computationResultAvailable(result);
    }
  }
  
  private void tearDownResultListener() {
    _resultListenerContainer.stop();
    _resultListenerContainer.destroy();
    _resultListenerContainer = null;
  }

  @Override
  public void setLiveUpdatePeriod(long periodMillis) {
    URI uri = getUri(_baseUri, DataViewClientResource.PATH_UPDATE_PERIOD);
    _client.access(uri).put(periodMillis);
  }
  
  //-------------------------------------------------------------------------
  private static URI getUri(URI baseUri, String path) {
    return UriBuilder.fromUri(baseUri).build(path);
  }
  
}
