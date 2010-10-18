/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.view.cache.msg.CacheMessage;
import com.opengamma.engine.view.cache.msg.CacheMessageVisitor;
import com.opengamma.transport.FudgeConnection;
import com.opengamma.transport.FudgeConnectionReceiver;
import com.opengamma.transport.FudgeConnectionStateListener;
import com.opengamma.transport.FudgeMessageReceiver;

/**
 * Composite server class for dispatching calls to a {@link IdentifierMapServer} and 
 * {@link BinaryDataStoreServer} within the same JVM.
 */
public class ViewComputationCacheServer implements FudgeConnectionReceiver, FudgeConnectionStateListener {

  private static final Logger s_logger = LoggerFactory.getLogger(ViewComputationCacheServer.class);

  private final IdentifierMapServer _identifierMap;
  private final BinaryDataStoreServer _binaryDataStore;

  public ViewComputationCacheServer(final IdentifierMapServer identifierMap, final BinaryDataStoreServer binaryDataStore) {
    _identifierMap = identifierMap;
    _binaryDataStore = binaryDataStore;
  }

  public ViewComputationCacheServer(final DefaultViewComputationCacheSource cacheSource) {
    this(new IdentifierMapServer(cacheSource.getIdentifierMap()), new BinaryDataStoreServer(cacheSource));
  }

  protected IdentifierMapServer getIdentifierMap() {
    return _identifierMap;
  }

  protected BinaryDataStoreServer getBinaryDataStore() {
    return _binaryDataStore;
  }

  private class MessageHandler extends CacheMessageVisitor implements FudgeMessageReceiver {

    private final FudgeConnection _connection;
    private final CacheMessageVisitor _binaryDataStore;

    public MessageHandler(final FudgeConnection connection) {
      _connection = connection;
      _binaryDataStore = ViewComputationCacheServer.this.getBinaryDataStore().onNewConnection(connection);
    }

    private CacheMessageVisitor getBinaryDataStore() {
      return _binaryDataStore;
    }

    private FudgeConnection getConnection() {
      return _connection;
    }

    @Override
    protected <T extends CacheMessage> T visitUnexpectedMessage(final CacheMessage message) {
      s_logger.warn("Unexpected message - {}", message);
      return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends CacheMessage> T visitBinaryDataStoreMessage(final CacheMessage message) {
      return (T) message.accept(getBinaryDataStore());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T extends CacheMessage> T visitIdentifierMapMessage(final CacheMessage message) {
      return (T) message.accept(getIdentifierMap());
    }

    @Override
    public void messageReceived(final FudgeContext context, final FudgeMsgEnvelope message) {
      final FudgeDeserializationContext dctx = new FudgeDeserializationContext(context);
      final CacheMessage request = dctx.fudgeMsgToObject(CacheMessage.class, message.getMessage());
      CacheMessage response = request.accept(this);
      if (response == null) {
        if (request.getCorrelationId() != null) {
          response = new CacheMessage();
        }
      }
      if (response != null) {
        response.setCorrelationId(request.getCorrelationId());
        final FudgeSerializationContext sctx = new FudgeSerializationContext(context);
        final MutableFudgeFieldContainer responseMsg = sctx.objectToFudgeMsg(response);
        // We have only one response type for each request, so don't really need the headers
        // FudgeSerializationContext.addClassHeader(responseMsg, response.getClass(), CacheMessage.class);
        getConnection().getFudgeMessageSender().send(responseMsg);
      }
    }

  };

  @Override
  public void connectionReceived(final FudgeContext fudgeContext, final FudgeMsgEnvelope message, final FudgeConnection connection) {
    connection.setConnectionStateListener(this);
    final MessageHandler handler = new MessageHandler(connection);
    handler.messageReceived(fudgeContext, message);
    connection.setFudgeMessageReceiver(handler);
  }

  @Override
  public void connectionFailed(final FudgeConnection connection, Exception cause) {
    getBinaryDataStore().onDroppedConnection(connection);
  }

  @Override
  public void connectionReset(final FudgeConnection connection) {
    // Shouldn't happen
  }

}
