/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.transport.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.FudgeMsgWriter;
import org.fudgemsg.xml.FudgeXMLStreamWriter;

/**
 * Register as a Jax-RS provider to support REST responses that are XML encoded messages. 
 * 
 * @author Andrew Griffin
 */
@Produces("application/xml")
public class FudgeXMLProducer extends FudgeProducer {
  
  public FudgeXMLProducer () {
    super ();
  }
  
  @Override
  public void writeTo(FudgeMsgEnvelope t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    final FudgeMsgWriter writer = new FudgeMsgWriter (new FudgeXMLStreamWriter (getFudgeContext (), new OutputStreamWriter (entityStream)));
    writer.writeMessageEnvelope (t, getFudgeTaxonomyId ());
    writer.flush ();
  }
  
}
