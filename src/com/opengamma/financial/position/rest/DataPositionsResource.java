/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.rest;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.financial.position.master.PositionMaster;
import com.opengamma.financial.position.master.PositionSearchRequest;
import com.opengamma.financial.position.master.PositionSearchResult;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.transport.jaxrs.FudgeRest;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.AbstractDataResource;

/**
 * RESTful resource for all positions.
 * <p>
 * The positions resource represents the whole of a position master.
 * This is a logical URL as positions have unique identifiers.
 */
@Path("/data/positions")
public class DataPositionsResource extends AbstractDataResource {

  /**
   * The injected position master.
   */
  private final PositionMaster _posMaster;

  /**
   * Creates the resource.
   * @param posMaster  the position master, not null
   */
  public DataPositionsResource(final PositionMaster posMaster) {
    ArgumentChecker.notNull(posMaster, "PositionMaster");
    _posMaster = posMaster;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the position master.
   * @return the position master, not null
   */
  public PositionMaster getPositionMaster() {
    return _posMaster;
  }

  //-------------------------------------------------------------------------
  @GET
  public Response search(@Context Providers providers, @QueryParam("msg") String msgBase64) {
    PositionSearchRequest request = decodeBean(PositionSearchRequest.class, providers, msgBase64);
    PositionSearchResult result = getPositionMaster().searchPositions(request);
    return Response.ok(result).build();
  }

  @POST
  @Consumes(FudgeRest.MEDIA)
  public Response add(@Context UriInfo uriInfo, PositionDocument request) {
    PositionDocument result = getPositionMaster().addPosition(request);
    return Response.created(DataPositionResource.uri(uriInfo, result.getPositionId())).entity(result).build();
  }

  //-------------------------------------------------------------------------
  @Path("{positionId}")
  public DataPositionResource findPosition(@PathParam("positionId") String idStr) {
    UniqueIdentifier id = UniqueIdentifier.parse(idStr);
    return new DataPositionResource(this, id);
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for all positions.
   * @param uriInfo  the URI information, not null
   * @return the URI, not null
   */
  public static URI uri(UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().path("/positions").build();
  }

}
