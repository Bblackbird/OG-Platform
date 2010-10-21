/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.config.mongo.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.time.Instant;
import javax.time.TimeSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.opengamma.DataNotFoundException;
import com.opengamma.config.ConfigDocument;
import com.opengamma.config.ConfigSearchHistoricRequest;
import com.opengamma.config.ConfigSearchHistoricResult;
import com.opengamma.config.ConfigSearchRequest;
import com.opengamma.config.ConfigSearchResult;
import com.opengamma.config.mongo.MongoDBConfigTypeMaster;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.MongoDBConnectionSettings;
import com.opengamma.util.db.Paging;
import com.opengamma.util.db.PagingRequest;

/**
 * Test MongoDBConfigMaster.
 *
 * @param <T>  the configuration document type
 */
public abstract class MongoDBConfigTypeMasterTestCase<T extends Serializable> {

  private MongoDBConfigTypeMaster<T> _configMaster;
  private Class<?> _entityType;
  private Random _random = new Random();

  public MongoDBConfigTypeMasterTestCase(Class<?> entityType) {
    _entityType = entityType;
  }

  @Before
  public void setUp() throws Exception {
    _configMaster = createMongoConfigMaster();
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH));
  }

  @After
  public void tearDown() throws Exception {
    MongoDBConnectionSettings settings = getMongoDBConnectionSettings();
    Mongo mongo = new Mongo(settings.getHost(), settings.getPort());
    DB db = mongo.getDB(settings.getDatabase());
    String collectionName =  settings.getCollectionName() == null ? _entityType.getSimpleName() : settings.getCollectionName();
    DBCollection collection = db.getCollection(collectionName);
    collection.drop();
  }

  //-------------------------------------------------------------------------
  protected abstract MongoDBConfigTypeMaster<T> createMongoConfigMaster();
  protected abstract ConfigDocument<T> makeTestConfigDoc(int version);
  protected abstract MongoDBConnectionSettings getMongoDBConnectionSettings();
  protected abstract T makeRandomConfigDoc();
  protected abstract void assertConfigDocumentValue(T expected, T actual);

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void search_nullRequest() throws Exception {
    _configMaster.search(null);
  }

  @Test
  public void search_withHistory() throws Exception {
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(1)));
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(3)));
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(5)));
    ConfigDocument<T> doc3 = makeTestConfigDoc(3);
    doc3.setConfigId(doc2.getConfigId());
    doc3.setName("changeOfName");
    doc3 = _configMaster.update(doc3);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(6)));
    
    // no criteria, match current
    ConfigSearchResult<T> found = doSearch(null, null, 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match new name at latest instant
    found = doSearch("changeOfName", Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match new name case insensitive at latest instant
    found = doSearch("CHANGEOFNAME", Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match new name wildcard at latest instant
    found = doSearch("CHANGE*", Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match new name wildcard at latest instant
    found = doSearch("CHA??E*", Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match new name no wildcard, no match
    found = doSearch("CHANGE", Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(0, found.getDocuments().size());
    
    // match old name at specific instant
    found = doSearch(doc2.getName(), Instant.EPOCH.plusSeconds(4), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc2, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match old name at specific instant
    found = doSearch(doc1.getName(), Instant.EPOCH.plusSeconds(2), 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc1, found.getFirstDocument());
    assertEquals(Instant.EPOCH.plusSeconds(6), found.getFirstDocument().getLastReadInstant());
    
    // match old name at current instant, no match
    found = doSearch(doc1.getName(), null, 1, 20);
    assertEquals(0, found.getDocuments().size());
    
    // match old name at latest instant, no match
    found = doSearch(doc2.getName(), Instant.EPOCH.plusSeconds(6), 1, 20);
    assertEquals(0, found.getDocuments().size());
    
    // match new name at old instant, no match
    found = doSearch("changeOfName", Instant.EPOCH.plusSeconds(4), 1, 20);
    assertEquals(0, found.getDocuments().size());
    
    // match unknown name
    found = doSearch("Unknown", null, 1, 20);
    assertEquals(0, found.getDocuments().size());
    
    // match before earliest
    found = doSearch(null, Instant.EPOCH, 1, 20);
    assertEquals(0, found.getDocuments().size());
  }

  @Test
  public void search_withMultiple() throws Exception {
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(1)));
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(3)));
    ConfigDocument<T> doc2 = _configMaster.add(makeTestConfigDoc(2));
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(5)));
    ConfigDocument<T> doc3 = _configMaster.add(makeTestConfigDoc(3));
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(6)));
    
    // no criteria, match all
    ConfigSearchResult<T> found = doSearch(null, null, 1, 20);
    assertEquals(3, found.getDocuments().size());
    assertConfigDoc(doc1, found.getDocuments().get(0));
    assertConfigDoc(doc2, found.getDocuments().get(1));
    assertConfigDoc(doc3, found.getDocuments().get(2));
    
    // no criteria, match all, page 1
    found = doSearch(null, null, 1, 2);
    assertEquals(2, found.getDocuments().size());
    assertConfigDoc(doc1, found.getDocuments().get(0));
    assertConfigDoc(doc2, found.getDocuments().get(1));
    assertEquals(new Paging(1, 2, 3), found.getPaging());
    
    // no criteria, match all, page 2
    found = doSearch(null, null, 2, 2);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc3, found.getDocuments().get(0));
    assertEquals(new Paging(2, 2, 3), found.getPaging());
    
    // match name
    found = doSearch(doc2.getName(), null, 1, 20);
    assertEquals(1, found.getDocuments().size());
    assertConfigDoc(doc2, found.getFirstDocument());
  }

  private ConfigSearchResult<T> doSearch(String name, Instant instant, int page, int pageSize) {
    ConfigSearchRequest request = new ConfigSearchRequest();
    request.setPagingRequest(PagingRequest.of(page, pageSize));
    request.setName(name);
    request.setVersionAsOfInstant(instant);
    return _configMaster.search(request);
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void get_nullUUID() throws Exception {
    _configMaster.get(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void get_notValidScheme() throws Exception {
    _configMaster.get(UniqueIdentifier.of("Rubbish", "1"));
  }

  @Test(expected = DataNotFoundException.class)
  public void get_notFound_versioned() throws Exception {
    _configMaster.get(UniqueIdentifier.of(MongoDBConfigTypeMaster.IDENTIFIER_SCHEME_DEFAULT, "1", "1"));
  }

  @Test(expected = DataNotFoundException.class)
  public void get_notFound_latest() throws Exception {
    _configMaster.get(UniqueIdentifier.of(MongoDBConfigTypeMaster.IDENTIFIER_SCHEME_DEFAULT, "1"));
  }

  @Test
  public void get() throws Exception {
    addRandomDocs();
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(1)));
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    assertNotNull(doc1);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(2)));
    ConfigDocument<T> found = _configMaster.get(doc1.getConfigId());
    assertNotNull(found);
    assertConfigDoc(doc1, found);
    assertEquals(Instant.EPOCH.plusSeconds(1), found.getVersionFromInstant());
    assertEquals(null, found.getVersionToInstant());
    assertEquals(Instant.EPOCH.plusSeconds(2), found.getLastReadInstant());
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(3)));
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    assertNotNull(doc2);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(4)));
    found = _configMaster.get(doc1.getConfigId().toLatest());
    assertNotNull(found);
    assertConfigDoc(doc2, found);
    assertEquals(Instant.EPOCH.plusSeconds(3), found.getVersionFromInstant());
    assertEquals(null, found.getVersionToInstant());
    assertEquals(Instant.EPOCH.plusSeconds(4), found.getLastReadInstant());
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void add_nullDocument() throws Exception {
    _configMaster.add(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void add_nullName() throws Exception {
    ConfigDocument<T> doc = makeTestConfigDoc(1);
    doc.setName(null);
    _configMaster.add(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void add_nullValue() throws Exception {
    ConfigDocument<T> doc = makeTestConfigDoc(1);
    doc.setValue(null);
    _configMaster.add(doc);
  }

  @Test
  public void add() throws Exception {
    addRandomDocs();
    
    ConfigDocument<T> doc = makeTestConfigDoc(1);
    String name = doc.getName();
    
    doc = _configMaster.add(doc);
    assertNotNull(doc);
    assertNotNull(doc.getConfigId());
    assertEquals(1, doc.getVersionNumber());
    assertEquals(name, doc.getName());
    assertEquals(Instant.EPOCH, doc.getVersionFromInstant());
    assertNull(doc.getVersionToInstant());
    assertEquals(Instant.EPOCH, doc.getLastReadInstant());
    
    // get fixed version
    ConfigDocument<T> fixed = _configMaster.get(doc.getConfigId());
    assertNotNull(fixed);
    assertConfigDoc(doc, fixed);
    
    // get latest
    ConfigDocument<T> latest = _configMaster.get(doc.getConfigId().toLatest());
    assertNotNull(latest);
    assertConfigDoc(doc, latest);
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void update_nullDocument() throws Exception {
    _configMaster.update(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_nullUID() throws Exception {
    ConfigDocument<T> doc = makeTestConfigDoc(2);
    doc.setConfigId(null);
    _configMaster.update(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_nullName() throws Exception {
    ConfigDocument<T> doc = makeTestConfigDoc(2);
    doc.setName(null);
    _configMaster.update(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_nullValue() throws Exception {
    ConfigDocument<T> doc = makeTestConfigDoc(2);
    doc.setValue(null);
    _configMaster.update(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_notLatest() throws Exception {
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    
    ConfigDocument<T> doc3 = makeTestConfigDoc(3);
    doc3.setConfigId(doc1.getConfigId());  // wrong version
    doc3.setName(doc1.getName());
    _configMaster.update(doc3);
  }

  @Test
  public void update() throws Exception {
    addRandomDocs();
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(1)));
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    assertEquals(1, doc1.getVersionNumber());
    assertEquals(Instant.EPOCH.plusSeconds(1), doc1.getVersionFromInstant());
    assertEquals(null, doc1.getVersionToInstant());
    assertEquals(Instant.EPOCH.plusSeconds(1), doc1.getLastReadInstant());
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(3)));
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    
    assertNotNull(doc2);
    assertEquals(doc1.getConfigId().toLatest(), doc2.getConfigId().toLatest());
    assertEquals(2, doc2.getVersionNumber());
    assertEquals(Instant.EPOCH.plusSeconds(3), doc2.getVersionFromInstant());
    assertEquals(null, doc2.getVersionToInstant());
    assertEquals(Instant.EPOCH.plusSeconds(3), doc2.getLastReadInstant());
    
    // version 1 end-dated
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(4)));
    doc1 = _configMaster.get(doc1.getConfigId());
    assertEquals(1, doc1.getVersionNumber());
    assertEquals(Instant.EPOCH.plusSeconds(1), doc1.getVersionFromInstant());
    assertEquals(Instant.EPOCH.plusSeconds(3), doc1.getVersionToInstant());
    assertEquals(Instant.EPOCH.plusSeconds(4), doc1.getLastReadInstant());
    
    // get latest
    ConfigDocument<T> latest = _configMaster.get(doc1.getConfigId().toLatest());
    assertEquals(2, latest.getVersionNumber());
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void remove_nullUUID() throws Exception {
    _configMaster.remove(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void remove_notLatest() throws Exception {
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    
    _configMaster.remove(doc1.getConfigId());
  }

  @Test
  public void remove() throws Exception {
    addRandomDocs();
    
    ConfigDocument<T> doc = makeTestConfigDoc(1);
    doc = _configMaster.add(doc);
    assertEquals(null, doc.getVersionToInstant());
    
    _configMaster.remove(doc.getConfigId());
    
    try {
      _configMaster.get(doc.getConfigId().toLatest());
      fail();
    } catch (DataNotFoundException ex) {
      // expected
    }
    
    ConfigDocument<T> removed = _configMaster.get(doc.getConfigId());
    assertEquals(Instant.EPOCH, removed.getVersionToInstant());
    assertConfigDoc(doc, removed);
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void searchHistoric_nullRequest() throws Exception {
    _configMaster.searchHistoric(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void searchHistoric_nullOid() throws Exception {
    ConfigSearchHistoricRequest historicRequest = new ConfigSearchHistoricRequest();
    _configMaster.searchHistoric(historicRequest);
  }

  @Test
  public void searchHistoric() throws Exception {
    addRandomDocs();
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(1)));
    ConfigDocument<T> doc1 = _configMaster.add(makeTestConfigDoc(1));
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(3)));
    ConfigDocument<T> doc2 = makeTestConfigDoc(2);
    doc2.setConfigId(doc1.getConfigId());
    doc2.setName(doc1.getName());
    doc2 = _configMaster.update(doc2);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(5)));
    ConfigDocument<T> doc3 = makeTestConfigDoc(3);
    doc3.setConfigId(doc2.getConfigId());
    doc3.setName(doc2.getName());
    doc3 = _configMaster.update(doc3);
    
    _configMaster.setTimeSource(TimeSource.fixed(Instant.EPOCH.plusSeconds(6)));
    
    // from infinity to infinity
    ConfigSearchHistoricRequest historicRequest = new ConfigSearchHistoricRequest();
    historicRequest.setConfigId(doc1.getConfigId());
    ConfigSearchHistoricResult<T> searchHistoric = _configMaster.searchHistoric(historicRequest);
    List<ConfigDocument<T>> allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(3, allDocs.size());
    assertConfigDoc(doc3, allDocs.get(0));
    assertConfigDoc(doc2, allDocs.get(1));
    assertConfigDoc(doc1, allDocs.get(2));
    
    // whole range
    historicRequest = new ConfigSearchHistoricRequest();
    historicRequest.setConfigId(doc1.getConfigId());
    historicRequest.setVersionsFromInstant(Instant.EPOCH);
    historicRequest.setVersionsToInstant(Instant.EPOCH.plusSeconds(6));
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(3, allDocs.size());
    assertConfigDoc(doc3, allDocs.get(0));
    assertConfigDoc(doc2, allDocs.get(1));
    assertConfigDoc(doc1, allDocs.get(2));
    
    // from start to infinity
    historicRequest.setVersionsFromInstant(Instant.EPOCH);
    historicRequest.setVersionsToInstant(null);
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(3, allDocs.size());
    assertConfigDoc(doc3, allDocs.get(0));
    assertConfigDoc(doc2, allDocs.get(1));
    assertConfigDoc(doc1, allDocs.get(2));
    
    // from second to infinity
    historicRequest.setVersionsFromInstant(Instant.EPOCH.plusSeconds(4));
    historicRequest.setVersionsToInstant(null);
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(2, allDocs.size());
    assertConfigDoc(doc3, allDocs.get(0));
    assertConfigDoc(doc2, allDocs.get(1));
    
    // from infinity to second
    historicRequest.setVersionsFromInstant(null);
    historicRequest.setVersionsToInstant(Instant.EPOCH.plusSeconds(4));
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(2, allDocs.size());
    assertConfigDoc(doc2, allDocs.get(0));
    assertConfigDoc(doc1, allDocs.get(1));
    
    // single instant before first
    historicRequest.setVersionsFromInstant(Instant.EPOCH);
    historicRequest.setVersionsToInstant(Instant.EPOCH);
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(0, allDocs.size());
    
    // single instant while first active
    historicRequest.setVersionsFromInstant(Instant.EPOCH.plusSeconds(2));
    historicRequest.setVersionsToInstant(Instant.EPOCH.plusSeconds(2));
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(1, allDocs.size());
    assertConfigDoc(doc1, allDocs.get(0));
    
    // range covering first
    historicRequest.setVersionsFromInstant(Instant.EPOCH);
    historicRequest.setVersionsToInstant(Instant.EPOCH.plusSeconds(2));
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(1, allDocs.size());
    assertConfigDoc(doc1, allDocs.get(0));
    
    // range covering second and third
    historicRequest.setVersionsFromInstant(Instant.EPOCH.plusSeconds(4));
    historicRequest.setVersionsToInstant(Instant.EPOCH.plusSeconds(6));
    searchHistoric = _configMaster.searchHistoric(historicRequest);
    allDocs = searchHistoric.getDocuments();
    assertNotNull(allDocs);
    assertEquals(2, allDocs.size());
    assertConfigDoc(doc3, allDocs.get(0));
    assertConfigDoc(doc2, allDocs.get(1));
  }

  //-------------------------------------------------------------------------
  private void addRandomDocs() {
    for (int i = 0; i < 10; i++) {
      String name = "RandName" + _random.nextInt();
      T doc = makeRandomConfigDoc();
      ConfigDocument<T> document = new ConfigDocument<T>();
      document.setValue(doc);
      document.setName(name);
      _configMaster.add(document);
    }
  }

  private void assertConfigDoc(ConfigDocument<T> expected, ConfigDocument<T> actual) {
    assertEquals(expected.getConfigId(), actual.getConfigId());
    assertEquals(expected.getVersionNumber(), actual.getVersionNumber());
    assertEquals(expected.getName(), actual.getName());
    assertConfigDocumentValue(expected.getValue(), actual.getValue());
  }

}
