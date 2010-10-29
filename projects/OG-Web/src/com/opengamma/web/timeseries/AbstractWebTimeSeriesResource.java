/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.web.timeseries;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.financial.timeseries.TimeSeriesMaster;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.AbstractWebResource;
import com.opengamma.web.WebHomeUris;

/**
 * Abstract base class for RESTful time series resources.
 */
public abstract class AbstractWebTimeSeriesResource extends AbstractWebResource {

  /**
   * The backing bean.
   */
  private final WebTimeSeriesData _data;

  /**
   * Creates the resource.
   * @param timeSeriesMaster  the time series master, not null
   */
  protected AbstractWebTimeSeriesResource(final TimeSeriesMaster<?> timeSeriesMaster) {
    ArgumentChecker.notNull(timeSeriesMaster, "timeSeriesMaster");
    _data = new WebTimeSeriesData();
    data().setTimeSeriesMaster(timeSeriesMaster);
  }

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  protected AbstractWebTimeSeriesResource(final AbstractWebTimeSeriesResource parent) {
    super(parent);
    _data = parent._data;
  }

  /**
   * Setter used to inject the URIInfo.
   * This is a roundabout approach, because Spring and JSR-311 injection clash.
   * DO NOT CALL THIS METHOD DIRECTLY.
   * @param uriInfo  the URI info, not null
   */
  @Context
  public void setUriInfo(final UriInfo uriInfo) {
    data().setUriInfo(uriInfo);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = getFreemarker().createRootData();
    out.put("homeUris", new WebHomeUris(data().getUriInfo()));
    out.put("uris", new WebTimeSeriesUris(data()));
    return out;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the backing bean.
   * @return the backing bean, not null
   */
  protected WebTimeSeriesData data() {
    return _data;
  }

}
