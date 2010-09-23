/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.server;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.opengamma.livedata.msg.LiveDataSubscriptionResult;
import com.opengamma.util.ArgumentChecker;

/**
 *
 */
@ManagedResource(
    objectName = "com.opengamma:name=LiveDataServer",
    description = "LiveData server attributes and operations that can be managed via JMX"
    )
public class LiveDataServerMBean {
  
  private static final Logger s_logger = LoggerFactory.getLogger(LiveDataServerMBean.class);
  private final AbstractLiveDataServer _server;
  
  public LiveDataServerMBean(AbstractLiveDataServer server) {
    ArgumentChecker.notNull(server, "Live Data Server");
    _server = server;
  }
  
  @ManagedAttribute(description = "How many different tickers the server subscribes to.")
  public int getNumActiveSubscriptions() {
    try {
      return _server.getNumActiveSubscriptions();
    } catch (RuntimeException e) {
      s_logger.error("getNumActiveSubscriptions() failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedAttribute(description = "Security IDs the server subscribes to."
      + " The form of the IDs is dependent on the source system"
      + " - Reuters RICs, Bloomberg unique IDs, etc.")
  public Set<String> getActiveSubscriptionIds() {
    try {
      return _server.getActiveSubscriptionIds();
    } catch (RuntimeException e) {
      s_logger.error("getActiveSubscriptionIds() failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedAttribute(description = "JMS topics the server publishes to.")
  public Set<String> getActiveDistributionSpecs() {
    try {
      return _server.getActiveDistributionSpecs();
    } catch (RuntimeException e) {
      s_logger.error("getActiveDistributionSpecs() failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedAttribute(description = "The number of market data updates the server has processed in its lifetime.")
  public long getNumMarketDataUpdatesReceived() {
    try {
      return _server.getNumMarketDataUpdatesReceived();
    } catch (RuntimeException e) {
      s_logger.error("getNumLiveDataUpdatesSent() failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedAttribute(description = "# of market data updates/sec, calculated over the last 60 seconds")
  public double getNumLiveDataUpdatesSentPerSecondOverLastMinute() {
    try {
      return _server.getNumLiveDataUpdatesSentPerSecondOverLastMinute();            
    } catch (RuntimeException e) {
      s_logger.error("getNumLiveDataUpdatesSentPerSecondOverLastMinute() failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedOperation(description = "Subscribes to market data. The subscription will be non-persistent."
      + " If the server already subscribes to the given market data, this method is a "
      + " no-op. Returns the name of the JMS topic market data will be published on.")
  @ManagedOperationParameters({
      @ManagedOperationParameter(name = "securityUniqueId", description = "Security unique ID. Server type dependent.)") })
  public String subscribe(String securityUniqueId) {
    try {
      SubscriptionResult result = _server.subscribe(securityUniqueId);
      if (result.getResult() != LiveDataSubscriptionResult.SUCCESS) {
        throw result.getException();
      }
      DistributionSpecification distributionSpec = result.getDistributionSpecification();
      return distributionSpec.getJmsTopic();
    } catch (RuntimeException e) {
      s_logger.error("subscribe(" + securityUniqueId + ") failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }
  
  @ManagedOperation(description = "Unsubscribes from market data. "
      + "Works even if the subscription is persistent. "
      + "Returns true if a market data subscription was actually removed,"
      + " false otherwise.")
  @ManagedOperationParameters({
       @ManagedOperationParameter(name = "securityUniqueId", description = "Security unique ID. Server type dependent.)") })
  public boolean unsubscribe(String securityUniqueId) {
    try {
      return _server.unsubscribe(securityUniqueId); 
    } catch (RuntimeException e) {
      s_logger.error("unsubscribe(" + securityUniqueId + ") failed", e);
      throw new RuntimeException(e.getMessage());
    }
  }

}
