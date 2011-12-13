package com.opengamma.web.server.push.rest;

import com.opengamma.web.server.conversion.ConversionMode;
import com.opengamma.web.server.push.Viewport;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * TODO sub-resource for individual grids that can return CSV? currently done by WebAnalyticsResource
 * URL like /{vieportId}/primitives|portfolio/analytics
 * URL like /{vieportId}/primitives|portfolio/dependencygraph/{row}/{col}
 */
public class ViewportResource {

  private final Viewport _viewport;

  public ViewportResource(Viewport viewport) {
    _viewport = viewport;
  }

  @Path("grid")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getGridStructure() {
    return new JSONObject(_viewport.getGridStructure()).toString();
  }

  @Path("data")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getLatestData() {
    return new JSONObject(_viewport.getLatestResults()).toString();
  }

  // TODO is a query param the right way to do this? easy to implement but is that adequate justification?
  @POST
  @Path("running")
  public Response setRunning(@QueryParam("run") boolean run) {
    _viewport.setRunning(run);
    return Response.ok().build();
  }

  // TODO is a query param the right way to do this? easy to implement but is that adequate justification?
  @POST
  @Path("mode")
  public Response setMode(@QueryParam("mode") ConversionMode mode) {
    _viewport.setConversionMode(mode);
    return Response.ok().build();
  }
}
