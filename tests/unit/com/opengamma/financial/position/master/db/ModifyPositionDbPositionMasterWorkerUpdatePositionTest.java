/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.master.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.TimeZone;

import javax.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;

import com.opengamma.DataNotFoundException;
import com.opengamma.engine.position.PositionImpl;
import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.financial.position.master.PositionSearchHistoricRequest;
import com.opengamma.financial.position.master.PositionSearchHistoricResult;
import com.opengamma.id.Identifier;
import com.opengamma.id.UniqueIdentifier;

/**
 * Tests ModifyPositionDbPositionMasterWorker.
 */
public class ModifyPositionDbPositionMasterWorkerUpdatePositionTest extends AbstractDbPositionMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(ModifyPositionDbPositionMasterWorkerUpdatePositionTest.class);

  private ModifyPositionDbPositionMasterWorker _worker;
  private QueryPositionDbPositionMasterWorker _queryWorker;

  public ModifyPositionDbPositionMasterWorkerUpdatePositionTest(String databaseType, String databaseVersion) {
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
  public void test_updatePosition_nullDocument() {
    _worker.updatePosition(null);
  }

  @Test(expected = NullPointerException.class)
  public void test_updatePosition_noPositionId() {
    PositionImpl position = new PositionImpl(BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument doc = new PositionDocument();
    doc.setPosition(position);
    _worker.updatePosition(doc);
  }

  @Test(expected = NullPointerException.class)
  public void test_updatePosition_noPosition() {
    PositionDocument doc = new PositionDocument();
    doc.setPositionId(UniqueIdentifier.of("DbPos", "121", "121"));
    _worker.updatePosition(doc);
  }

  @Test(expected = DataNotFoundException.class)
  public void test_updatePosition_notFound() {
    PositionImpl pos = new PositionImpl(UniqueIdentifier.of("DbPos", "0", "0"), BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument doc = new PositionDocument(pos);
    _worker.updatePosition(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_updatePosition_notLatestVersion() {
    PositionImpl pos = new PositionImpl(UniqueIdentifier.of("DbPos", "221", "221"), BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument doc = new PositionDocument(pos);
    _worker.updatePosition(doc);
  }

  @Test
  public void test_updatePosition_getUpdateGet() {
    Instant now = Instant.now(_posMaster.getTimeSource());
    
    PositionDocument base = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "121"));
    PositionImpl pos = new PositionImpl(UniqueIdentifier.of("DbPos", "121", "121"), BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument input = new PositionDocument(pos);
    
    PositionDocument updated = _worker.updatePosition(input);
    assertEquals(false, base.getPositionId().equals(updated.getPositionId()));
    assertEquals(base.getPortfolioId(), updated.getPortfolioId());
    assertEquals(base.getParentNodeId(), updated.getParentNodeId());
    assertEquals(now, updated.getVersionFromInstant());
    assertEquals(null, updated.getVersionToInstant());
    assertEquals(now, updated.getCorrectionFromInstant());
    assertEquals(null, updated.getCorrectionToInstant());
    assertEquals(input.getPosition(), updated.getPosition());
    
    PositionDocument old = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "121"));
    assertEquals(base.getPositionId(), old.getPositionId());
    assertEquals(base.getPortfolioId(), old.getPortfolioId());
    assertEquals(base.getParentNodeId(), old.getParentNodeId());
    assertEquals(base.getVersionFromInstant(), old.getVersionFromInstant());
    assertEquals(now, old.getVersionToInstant());  // old version ended
    assertEquals(base.getCorrectionFromInstant(), old.getCorrectionFromInstant());
    assertEquals(base.getCorrectionToInstant(), old.getCorrectionToInstant());
    assertEquals(base.getPosition(), old.getPosition());
    
    PositionSearchHistoricRequest search = new PositionSearchHistoricRequest(base.getPositionId(), null, now);
    PositionSearchHistoricResult searchResult = _queryWorker.searchPositionHistoric(search);
    assertEquals(2, searchResult.getDocuments().size());
  }

  @Test
  public void test_updatePosition_rollback() {
    ModifyPositionDbPositionMasterWorker w = new ModifyPositionDbPositionMasterWorker() {
      protected String sqlInsertSecurityKey() {
        return "INSERT";  // bad sql
      };
    };
    w.init(_posMaster);
    final PositionDocument base = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "121"));
    PositionImpl pos = new PositionImpl(UniqueIdentifier.of("DbPos", "121", "121"), BigDecimal.TEN, Identifier.of("A", "B"));
    PositionDocument input = new PositionDocument(pos);
    try {
      w.updatePosition(input);
      fail();
    } catch (BadSqlGrammarException ex) {
      // expected
    }
    final PositionDocument test = _queryWorker.getPosition(UniqueIdentifier.of("DbPos", "121", "121"));
    
    assertEquals(base, test);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_worker.getClass().getSimpleName() + "[DbPos]", _worker.toString());
  }

}
