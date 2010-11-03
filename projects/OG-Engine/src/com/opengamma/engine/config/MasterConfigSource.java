/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.config;

import java.util.ArrayList;
import java.util.List;

import javax.time.Instant;
import javax.time.InstantProvider;

import com.opengamma.DataNotFoundException;
import com.opengamma.config.ConfigDocument;
import com.opengamma.config.ConfigMaster;
import com.opengamma.config.ConfigHistoryRequest;
import com.opengamma.config.ConfigHistoryResult;
import com.opengamma.config.ConfigSearchRequest;
import com.opengamma.config.ConfigSearchResult;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.db.PagingRequest;

/**
 * A {@code ConfigSource} implemented using an underlying {@code ConfigMaster}.
 * <p>
 * The {@link ConfigSource} interface provides securities to the engine via a narrow API.
 * This class provides the source on top of a standard {@link ConfigMaster}.
 * <p>
 * This implementation supports the concept of fixing the version.
 * This allows the version to be set in the constructor, and applied automatically to the methods.
 * Some methods on {@code ConfigSource} specify their own version requirements, which are respected.
 */
public class MasterConfigSource implements ConfigSource {

  /**
   * The config master.
   */
  private final ConfigMaster _configMaster;
  /**
   * The instant to search for a version at.
   * Null is treated as the latest version.
   */
  private final Instant _versionAsOfInstant;

  /**
   * Creates an instance with an underlying config master.
   * @param configMaster  the config master, not null
   */
  public MasterConfigSource(final ConfigMaster configMaster) {
    this(configMaster, null);
  }

  /**
   * Creates an instance with an underlying config master viewing the version
   * that existed on the specified instant.
   * @param configMaster  the config master, not null
   * @param versionAsOfInstantProvider  the version instant to retrieve, null for latest version
   */
  public MasterConfigSource(final ConfigMaster configMaster, final InstantProvider versionAsOfInstantProvider) {
    ArgumentChecker.notNull(configMaster, "configMaster");
    _configMaster = configMaster;
    if (versionAsOfInstantProvider != null) {
      _versionAsOfInstant = Instant.of(versionAsOfInstantProvider);
    } else {
      _versionAsOfInstant = null;
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the underlying config master.
   * 
   * @return the config master, not null
   */
  public ConfigMaster getMaster() {
    return _configMaster;
  }

  /**
   * Gets the version instant to retrieve.
   * 
   * @return the version instant to retrieve, null for latest version
   */
  public Instant getVersionAsOfInstant() {
    return _versionAsOfInstant;
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> List<T> search(final Class<T> clazz, final ConfigSearchRequest request) {
    ArgumentChecker.notNull(clazz, "clazz");
    ArgumentChecker.notNull(request, "request");
    request.setVersionAsOfInstant(_versionAsOfInstant);
    ConfigSearchResult<T> searchResult = _configMaster.typed(clazz).search(request);
    List<ConfigDocument<T>> documents = searchResult.getDocuments();
    List<T> result = new ArrayList<T>();
    for (ConfigDocument<T> configDocument : documents) {
      result.add(configDocument.getValue());
    }
    return result;
  }

  @Override
  public <T> T get(final Class<T> clazz, final UniqueIdentifier uid) {
    ArgumentChecker.notNull(clazz, "clazz");
    ArgumentChecker.notNull(uid, "uid");
    if (_versionAsOfInstant != null) {
      ConfigHistoryRequest request = new ConfigHistoryRequest(uid, _versionAsOfInstant);
      ConfigHistoryResult<T> result = _configMaster.typed(clazz).history(request);
      if (result.getDocuments().isEmpty()) {
        return null;
      }
      if (uid.isVersioned() && uid.getVersion().equals(result.getFirstDocument().getConfigId().getVersion()) == false) {
        return null;  // config found, but not matching the version we asked for
      }
      return result.getFirstValue();
    } else {
      // just want the latest (or version) asked for, so don't use the more costly historic search operation
      try {
        return _configMaster.typed(clazz).get(uid).getValue();
      } catch (DataNotFoundException ex) {
        return null;
      }
    }
  }

  @Override
  public <T> T getLatestByName(final Class<T> clazz, final String name) {
    return getByName(clazz, name, null);
  }

  @Override
  public <T> T getByName(final Class<T> clazz, final String name, final Instant versionAsOf) {
    ConfigDocument<T> doc = getDocumentByName(clazz, name, versionAsOf);
    return doc == null ? null : doc.getValue();
  }

  @Override
  public <T> ConfigDocument<T> getDocumentByName(final Class<T> clazz, final String name, final Instant versionAsOf) {
    ArgumentChecker.notNull(clazz, "clazz");
    ArgumentChecker.notNull(name, "name");
    ConfigSearchRequest request = new ConfigSearchRequest();
    request.setPagingRequest(PagingRequest.ONE);
    request.setVersionAsOfInstant(versionAsOf);
    request.setName(name);
    ConfigSearchResult<T> searchResult = _configMaster.typed(clazz).search(request);
    return searchResult.getFirstDocument();
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    String str = "MasterConfigSource[" + getMaster();
    if (_versionAsOfInstant != null) {
      str += ",versionAsOf=" + _versionAsOfInstant;
    }
    return str + "]";
  }

}
