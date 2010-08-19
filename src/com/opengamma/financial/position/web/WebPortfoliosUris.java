/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.web;

import java.net.URI;

import com.opengamma.financial.position.master.ManageablePortfolio;
import com.opengamma.financial.position.master.ManageablePortfolioNode;
import com.opengamma.financial.position.master.ManageablePosition;

/**
 * URIs for web-based portfolios.
 */
public class WebPortfoliosUris {

  /**
   * The data.
   */
  private final WebPortfoliosData _data;

  public WebPortfoliosUris(WebPortfoliosData data) {
    _data = data;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the URI.
   * @return the URI
   */
  public URI portfolios() {
    return WebPortfoliosResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI portfolio() {
    return WebPortfolioResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param portfolio  the portfolio, not null
   * @return the URI
   */
  public URI portfolio(final ManageablePortfolio portfolio) {
    return WebPortfolioResource.uri(_data, portfolio.getUniqueIdentifier());
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI node() {
    return WebPortfolioNodeResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param node  the node, not null
   * @return the URI
   */
  public URI node(final ManageablePortfolioNode node) {
    return WebPortfolioNodeResource.uri(_data, node.getUniqueIdentifier());
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI nodePositions() {
    return WebPortfolioNodePositionsResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param node  the node, not null
   * @return the URI
   */
  public URI nodePositions(final ManageablePortfolioNode node) {
    return WebPortfolioNodePositionsResource.uri(_data, node.getUniqueIdentifier());
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI nodePosition() {
    return WebPortfolioNodePositionResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param position  the position, not null
   * @return the URI
   */
  public URI nodePosition(final ManageablePosition position) {
    return WebPortfolioNodePositionResource.uri(_data, position.getUniqueIdentifier());
  }

}
