/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.timeseries;

import java.net.URI;

import com.opengamma.financial.timeseries.TimeSeriesDocument;

/**
 * URIs for web-based time series.
 */
public class WebTimeSeriesUris {

  /**
   * The data.
   */
  private final WebTimeSeriesData _data;

  /**
   * Creates an instance.
   * @param data  the web data, not null
   */
  public WebTimeSeriesUris(WebTimeSeriesData data) {
    _data = data;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the URI.
   * @return the URI
   */
  public URI allTimeSeries() {
    return WebAllTimeSeriesResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI oneTimeSeries() {
    return WebOneTimeSeriesResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param timeSeries  the time series, not null
   * @return the URI
   */
  public URI oneTimeSeries(final TimeSeriesDocument<?> timeSeries) {
    return WebOneTimeSeriesResource.uri(_data, timeSeries.getUniqueIdentifier());
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI timeSeriesVersions() {
    return WebTimeSeriesVersionsResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @return the URI
   */
  public URI timeSeriesVersion() {
    return WebTimeSeriesVersionResource.uri(_data);
  }

  /**
   * Gets the URI.
   * @param timeSeries  the time series, not null
   * @return the URI
   */
  public URI timeSeriesVersion(final TimeSeriesDocument<?> timeSeries) {
    return WebTimeSeriesVersionResource.uri(_data, timeSeries.getUniqueIdentifier());
  }

}
