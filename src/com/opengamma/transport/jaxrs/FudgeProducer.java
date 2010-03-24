/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.transport.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import org.fudgemsg.FudgeMsgEnvelope;

/**
 * Base class for the Producer objects.
 * 
 * @author Andrew Griffin
 */
/* package */ abstract class FudgeProducer extends FudgeBase implements MessageBodyWriter<FudgeMsgEnvelope> {
  
  private int _fudgeTaxonomyId;

  protected FudgeProducer () {
    super ();
    setFudgeTaxonomyId (0);
  }
  
  public void setFudgeTaxonomyId (final int fudgeTaxonomyId) {
    if ((fudgeTaxonomyId < Short.MIN_VALUE) || (fudgeTaxonomyId > Short.MAX_VALUE)) throw new IllegalArgumentException ("fudgeTaxonomyId must be 16-bit signed integer");
    _fudgeTaxonomyId = fudgeTaxonomyId;
  }
  
  public int getFudgeTaxonomyId () {
    return _fudgeTaxonomyId;
  }
  
  @Override
  public long getSize(FudgeMsgEnvelope t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return FudgeMsgEnvelope.class.isAssignableFrom (type);
  }

}