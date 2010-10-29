/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.exchange;

import java.net.URI;

import com.opengamma.financial.world.exchange.Exchange;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;

/**
 * URIs for web-based exchanges.
 */
public class WebExchangeUris {

  /**
   * The data.
   */
  private final WebExchangeData _data;

  /**
   * Creates an instance.
   * @param data  the web data, not null
   */
  public WebExchangeUris(WebExchangeData data) {
    _data = data;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the URI.
   * @return the URI
   */
  public URI exchanges() {
    return WebExchangesResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param identifier  the identifier to search for, may be null
   * @return the URI
   */
  public URI exchanges(final Identifier identifier) {
    return WebExchangesResource.uri(_data, IdentifierBundle.of(identifier));
  }

  /**
   * Gets the URI.
   * @param identifiers  the identifiers to search for, may be null
   * @return the URI
   */
  public URI exchanges(final IdentifierBundle identifiers) {
    return WebExchangesResource.uri(_data, identifiers);
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI exchange() {
    return WebExchangeResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param exchange  the exchange, not null
   * @return the URI
   */
  public URI exchange(final Exchange exchange) {
    return WebExchangeResource.uri(_data, exchange.getUniqueIdentifier());
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI exchangeVersions() {
    return WebExchangeVersionsResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI exchangeVersion() {
    return WebExchangeVersionResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param exchange  the exchange, not null
   * @return the URI
   */
  public URI exchangeVersion(final Exchange exchange) {
    return WebExchangeVersionResource.uri(_data, exchange.getUniqueIdentifier());
  }

}
