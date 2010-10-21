/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.web.config;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.config.ConfigDocument;
import com.opengamma.config.ConfigSearchHistoricRequest;
import com.opengamma.config.ConfigSearchHistoricResult;
import com.opengamma.id.UniqueIdentifier;

/**
 * RESTful resource for all versions of an config.
 * @param <T>  the config element type
 */
@Path("/configs/{type}/{configId}/versions")
@Produces(MediaType.TEXT_HTML)
public class WebConfigTypeVersionsResource<T> extends AbstractWebConfigTypeResource<T> {

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  public WebConfigTypeVersionsResource(final AbstractWebConfigTypeResource<T> parent) {
    super(parent);
  }

  //-------------------------------------------------------------------------
  @GET
  public String get() {
    ConfigSearchHistoricRequest request = new ConfigSearchHistoricRequest();
    request.setConfigId(data().getConfig().getConfigId());
    ConfigSearchHistoricResult<T> result = data().getConfigTypeMaster().searchHistoric(request);
    
    FlexiBean out = createRootData();
    out.put("versionsResult", result);
    out.put("versions", result.getValues());
    return getFreemarker().build("configs/configtypeversions.ftl", out);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = super.createRootData();
    ConfigDocument<T> doc = data().getConfig();
    out.put("configDoc", doc);
    out.put("config", doc.getValue());
    return out;
  }

  //-------------------------------------------------------------------------
  @Path("{versionId}")
  public WebConfigTypeVersionResource<T> findVersion(@PathParam("versionId") String idStr) {
    data().setUriVersionId(idStr);
    ConfigDocument<T> doc = data().getConfig();
    UniqueIdentifier combined = doc.getConfigId().withVersion(idStr);
    if (doc.getConfigId().equals(combined) == false) {
      ConfigDocument<T> versioned = data().getConfigTypeMaster().get(combined);
      data().setVersioned(versioned);
    } else {
      data().setVersioned(doc);
    }
    return new WebConfigTypeVersionResource<T>(this);
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebConfigData<?> data) {
    String typeStr = data.getTypeMap().inverse().get(data.getType());
    String configId = data.getBestConfigUriId(null);
    return data.getUriInfo().getBaseUriBuilder().path(WebConfigTypeVersionsResource.class).build(typeStr, configId);
  }

}
