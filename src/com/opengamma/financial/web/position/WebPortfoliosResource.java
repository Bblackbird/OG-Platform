/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.position;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.financial.position.master.ManageablePortfolio;
import com.opengamma.financial.position.master.PortfolioTreeDocument;
import com.opengamma.financial.position.master.PortfolioTreeSearchRequest;
import com.opengamma.financial.position.master.PortfolioTreeSearchResult;
import com.opengamma.financial.position.master.PositionMaster;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.db.PagingRequest;

/**
 * RESTful resource for all portfolios.
 * <p>
 * The portfolios resource represents the whole of a position master.
 */
@Path("/portfolios")
public class WebPortfoliosResource extends AbstractWebPortfolioResource {

  /**
   * Creates the resource.
   * @param positionMaster  the position master, not null
   */
  public WebPortfoliosResource(final PositionMaster positionMaster) {
    super(positionMaster);
  }

  //-------------------------------------------------------------------------
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String get(
      @QueryParam("page") int page,
      @QueryParam("pageSize") int pageSize,
      @QueryParam("name") String name) {
    FlexiBean out = createRootData();
    
    PortfolioTreeSearchRequest searchRequest = new PortfolioTreeSearchRequest();
    searchRequest.setPagingRequest(PagingRequest.of(page, pageSize));
    searchRequest.setName(StringUtils.trimToNull(name));
    out.put("searchRequest", searchRequest);
    
    if (data().getUriInfo().getQueryParameters().size() > 0) {
      PortfolioTreeSearchResult searchResult = data().getPositionMaster().searchPortfolioTrees(searchRequest);
      out.put("searchResult", searchResult);
    }
    return getFreemarker().build("portfolios/portfolios.ftl", out);
  }

  //-------------------------------------------------------------------------
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response post(@FormParam("name") String name) {
    name = StringUtils.trimToNull(name);
    if (name == null) {
      FlexiBean out = createRootData();
      out.put("err_nameMissing", true);
      String html = getFreemarker().build("portfolios/portfolios-add.ftl", out);
      return Response.ok(html).build();
    }
    ManageablePortfolio portfolio = new ManageablePortfolio(name);
    PortfolioTreeDocument doc = new PortfolioTreeDocument(portfolio);
    PortfolioTreeDocument added = data().getPositionMaster().addPortfolioTree(doc);
    URI uri = data().getUriInfo().getAbsolutePathBuilder().path(added.getPortfolioId().toLatest().toString()).build();
    return Response.seeOther(uri).build();
  }

  //-------------------------------------------------------------------------
  @Path("{portfolioId}")
  public WebPortfolioResource findPortfolio(@PathParam("portfolioId") String idStr) {
    data().setUriPortfolioId(idStr);
    PortfolioTreeDocument portfolio = data().getPositionMaster().getPortfolioTree(UniqueIdentifier.parse(idStr));
    data().setPortfolio(portfolio);
    data().setNode(portfolio.getPortfolio().getRootNode());
    return new WebPortfolioResource(this);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  public FlexiBean createRootData() {
    PortfolioTreeSearchRequest searchRequest = new PortfolioTreeSearchRequest();
    FlexiBean out = getFreemarker().createRootData();
    out.put("searchRequest", searchRequest);
    out.put("uris", new WebPortfoliosUris(data()));
    return out;
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for portfolios.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(WebPortfoliosData data) {
    return data.getUriInfo().getBaseUriBuilder().path(WebPortfoliosResource.class).build();
  }

}
