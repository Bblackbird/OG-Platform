/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.holiday;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.financial.web.WebHomeUris;
import com.opengamma.financial.web.exchange.WebExchangeData;
import com.opengamma.financial.web.exchange.WebExchangeUris;
import com.opengamma.financial.world.holiday.master.HolidayMaster;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.AbstractWebResource;

/**
 * Abstract base class for RESTful holiday resources.
 */
public abstract class AbstractWebHolidayResource extends AbstractWebResource {

  /**
   * The backing bean.
   */
  private final WebHolidayData _data;

  /**
   * Creates the resource.
   * @param holidayMaster  the holiday master, not null
   */
  protected AbstractWebHolidayResource(final HolidayMaster holidayMaster) {
    ArgumentChecker.notNull(holidayMaster, "holidayMaster");
    _data = new WebHolidayData();
    data().setHolidayMaster(holidayMaster);
  }

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  protected AbstractWebHolidayResource(final AbstractWebHolidayResource parent) {
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
    out.put("uris", new WebHolidayUris(data()));
    WebExchangeData exchangeData = new WebExchangeData(data().getUriInfo());
    out.put("exchangeUris", new WebExchangeUris(exchangeData));
    return out;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the backing bean.
   * @return the backing bean, not null
   */
  protected WebHolidayData data() {
    return _data;
  }

}
