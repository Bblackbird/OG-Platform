/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.entitlement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.opengamma.id.IdentificationScheme;
import com.opengamma.id.Identifier;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.client.DistributedEntitlementChecker;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.transport.ByteArrayFudgeRequestSender;
import com.opengamma.transport.FudgeRequestDispatcher;
import com.opengamma.transport.InMemoryByteArrayRequestConduit;

/**
 * Integration test between {@link DistributedEntitlementChecker} and {@link EntitlementServer}.
 *
 */
public class DistributedEntitlementCheckerTest {
  
  @Test
  public void testRequestResponse() {
    
    PermissiveLiveDataEntitlementChecker delegate = new PermissiveLiveDataEntitlementChecker();
    EntitlementServer server = new EntitlementServer(delegate); 
    
    FudgeRequestDispatcher fudgeRequestDispatcher = new FudgeRequestDispatcher(server);
    InMemoryByteArrayRequestConduit inMemoryByteArrayRequestConduit = new InMemoryByteArrayRequestConduit(fudgeRequestDispatcher);
    ByteArrayFudgeRequestSender fudgeRequestSender = new ByteArrayFudgeRequestSender(inMemoryByteArrayRequestConduit);
    
    DistributedEntitlementChecker client = new DistributedEntitlementChecker(fudgeRequestSender);
    
    LiveDataSpecification testSpec = new LiveDataSpecification(
        "TestNormalization",
        new Identifier(new IdentificationScheme("test1"), "test1"));
    UserPrincipal megan = new UserPrincipal("megan", "127.0.0.1");
    assertTrue(client.isEntitled(megan, testSpec));
    
  }

}
