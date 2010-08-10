/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.master.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.financial.position.master.PortfolioTreePosition;
import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.financial.position.master.PositionSearchHistoricRequest;
import com.opengamma.financial.position.master.PositionSearchHistoricResult;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.db.PagingRequest;

/**
 * Tests QueryPositionDbPositionMasterWorker.
 */
public class QueryPositionDbPositionMasterWorkerSearchPositionHistoricTest extends AbstractDbPositionMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(QueryPositionDbPositionMasterWorkerSearchPositionHistoricTest.class);

  private DbPositionMasterWorker _worker;

  public QueryPositionDbPositionMasterWorkerSearchPositionHistoricTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    _worker = new QueryPositionDbPositionMasterWorker();
    _worker.init(_posMaster);
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    _worker = null;
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_searchPositionHistoric_documents() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(2, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);  // new version
    PositionDocument doc1 = test.getDocuments().get(1);  // old version
    
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "201"), doc0.getPortfolioId());
    assertEquals(UniqueIdentifier.of("DbPos", "211"), doc0.getParentNodeId());
    assertNotNull(doc0.getVersionFromInstant());
    assertEquals(null, doc0.getVersionToInstant());
    assertEquals(doc0.getVersionFromInstant(), doc0.getCorrectionFromInstant());
    assertEquals(null, doc0.getCorrectionToInstant());
    PortfolioTreePosition position0 = doc0.getPosition();
    assertNotNull(position0);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), position0.getUniqueIdentifier());
    assertEquals(BigDecimal.valueOf(222.987), position0.getQuantity());
    IdentifierBundle secKey0 = position0.getSecurityKey();
    assertNotNull(secKey0);
    assertEquals(1, secKey0.size());
    assertEquals(Identifier.of("TICKER", "IBMC"), secKey0.getIdentifiers().iterator().next());
    
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc1.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "201"), doc1.getPortfolioId());
    assertEquals(UniqueIdentifier.of("DbPos", "211"), doc1.getParentNodeId());
    assertNotNull(doc1.getVersionFromInstant());
    assertEquals(doc0.getVersionFromInstant(), doc1.getVersionToInstant());
    assertEquals(doc1.getVersionFromInstant(), doc1.getCorrectionFromInstant());
    assertEquals(null, doc1.getCorrectionToInstant());
    PortfolioTreePosition position1 = doc1.getPosition();
    assertNotNull(position1);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), position1.getUniqueIdentifier());
    assertEquals(BigDecimal.valueOf(221.987), position1.getQuantity());
    IdentifierBundle secKey1 = position1.getSecurityKey();
    assertNotNull(secKey1);
    assertEquals(1, secKey1.size());
    assertEquals(Identifier.of("TICKER", "IBMC"), secKey1.getIdentifiers().iterator().next());
  }

  @Test
  public void test_searchPositionHistoric_documentCountWhenMultipleSecurities() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "121");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(1, test.getPaging().getTotalItems());
    
    assertEquals(1, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);  // new version
    
    assertEquals(UniqueIdentifier.of("DbPos", "121", "0"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "101"), doc0.getPortfolioId());
    assertEquals(UniqueIdentifier.of("DbPos", "112"), doc0.getParentNodeId());
    assertNotNull(doc0.getVersionFromInstant());
    assertEquals(null, doc0.getVersionToInstant());
    assertEquals(doc0.getVersionFromInstant(), doc0.getCorrectionFromInstant());
    assertEquals(null, doc0.getCorrectionToInstant());
    PortfolioTreePosition position0 = doc0.getPosition();
    assertNotNull(position0);
    assertEquals(UniqueIdentifier.of("DbPos", "121", "0"), position0.getUniqueIdentifier());
    assertEquals(BigDecimal.valueOf(121.987), position0.getQuantity());
    IdentifierBundle secKey0 = position0.getSecurityKey();
    assertNotNull(secKey0);
    assertEquals(2, secKey0.size());
    assertTrue(secKey0.getIdentifiers().contains(Identifier.of("TICKER", "MSFT")));
    assertTrue(secKey0.getIdentifiers().contains(Identifier.of("NASDAQ", "Micro")));
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_searchPositionHistoric_noInstants() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(1, test.getPaging().getFirstItem());
    assertEquals(Integer.MAX_VALUE, test.getPaging().getPagingSize());
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertEquals(2, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    PositionDocument doc1 = test.getDocuments().get(1);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc1.getPositionId());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_searchPositionHistoric_noInstants_pageOne() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setPagingRequest(new PagingRequest(1, 1));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(1, test.getPaging().getFirstItem());
    assertEquals(1, test.getPaging().getPagingSize());
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertEquals(1, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
  }

  @Test
  public void test_searchPositionHistoric_noInstants_pageTwo() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setPagingRequest(new PagingRequest(2, 1));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertNotNull(test);
    assertNotNull(test.getPaging());
    assertEquals(2, test.getPaging().getFirstItem());
    assertEquals(1, test.getPaging().getPagingSize());
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertNotNull(test.getDocuments());
    assertEquals(1, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc0.getPositionId());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_searchPositionHistoric_versionsFrom_preFirst() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsFromInstant(_version1Instant.minusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertEquals(2, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    PositionDocument doc1 = test.getDocuments().get(1);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc1.getPositionId());
  }

  @Test
  public void test_searchPositionHistoric_versionsFrom_firstToSecond() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsFromInstant(_version1Instant.plusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertEquals(2, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    PositionDocument doc1 = test.getDocuments().get(1);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc1.getPositionId());
  }

  @Test
  public void test_searchPositionHistoric_versionsFrom_postSecond() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsFromInstant(_version2Instant.plusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(1, test.getPaging().getTotalItems());
    
    assertEquals(1, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_searchPositionHistoric_versionsTo_preFirst() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsToInstant(_version1Instant.minusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(0, test.getPaging().getTotalItems());
    
    assertEquals(0, test.getDocuments().size());
  }

  @Test
  public void test_searchPositionHistoric_versionsTo_firstToSecond() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsToInstant(_version1Instant.plusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(1, test.getPaging().getTotalItems());
    
    assertEquals(1, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc0.getPositionId());
  }

  @Test
  public void test_searchPositionHistoric_versionsTo_postSecond() {
    UniqueIdentifier oid = UniqueIdentifier.of("DbPos", "221");
    PositionSearchHistoricRequest request = new PositionSearchHistoricRequest(oid);
    request.setVersionsToInstant(_version2Instant.plusSeconds(5));
    PositionSearchHistoricResult test = _worker.searchPositionHistoric(request);
    
    assertEquals(2, test.getPaging().getTotalItems());
    
    assertEquals(2, test.getDocuments().size());
    PositionDocument doc0 = test.getDocuments().get(0);
    PositionDocument doc1 = test.getDocuments().get(1);
    assertEquals(UniqueIdentifier.of("DbPos", "221", "1"), doc0.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "221", "0"), doc1.getPositionId());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_worker.getClass().getSimpleName() + "[DbPos]", _worker.toString());
  }

}
