/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.region.impl;

import java.net.URI;

import com.opengamma.core.change.BasicChangeManager;
import com.opengamma.core.change.ChangeManager;
import com.opengamma.id.ObjectIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.region.RegionDocument;
import com.opengamma.master.region.RegionHistoryRequest;
import com.opengamma.master.region.RegionHistoryResult;
import com.opengamma.master.region.RegionMaster;
import com.opengamma.master.region.RegionSearchRequest;
import com.opengamma.master.region.RegionSearchResult;
import com.opengamma.transport.jaxrs.FudgeRest;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.rest.FudgeRestClient;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Provides access to a remote {@link RegionMaster}.
 */
public class RemoteRegionMaster implements RegionMaster {

  /**
   * The base URI to call.
   */
  private final URI _baseUri;
  /**
   * The client API.
   */
  private final FudgeRestClient _client;
  /**
   * The change manager.
   */
  private final ChangeManager _changeManager;

  /**
   * Creates an instance.
   * 
   * @param baseUri  the base target URI for all RESTful web services, not null
   */
  public RemoteRegionMaster(final URI baseUri) {
    this(baseUri, new BasicChangeManager());
  }

  /**
   * Creates an instance.
   * 
   * @param baseUri  the base target URI for all RESTful web services, not null
   * @param changeManager  the change manager, not null
   */
  public RemoteRegionMaster(final URI baseUri, ChangeManager changeManager) {
    ArgumentChecker.notNull(baseUri, "baseUri");
    ArgumentChecker.notNull(changeManager, "changeManager");
    _baseUri = baseUri;
    _client = FudgeRestClient.create();
    _changeManager = changeManager;
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionSearchResult search(final RegionSearchRequest request) {
    ArgumentChecker.notNull(request, "request");
    
    String msgBase64 = _client.encodeBean(request);
    URI uri = DataRegionsResource.uri(_baseUri, msgBase64);
    return accessRemote(uri).get(RegionSearchResult.class);
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionDocument get(final UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    
    if (uniqueId.isVersioned()) {
      URI uri = DataRegionResource.uriVersion(_baseUri, uniqueId);
      return accessRemote(uri).get(RegionDocument.class);
    } else {
      return get(uniqueId, VersionCorrection.LATEST);
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionDocument get(final ObjectIdentifiable objectId, final VersionCorrection versionCorrection) {
    ArgumentChecker.notNull(objectId, "objectId");
    
    URI uri = DataRegionResource.uri(_baseUri, objectId, versionCorrection);
    return accessRemote(uri).get(RegionDocument.class);
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionDocument add(final RegionDocument document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getRegion(), "document.position");
    
    URI uri = DataRegionsResource.uri(_baseUri, null);
    return accessRemote(uri).post(RegionDocument.class, document);
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionDocument update(final RegionDocument document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getRegion(), "document.position");
    ArgumentChecker.notNull(document.getUniqueId(), "document.uniqueId");
    
    URI uri = DataRegionResource.uri(_baseUri, document.getUniqueId(), VersionCorrection.LATEST);
    return accessRemote(uri).put(RegionDocument.class, document);
  }

  //-------------------------------------------------------------------------
  @Override
  public void remove(final UniqueId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    
    URI uri = DataRegionResource.uri(_baseUri, uniqueId, VersionCorrection.LATEST);
    accessRemote(uri).delete();
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionHistoryResult history(final RegionHistoryRequest request) {
    ArgumentChecker.notNull(request, "request");
    ArgumentChecker.notNull(request.getObjectId(), "request.objectId");
    
    String msgBase64 = _client.encodeBean(request);
    URI uri = DataRegionResource.uriVersions(_baseUri, request.getObjectId(), msgBase64);
    return accessRemote(uri).get(RegionHistoryResult.class);
  }

  //-------------------------------------------------------------------------
  @Override
  public RegionDocument correct(final RegionDocument document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getRegion(), "document.position");
    ArgumentChecker.notNull(document.getUniqueId(), "document.uniqueId");
    
    URI uri = DataRegionResource.uriVersion(_baseUri, document.getUniqueId());
    return accessRemote(uri).get(RegionDocument.class);
  }

  //-------------------------------------------------------------------------
  @Override
  public ChangeManager changeManager() {
    return _changeManager;
  }

  //-------------------------------------------------------------------------
  /**
   * Accesses the remote master.
   * 
   * @param uri  the URI to call, not null
   * @return the resource, suitable for calling get/post/put/delete on, not null
   */
  protected Builder accessRemote(URI uri) {
    // TODO: Better solution to this limitation in JAX-RS (we shouldn't have "data" in URI)
    // this code removes a second duplicate "data"
    String uriStr = uri.toString();
    int pos = uriStr.indexOf("/jax/data/");
    if (pos > 0) {
      pos = uriStr.indexOf("/data/", pos + 10);
      if (pos > 0) {
        uriStr = uriStr.substring(0, pos) + uriStr.substring(pos + 5);
      }
    }
    uri = URI.create(uriStr);
    return _client.access(uri).type(FudgeRest.MEDIA_TYPE).accept(FudgeRest.MEDIA_TYPE);
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a string summary of this master.
   * 
   * @return the string summary, not null
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + _baseUri + "]";
  }

}
