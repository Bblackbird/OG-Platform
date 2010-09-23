/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.master.db;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.TimeZone;

import javax.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.position.master.ManageablePosition;
import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.financial.position.master.PositionSearchHistoricRequest;
import com.opengamma.financial.position.master.PositionSearchHistoricResult;
import com.opengamma.id.Identifier;
import com.opengamma.id.UniqueIdentifier;

/**
 * Tests ModifyPositionDbPositionMasterWorker.
 */
public class ModifyPositionDbPositionMasterWorkerCorrectPositionTest extends AbstractDbPositionMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(ModifyPositionDbPositionMasterWorkerCorrectPositionTest.class);

  private ModifyPositionDbPositionMasterWorker _worker;
  private DbPositionMasterWorker _queryWorker;

  public ModifyPositionDbPositionMasterWorkerCorrectPositionTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    _worker = new ModifyPositionDbPositionMasterWorker();
    _worker.init(_posMaster);
    _queryWorker = new QueryPositionDbPositionMasterWorker();
    _queryWorker.init(_posMaster);
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    _worker = null;
    _queryWorker = null;
  }

  //-------------------------------------------------------------------------
  @Test(expected = NullPointerException.class)
  public void test_correctPosition_nullDocument() {
    _worker.correctPosition(null);
  }

  @Test(expected = NullPointerException.class)
  public void test_correctPosition_noPositionId() {
    ManageablePosition position = new ManageablePosition(BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument doc = new PositionDocument();
    doc.setPosition(position);
    _worker.correctPosition(doc);
  }

  @Test(expected = NullPointerException.class)
  public void test_correctPosition_noPosition() {
    PositionDocument doc = new PositionDocument();
    doc.setPositionId(UniqueIdentifier.of("DbPos", "121", "0"));
    _worker.correctPosition(doc);
  }

  @Test(expected = DataNotFoundException.class)
  public void test_correctPosition_notFound() {
    ManageablePosition pos = new ManageablePosition(BigDecimal.TEN, Identifier.of("A", "B"));
    pos.setUniqueIdentifier(UniqueIdentifier.of("DbPos", "0", "0"));
    PositionDocument doc = new PositionDocument(pos);
    _worker.correctPosition(doc);
  }

//  @Test(expected = IllegalArgumentException.class)
//  public void test_correctPosition_notLatestCorrection() {
//    PortfolioTreePosition pos = new PortfolioTreePosition(UniqueIdentifier.of("DbPos", "221", "221"), BigDecimal.TEN, Identifier.of("A", "B"));
//    PositionDocument doc = new PositionDocument(pos);
//    _worker.correctPosition(doc);
//  }

  @Test
  public void test_correctPosition_getUpdateGet() {
    Instant now = Instant.now(_posMaster.getTimeSource());
    
    PositionDocument base = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "0"));
    ManageablePosition pos = new ManageablePosition(BigDecimal.TEN, Identifier.of("A", "B"));
    pos.setUniqueIdentifier(UniqueIdentifier.of("DbPos", "121", "0"));
    PositionDocument input = new PositionDocument(pos);
    
    PositionDocument corrected = _worker.correctPosition(input);
    assertEquals(false, base.getPositionId().equals(corrected.getPositionId()));
    assertEquals(base.getPortfolioId(), corrected.getPortfolioId());
    assertEquals(base.getParentNodeId(), corrected.getParentNodeId());
    assertEquals(base.getVersionFromInstant(), corrected.getVersionFromInstant());
    assertEquals(base.getVersionToInstant(), corrected.getVersionToInstant());
    assertEquals(now, corrected.getCorrectionFromInstant());
    assertEquals(null, corrected.getCorrectionToInstant());
    assertEquals(input.getPosition(), corrected.getPosition());
    
    PositionDocument old = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "0"));
    assertEquals(base.getPositionId(), old.getPositionId());
    assertEquals(base.getPortfolioId(), old.getPortfolioId());
    assertEquals(base.getParentNodeId(), old.getParentNodeId());
    assertEquals(base.getVersionFromInstant(), old.getVersionFromInstant());
    assertEquals(base.getVersionToInstant(), old.getVersionToInstant());
    assertEquals(base.getCorrectionFromInstant(), old.getCorrectionFromInstant());
    assertEquals(now, old.getCorrectionToInstant());  // old version ended
    assertEquals(base.getPosition(), old.getPosition());
    
    PositionSearchHistoricRequest search = new PositionSearchHistoricRequest(base.getPositionId(), now, null);
    PositionSearchHistoricResult searchResult = _queryWorker.searchPositionHistoric(search);
    assertEquals(2, searchResult.getDocuments().size());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_worker.getClass().getSimpleName() + "[DbPos]", _worker.toString());
  }

}
