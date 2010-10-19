/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.config.memory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.time.Instant;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.opengamma.DataNotFoundException;
import com.opengamma.config.ConfigDocument;
import com.opengamma.config.ConfigMaster;
import com.opengamma.config.ConfigSearchHistoricRequest;
import com.opengamma.config.ConfigSearchHistoricResult;
import com.opengamma.config.ConfigSearchRequest;
import com.opengamma.config.ConfigSearchResult;
import com.opengamma.id.UniqueIdentifiables;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.id.UniqueIdentifierSupplier;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.RegexUtils;
import com.opengamma.util.db.Paging;

/**
 * A simple, in-memory implementation of {@code ConfigMaster}.
 * <p>
 * This security master does not support versioning of configuration documents.
 * <p>
 * This implementation does not copy stored elements, making it thread-hostile.
 * As such, this implementation is currently most useful for testing scenarios.
 * 
 * @param <T>  the config element type
 */
public class InMemoryConfigMaster<T> implements ConfigMaster<T> {

  /**
   * The default scheme used for each {@link UniqueIdentifier}.
   */
  public static final String DEFAULT_UID_SCHEME = "Memory";

  /**
   * A cache of securities by identifier.
   */
  private final ConcurrentMap<UniqueIdentifier, ConfigDocument<T>> _configs = new ConcurrentHashMap<UniqueIdentifier, ConfigDocument<T>>();
  /**
   * The supplied of identifiers.
   */
  private final Supplier<UniqueIdentifier> _uidSupplier;

  /**
   * Creates an empty security master using the default scheme for any {@link UniqueIdentifier}s created.
   */
  public InMemoryConfigMaster() {
    this(new UniqueIdentifierSupplier(DEFAULT_UID_SCHEME));
  }

  /**
   * Creates an instance specifying the supplier of unique identifiers.
   * 
   * @param uidSupplier  the supplier of unique identifiers, not null
   */
  public InMemoryConfigMaster(final Supplier<UniqueIdentifier> uidSupplier) {
    ArgumentChecker.notNull(uidSupplier, "uidSupplier");
    _uidSupplier = uidSupplier;
  }

  //-------------------------------------------------------------------------
  @Override
  public ConfigSearchResult<T> search(final ConfigSearchRequest request) {
    ArgumentChecker.notNull(request, "request");
    final ConfigSearchResult<T> result = new ConfigSearchResult<T>();
    Collection<ConfigDocument<T>> docs = _configs.values();
    if (request.getName() != null) {
      final Pattern pattern = RegexUtils.wildcardsToPattern(request.getName());
      docs = Collections2.filter(docs, new Predicate<ConfigDocument<T>>() {
        @Override
        public boolean apply(final ConfigDocument<T> doc) {
          return pattern.matcher(doc.getName()).matches();
        }
      });
    }
    result.getDocuments().addAll(docs);
    result.setPaging(Paging.of(docs));
    return result;
  }

  //-------------------------------------------------------------------------
  @Override
  public ConfigDocument<T> get(final UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "uid");
    final ConfigDocument<T> document = _configs.get(uid);
    if (document == null) {
      throw new DataNotFoundException("Config not found: " + uid);
    }
    return document;
  }

  //-------------------------------------------------------------------------
  @Override
  public ConfigDocument<T> add(final ConfigDocument<T> document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getName(), "document.name");
    ArgumentChecker.notNull(document.getValue(), "document.value");
    
    final T value = document.getValue();
    final UniqueIdentifier uid = _uidSupplier.get();
    final Instant now = Instant.nowSystemClock();
    UniqueIdentifiables.setInto(value, uid);
    final ConfigDocument<T> doc = new ConfigDocument<T>();
    doc.setName(document.getName());
    doc.setValue(value);
    doc.setConfigId(uid);
    doc.setVersionFromInstant(now);
    _configs.put(uid, doc);  // unique identifier should be unique
    return doc;
  }

  //-------------------------------------------------------------------------
  @Override
  public ConfigDocument<T> update(final ConfigDocument<T> document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getValue(), "document.value");
    ArgumentChecker.notNull(document.getConfigId(), "document.configId");
    
    final UniqueIdentifier uid = document.getConfigId();
    final Instant now = Instant.nowSystemClock();
    final ConfigDocument<T> storedDocument = _configs.get(uid);
    if (storedDocument == null) {
      throw new DataNotFoundException("Config not found: " + uid);
    }
    document.setVersionFromInstant(now);
    document.setVersionToInstant(null);
    if (_configs.replace(uid, storedDocument, document) == false) {
      throw new IllegalArgumentException("Concurrent modification");
    }
    return document;
  }

  //-------------------------------------------------------------------------
  @Override
  public void remove(final UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "uid");
    
    if (_configs.remove(uid) == null) {
      throw new DataNotFoundException("Config not found: " + uid);
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public ConfigSearchHistoricResult<T> searchHistoric(final ConfigSearchHistoricRequest request) {
    ArgumentChecker.notNull(request, "request");
    ArgumentChecker.notNull(request.getConfigId(), "request.configId");
    
    final ConfigSearchHistoricResult<T> result = new ConfigSearchHistoricResult<T>();
    final ConfigDocument<T> doc = get(request.getConfigId());
    if (doc != null) {
      result.getDocuments().add(doc);
    }
    result.setPaging(Paging.of(result.getDocuments()));
    return result;
  }

}
