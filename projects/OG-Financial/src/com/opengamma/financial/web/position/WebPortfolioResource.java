/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.position;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.financial.position.master.PortfolioTreeDocument;
import com.opengamma.financial.position.master.PositionSearchRequest;
import com.opengamma.financial.position.master.PositionSearchResult;
import com.opengamma.id.UniqueIdentifier;

/**
 * RESTful resource for a portfolio.
 */
@Path("/portfolios/{portfolioId}")
public class WebPortfolioResource extends AbstractWebPortfolioResource {

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  public WebPortfolioResource(final AbstractWebPortfolioResource parent) {
    super(parent);
  }

  //-------------------------------------------------------------------------
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String get() {
    PortfolioTreeDocument doc = data().getPortfolio();
    PositionSearchRequest positionSearch = new PositionSearchRequest();
    positionSearch.setParentNodeId(doc.getPortfolio().getRootNode().getUniqueIdentifier());
    PositionSearchResult positionsResult = data().getPositionMaster().searchPositions(positionSearch);
    
    FlexiBean out = createRootData();
    out.put("positionsResult", positionsResult);
    out.put("positions", positionsResult.getPositions());
    return getFreemarker().build("portfolios/portfolio.ftl", out);
  }

  @PUT
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response put(@FormParam("name") String name) {
    name = StringUtils.trimToNull(name);
    if (name == null) {
      FlexiBean out = createRootData();
      out.put("err_nameMissing", true);
      String html = getFreemarker().build("portfolios/portfolio-update.ftl", out);
      return Response.ok(html).build();
    }
    PortfolioTreeDocument doc = data().getPortfolio();
    doc.getPortfolio().setName(name);
    doc = data().getPositionMaster().updatePortfolioTree(doc);
    data().setPortfolio(doc);
    URI uri = WebPortfolioResource.uri(data());
    return Response.seeOther(uri).build();
  }

  @DELETE
  public Response delete() {
    PortfolioTreeDocument doc = data().getPortfolio();
    data().getPositionMaster().removePortfolioTree(doc.getPortfolioId());
    URI uri = WebPortfoliosResource.uri(data());
    return Response.seeOther(uri).build();
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = super.createRootData();
    PortfolioTreeDocument doc = data().getPortfolio();
    out.put("portfolioDoc", doc);
    out.put("portfolio", doc.getPortfolio());
    out.put("childNodes", doc.getPortfolio().getRootNode().getChildNodes());
    return out;
  }

  //-------------------------------------------------------------------------
  @Path("nodes")
  public WebPortfolioNodesResource findNodes() {
    return new WebPortfolioNodesResource(this);
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebPortfoliosData data) {
    return uri(data, null);
  }

  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @param overridePortfolioId  the override portfolio id, null uses information from data
   * @return the URI, not null
   */
  public static URI uri(final WebPortfoliosData data, final UniqueIdentifier overridePortfolioId) {
    String portfolioId = data.getBestPortfolioUriId(overridePortfolioId);
    return data.getUriInfo().getBaseUriBuilder().path(WebPortfolioResource.class).build(portfolioId);
  }

}
