/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server.distribution;

import org.fudgemsg.FudgeMsg;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.livedata.LiveDataValueUpdateBean;
import com.opengamma.livedata.LiveDataValueUpdateFudgeBuilder;
import com.opengamma.transport.FudgeMessageSender;
import com.opengamma.util.ArgumentChecker;

/**
 * A simple implementation of {@link MarketDataSender} that sends
 * market data to a {@link FudgeMessageSender}. 
 * Useful in tests. 
 */
public class FudgeSender implements MarketDataSender {

  private final FudgeMessageSender _fudgeMessageSender;
  private final MarketDataDistributor _distributor;

  public FudgeSender(FudgeMessageSender fudgeMessageSender, MarketDataDistributor distributor) {
    ArgumentChecker.notNull(fudgeMessageSender, "Fudge Message Sender");
    ArgumentChecker.notNull(distributor, "Market Data Distributor");
    _fudgeMessageSender = fudgeMessageSender;
    _distributor = distributor;
  }

  public FudgeMessageSender getFudgeMessageSender() {
    return _fudgeMessageSender;
  }

  @Override
  public MarketDataDistributor getDistributor() {
    return _distributor;
  }

  @Override
  public void sendMarketData(LiveDataValueUpdateBean data) {
    FudgeSerializer serializer = new FudgeSerializer(getFudgeMessageSender().getFudgeContext());
    FudgeMsg fudgeMsg = LiveDataValueUpdateFudgeBuilder.toFudgeMsg(serializer, data);
    getFudgeMessageSender().send(fudgeMsg);
  }

}
