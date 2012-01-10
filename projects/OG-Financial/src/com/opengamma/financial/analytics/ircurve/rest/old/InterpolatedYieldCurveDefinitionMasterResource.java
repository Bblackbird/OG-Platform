/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve.rest.old;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveDefinitionMaster;
import com.opengamma.financial.analytics.ircurve.YieldCurveDefinition;
import com.opengamma.financial.analytics.ircurve.YieldCurveDefinitionDocument;
import com.opengamma.id.UniqueId;
import com.opengamma.id.UniqueIdFudgeBuilder;

/**
 * 
 */
public class InterpolatedYieldCurveDefinitionMasterResource {

  private final InterpolatedYieldCurveDefinitionMaster _underlying;
  private final FudgeContext _fudgeContext;

  public InterpolatedYieldCurveDefinitionMasterResource(final InterpolatedYieldCurveDefinitionMaster underlying, final FudgeContext fudgeContext) {
    _underlying = underlying;
    _fudgeContext = fudgeContext;
  }

  protected InterpolatedYieldCurveDefinitionMaster getUnderlying() {
    return _underlying;
  }

  protected FudgeContext getFudgeContext() {
    return _fudgeContext;
  }

  @POST
  @Path("add")
  public FudgeMsgEnvelope add(final FudgeMsgEnvelope payload) {
    final FudgeDeserializer deserializer = new FudgeDeserializer(getFudgeContext());
    final YieldCurveDefinition curveDefinition = deserializer.fieldValueToObject(YieldCurveDefinition.class, payload.getMessage().getByName("definition"));
    YieldCurveDefinitionDocument document = new YieldCurveDefinitionDocument(curveDefinition);
    document = getUnderlying().add(document);
    if (document == null) {
      return null;
    }
    final FudgeSerializer serializer = new FudgeSerializer(getFudgeContext());
    final MutableFudgeMsg resp = serializer.newMessage();
    resp.add("uniqueId", UniqueIdFudgeBuilder.toFudgeMsg(serializer, document.getUniqueId()));
    return new FudgeMsgEnvelope(resp);
  }

  @POST
  @Path("addOrUpdate")
  public FudgeMsgEnvelope addOrUpdate(final FudgeMsgEnvelope payload) {
    final FudgeDeserializer deserializer = new FudgeDeserializer(getFudgeContext());
    final YieldCurveDefinition curveDefinition = deserializer.fieldValueToObject(YieldCurveDefinition.class, payload.getMessage().getByName("definition"));
    YieldCurveDefinitionDocument document = new YieldCurveDefinitionDocument(curveDefinition);
    document = getUnderlying().addOrUpdate(document);
    if (document == null) {
      return null;
    }
    final FudgeSerializer serializer = new FudgeSerializer(getFudgeContext());
    final MutableFudgeMsg resp = serializer.newMessage();
    resp.add("uniqueId", UniqueIdFudgeBuilder.toFudgeMsg(serializer, document.getUniqueId()));
    return new FudgeMsgEnvelope(resp);
  }

  @GET
  @Path("curves/{uid}")
  public FudgeMsgEnvelope get(@PathParam("uid") final String uidString) {
    final UniqueId uid = UniqueId.parse(uidString);
    try {
      final YieldCurveDefinitionDocument document = getUnderlying().get(uid);
      final FudgeSerializer serializer = new FudgeSerializer(getFudgeContext());
      final MutableFudgeMsg resp = serializer.newMessage();
      resp.add("uniqueId", UniqueIdFudgeBuilder.toFudgeMsg(serializer, document.getUniqueId()));
      serializer.addToMessageWithClassHeaders(resp, "definition", null, document.getYieldCurveDefinition(), YieldCurveDefinition.class);
      return new FudgeMsgEnvelope(resp);
    } catch (DataNotFoundException e) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  @DELETE
  @Path("curves/{uid}")
  public FudgeMsgEnvelope remove(@PathParam("uid") final String uidString) {
    final UniqueId uid = UniqueId.parse(uidString);
    try {
      getUnderlying().remove(uid);
      return null;
    } catch (DataNotFoundException e) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

  @PUT
  @Path("curves/{uid}")
  public FudgeMsgEnvelope update(@PathParam("uid") final String uidString, final FudgeMsgEnvelope payload) {
    final UniqueId uid = UniqueId.parse(uidString);
    final FudgeDeserializer deserializer = new FudgeDeserializer(getFudgeContext());
    final YieldCurveDefinition curveDefinition = deserializer.fieldValueToObject(YieldCurveDefinition.class, payload.getMessage().getByName("definition"));
    YieldCurveDefinitionDocument document = new YieldCurveDefinitionDocument(uid, curveDefinition);
    try {
      document = getUnderlying().update(document);
      if (document == null) {
        return null;
      }
      return FudgeContext.EMPTY_MESSAGE_ENVELOPE;
    } catch (DataNotFoundException e) {
      throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
  }

}
