/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server.distribution;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.fudgemsg.FudgeFieldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.livedata.LiveDataValueUpdateBean;
import com.opengamma.livedata.server.DistributionSpecification;
import com.opengamma.livedata.server.FieldHistoryStore;
import com.opengamma.livedata.server.Subscription;
import com.opengamma.util.ArgumentChecker;

/**
 * Distributes market data to clients and keeps a history of what has been distributed.
 *
 * @author pietari
 */
public class MarketDataDistributor {
  
  private static final Logger s_logger = LoggerFactory.getLogger(MarketDataDistributor.class);
  
  /**
   * What data should be distributed, how and where.
   */
  private final DistributionSpecification _distributionSpec;
  
  /**
   * Which subscription this distributor belongs to.
   */
  private final Subscription _subscription;
  
  /** These listener(s) actually publish the data */ 
  private final Collection<MarketDataSender> _marketDataSenders;
  
  /**
   * Last known values of ALL fully normalized fields that were
   * sent to clients. This is not the last message as such
   * because the last message might not have included all the fields.
   * Instead, because the last value of ALL fields is stored,
   * this store provides a current snapshot of the entire state of the 
   * market data line.   
   */
  private FieldHistoryStore _lastKnownValues = null;
  
  /** 
   * A history store to be used by the FieldHistoryUpdater normalization rule.
   * Fields stored in this history could either be completely unnormalized, 
   * partially normalized, or fully normalized.   
   */
  private final FieldHistoryStore _history = new FieldHistoryStore();
  
  /**
   * Stores how many normalized messages have been sent to clients.  
   */
  private final AtomicLong _numMessagesSent = new AtomicLong(0);
  
  
  /**
   * @parma distributionSpec What data should be distributed, how and where.
   * @param subscription Which subscription this distributor belongs to.
   * @param marketDataSenderFactory Used to create listener(s) that actually publish the data
   */
  public MarketDataDistributor(DistributionSpecification distributionSpec,
      Subscription subscription,
      MarketDataSenderFactory marketDataSenderFactory) {
    
    ArgumentChecker.notNull(distributionSpec, "Distribution spec");
    ArgumentChecker.notNull(subscription, "Subscription");
    ArgumentChecker.notNull(marketDataSenderFactory, "Market data sender factory");
    
    _distributionSpec = distributionSpec;
    _subscription = subscription;
    _marketDataSenders = marketDataSenderFactory.create(this);
    if (_marketDataSenders == null) {
      throw new IllegalStateException("Null returned by " + marketDataSenderFactory);
    }
  }
  
  public DistributionSpecification getDistributionSpec() {
    return _distributionSpec;
  }

  private synchronized FudgeFieldContainer getLastKnownValues() {
    if (_lastKnownValues == null) {
      return null;
    }
    return _lastKnownValues.getLastKnownValues();
  }
  
  private synchronized void updateLastKnownValues(FudgeFieldContainer lastKnownValue) {
    if (_lastKnownValues == null) {
      _lastKnownValues = new FieldHistoryStore();
    }
    _lastKnownValues.liveDataReceived(lastKnownValue);
  }

  public LiveDataValueUpdateBean getSnapshot() {
    if (getLastKnownValues() == null) {
      return null;
    }
    return new LiveDataValueUpdateBean(
        getNumMessagesSent(), // 0-based as it should be 
        getDistributionSpec().getFullyQualifiedLiveDataSpecification(), 
        getLastKnownValues());
  }
  
  
  public Collection<MarketDataSender> getMarketDataSenders() {
    return Collections.unmodifiableCollection(_marketDataSenders);
  }
  
  public Subscription getSubscription() {
    return _subscription;
  }
  
  public long getNumMessagesSent() {
    return _numMessagesSent.get();
  }
  
  /**
   * @param msg Message received from underlying market data API in its native format.
   * @return The normalized message. Null if in the process of normalization,
   * the message became empty and therefore should not be sent.
   */
  private FudgeFieldContainer normalize(FudgeFieldContainer msg) {
    FudgeFieldContainer normalizedMsg = _distributionSpec.getNormalizedMessage(msg, _history);
    return normalizedMsg;
  }
  
  /**
   * Updates field history without sending any market data to field receivers. 
   * 
   * @param liveDataFields Unnormalized market data from underlying market data API.
   */
  public synchronized void updateFieldHistory(FudgeFieldContainer msg) {
    FudgeFieldContainer normalizedMsg = normalize(msg);
    if (normalizedMsg != null) {
      updateLastKnownValues(normalizedMsg);
    }
  }
  
  /**
   * Sends normalized market data to field receivers. 
   * <p>
   * Serialized to ensure a well-defined distribution order for this topic.
   * 
   * @param liveDataFields Unnormalized market data from underlying market data API.
   */
  public synchronized void distributeLiveData(FudgeFieldContainer liveDataFields) {
    FudgeFieldContainer normalizedMsg;
    try {
      normalizedMsg = normalize(liveDataFields);
    } catch (RuntimeException e) {
      s_logger.error("Normalizing " + liveDataFields + " to " + this + " failed.", e);
      return;
    }
    
    if (normalizedMsg != null) {
      updateLastKnownValues(normalizedMsg);
      
      LiveDataValueUpdateBean data = new LiveDataValueUpdateBean(
          getNumMessagesSent(), // 0-based as it should be
          getDistributionSpec().getFullyQualifiedLiveDataSpecification(),
          normalizedMsg);
      
      s_logger.debug("{}: Sending Live Data update {}", this, data);
      
      for (MarketDataSender sender : _marketDataSenders) {
        try {
          sender.sendMarketData(data);
        } catch (RuntimeException e) {
          s_logger.error(sender + " failed", e);
        }
      }
      
      _numMessagesSent.incrementAndGet();
    
    } else {
      s_logger.debug("{}: Not sending Live Data update (message extinguished).", this);
    }
  }
  
  @Override
  public String toString() {
    return "MarketDataDistributor[" + getDistributionSpec().toString() +  "]";    
  }

  
}
