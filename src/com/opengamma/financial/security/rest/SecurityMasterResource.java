/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.rest;

import static com.opengamma.financial.security.rest.SecurityMasterServiceNames.SECURITYMASTER_HISTORIC;
import static com.opengamma.financial.security.rest.SecurityMasterServiceNames.SECURITYMASTER_SEARCH;
import static com.opengamma.financial.security.rest.SecurityMasterServiceNames.SECURITYMASTER_SECURITY;

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
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.security.master.SecurityDocument;
import com.opengamma.financial.security.master.SecurityMaster;
import com.opengamma.financial.security.master.SecuritySearchHistoricRequest;
import com.opengamma.financial.security.master.SecuritySearchHistoricResult;
import com.opengamma.financial.security.master.SecuritySearchRequest;
import com.opengamma.financial.security.master.SecuritySearchResult;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;

/**
 * RESTful resource publishing details from a SecurityMaster.
 */
public class SecurityMasterResource {

  private SecurityMaster _securityMaster;

  private FudgeContext _fudgeContext;

  public SecurityMasterResource(final SecurityMaster securityMaster, final FudgeContext fudgeContext) {
    setSecurityMaster(securityMaster);
    setFudgeContext(fudgeContext);
  }

  public void setSecurityMaster(final SecurityMaster securityMaster) {
    ArgumentChecker.notNull(securityMaster, "securityMaster");
    _securityMaster = securityMaster;
  }

  public SecurityMaster getSecurityMaster() {
    return _securityMaster;
  }

  public void setFudgeContext(final FudgeContext fudgeContext) {
    ArgumentChecker.notNull(fudgeContext, "fudgeContext");
    _fudgeContext = fudgeContext;
  }

  public FudgeContext getFudgeContext() {
    return _fudgeContext;
  }

  public FudgeSerializationContext getFudgeSerializationContext() {
    return new FudgeSerializationContext(getFudgeContext());
  }

  public FudgeDeserializationContext getFudgeDeserializationContext() {
    return new FudgeDeserializationContext(getFudgeContext());
  }

  /**
   * 
   */
  public class IdentifiedSecurityResource {

    private final UniqueIdentifier _uid;

    public IdentifiedSecurityResource(final UniqueIdentifier uid) {
      _uid = uid;
    }

    private UniqueIdentifier getUniqueIdentifier() {
      return _uid;
    }

    @GET
    public FudgeMsgEnvelope get() {
      try {
        final SecurityDocument document = getSecurityMaster().get(getUniqueIdentifier());
        return new FudgeMsgEnvelope(getFudgeSerializationContext().objectToFudgeMsg(document));
      } catch (DataNotFoundException e) {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
    }

    @PUT
    public FudgeMsgEnvelope correct(final FudgeMsgEnvelope payload) {
      SecurityDocument document = getFudgeDeserializationContext().fudgeMsgToObject(SecurityDocument.class, payload.getMessage());
      document = getSecurityMaster().correct(document);
      final UniqueIdentifier uid = document.getSecurityId();
      if (uid == null) {
        return FudgeContext.EMPTY_MESSAGE_ENVELOPE;
      } else {
        return new FudgeMsgEnvelope(uid.toFudgeMsg(getFudgeContext()));
      }
    }

    @POST
    public FudgeMsgEnvelope update(final FudgeMsgEnvelope payload) {
      SecurityDocument document = getFudgeDeserializationContext().fudgeMsgToObject(SecurityDocument.class, payload.getMessage());
      document = getSecurityMaster().update(document);
      final UniqueIdentifier uid = document.getSecurityId();
      if (uid == null) {
        return FudgeContext.EMPTY_MESSAGE_ENVELOPE;
      } else {
        return new FudgeMsgEnvelope(uid.toFudgeMsg(getFudgeContext()));
      }
    }

    @DELETE
    public void remove() {
      getSecurityMaster().remove(getUniqueIdentifier());
    }

  }

  /**
   * 
   */
  public class SecurityResource {

    @POST
    public FudgeMsgEnvelope add(final FudgeMsgEnvelope payload) {
      SecurityDocument document = getFudgeDeserializationContext().fudgeMsgToObject(SecurityDocument.class, payload.getMessage());
      document = getSecurityMaster().add(document);
      return new FudgeMsgEnvelope(document.getSecurityId().toFudgeMsg(getFudgeContext()));
    }

    @Path("{uid}")
    public Object resource(@PathParam("uid") String uid) {
      final UniqueIdentifier uniqueIdentifier = UniqueIdentifier.parse(uid);
      return new IdentifiedSecurityResource(uniqueIdentifier);
    }

  }

  @Path(SECURITYMASTER_SECURITY)
  public SecurityResource securityResource() {
    return new SecurityResource();
  }

  @POST
  @Path(SECURITYMASTER_SEARCH)
  public FudgeMsgEnvelope search(final FudgeMsgEnvelope payload) {
    final SecuritySearchRequest request = getFudgeDeserializationContext().fudgeMsgToObject(SecuritySearchRequest.class, payload.getMessage());
    final SecuritySearchResult result = getSecurityMaster().search(request);
    return new FudgeMsgEnvelope(getFudgeSerializationContext().objectToFudgeMsg(result));
  }

  @POST
  @Path(SECURITYMASTER_HISTORIC)
  public FudgeMsgEnvelope searchHistoric(final FudgeMsgEnvelope payload) {
    final SecuritySearchHistoricRequest request = getFudgeDeserializationContext().fudgeMsgToObject(SecuritySearchHistoricRequest.class, payload.getMessage());
    final SecuritySearchHistoricResult result = getSecurityMaster().searchHistoric(request);
    return new FudgeMsgEnvelope(getFudgeSerializationContext().objectToFudgeMsg(result));
  }

  /**
   * For debugging purposes only.
   * 
   * @return some debug information about the state of this resource object; e.g. which underlying objects is it connected to.
   */
  @GET
  @Path("debugInfo")
  public FudgeMsgEnvelope getDebugInfo() {
    final MutableFudgeFieldContainer message = getFudgeContext().newMessage();
    message.add("fudgeContext", getFudgeContext().toString());
    message.add("securityMaster", getSecurityMaster().toString());
    return new FudgeMsgEnvelope(message);
  }

}
