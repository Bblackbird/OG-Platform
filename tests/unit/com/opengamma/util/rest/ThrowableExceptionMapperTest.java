/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.rest;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * Test ThrowableExceptionMapper.
 */
public class ThrowableExceptionMapperTest extends AbstractExceptionMapperTestHelper {

  @Test
  public void test_mapping() throws Exception {
    NullPointerException ex = new NullPointerException("Test message");
    ThrowableExceptionMapper mapper = new ThrowableExceptionMapper();
    init(mapper);
    
    Response test = mapper.toResponse(ex);
    testResult(test, Status.INTERNAL_SERVER_ERROR, ex);
  }

}
