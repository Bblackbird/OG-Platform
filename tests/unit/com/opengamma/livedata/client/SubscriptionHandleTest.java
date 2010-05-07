/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.fudgemsg.FudgeContext;
import org.junit.Test;

import com.opengamma.id.Identifier;
import com.opengamma.livedata.CollectingLiveDataListener;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.LiveDataValueUpdateBean;
import com.opengamma.livedata.msg.SubscriptionType;
import com.opengamma.livedata.msg.UserPrincipal;

/**
 * 
 *
 * @author kirk
 */
public class SubscriptionHandleTest {
  
  private final UserPrincipal _user = new UserPrincipal("kirk", "127.0.0.1");

  @Test
  public void equalsDifferentRequestedSpecification() {
    LiveDataSpecification requestedSpecification =
      new LiveDataSpecification(
          "NormalizationId1",
          new Identifier("Domain1", "Value1"));
    CollectingLiveDataListener listener = new CollectingLiveDataListener();
    SubscriptionHandle handle1 = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, requestedSpecification, listener);
    SubscriptionHandle handle2 = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, requestedSpecification,  listener);
    assertTrue(handle1.equals(handle2));
  }

  @Test
  public void hashCodeDifferentRequestedSpecification() {
    LiveDataSpecification requestedSpecification =
      new LiveDataSpecification(
          "NormalizationId1",
          new Identifier("Domain1", "Value1"));
    CollectingLiveDataListener listener = new CollectingLiveDataListener();
    SubscriptionHandle handle1 = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, requestedSpecification, listener);
    SubscriptionHandle handle2 = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, requestedSpecification, listener);
    assertEquals(handle1.hashCode(), handle2.hashCode());
  }
  
  @Test
  public void releaseTicks() {
    LiveDataSpecification spec =
      new LiveDataSpecification(
          "NormalizationId1",
          new Identifier("Domain1", "Value1"));
    CollectingLiveDataListener listener = new CollectingLiveDataListener();
    SubscriptionHandle handle = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, spec, listener);
    
    handle.addTickOnHold(new LiveDataValueUpdateBean(500, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addSnapshotOnHold(new LiveDataValueUpdateBean(501, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(502, spec, FudgeContext.EMPTY_MESSAGE));
    handle.releaseTicksOnHold();
    
    assertEquals(2, listener.getValueUpdates().size());
    
    assertEquals(501, listener.getValueUpdates().get(0).getSequenceNumber());
    assertEquals(502, listener.getValueUpdates().get(1).getSequenceNumber());
  }
  
  @Test
  public void releaseTicksServerRestart() {
    LiveDataSpecification spec =
      new LiveDataSpecification(
          "NormalizationId1",
          new Identifier("Domain1", "Value1"));
    CollectingLiveDataListener listener = new CollectingLiveDataListener();
    SubscriptionHandle handle = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, spec, listener);
    
    handle.addTickOnHold(new LiveDataValueUpdateBean(500, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(501, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addSnapshotOnHold(new LiveDataValueUpdateBean(502, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(0, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(1, spec, FudgeContext.EMPTY_MESSAGE));
    handle.releaseTicksOnHold();
    
    assertEquals(2, listener.getValueUpdates().size());
    
    assertEquals(0, listener.getValueUpdates().get(0).getSequenceNumber());
    assertEquals(1, listener.getValueUpdates().get(1).getSequenceNumber());
  }
  
  @Test
  public void releaseTicksMultipleServerRestarts() {
    LiveDataSpecification spec =
      new LiveDataSpecification(
          "NormalizationId1",
          new Identifier("Domain1", "Value1"));
    CollectingLiveDataListener listener = new CollectingLiveDataListener();
    SubscriptionHandle handle = new SubscriptionHandle(_user, SubscriptionType.NON_PERSISTENT, spec, listener);
    
    handle.addTickOnHold(new LiveDataValueUpdateBean(500, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addSnapshotOnHold(new LiveDataValueUpdateBean(501, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(502, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(0, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(1, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(0, spec, FudgeContext.EMPTY_MESSAGE));
    handle.addTickOnHold(new LiveDataValueUpdateBean(1, spec, FudgeContext.EMPTY_MESSAGE));
    handle.releaseTicksOnHold();
    
    assertEquals(2, listener.getValueUpdates().size());
    
    assertEquals(0, listener.getValueUpdates().get(0).getSequenceNumber());
    assertEquals(1, listener.getValueUpdates().get(1).getSequenceNumber());
  }
}
