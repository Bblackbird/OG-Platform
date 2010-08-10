/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.master.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.TimeZone;

import javax.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.position.master.PortfolioTreePosition;
import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Tests ModifyPositionDbPositionMasterWorker.
 */
public class ModifyPositionDbPositionMasterWorkerRemovePositionTest extends AbstractDbPositionMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(ModifyPositionDbPositionMasterWorkerRemovePositionTest.class);

  private ModifyPositionDbPositionMasterWorker _worker;
  private DbPositionMasterWorker _queryWorker;

  public ModifyPositionDbPositionMasterWorkerRemovePositionTest(String databaseType, String databaseVersion) {
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
  public void test_removePosition_nullUID() {
    _worker.removePosition(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_removePosition_nonVersionedUID() {
    _worker.removePosition(UniqueIdentifier.of("DbPos", "121"));
  }

  @Test(expected = DataNotFoundException.class)
  public void test_removePosition_versioned_notFound() {
    UniqueIdentifier uid = UniqueIdentifier.of("DbPos", "0", "0");
    _worker.removePosition(uid);
  }

  @Test
  public void test_removePosition_removed() {
    Instant now = Instant.now(_posMaster.getTimeSource());
    
    UniqueIdentifier uid = UniqueIdentifier.of("DbPos", "122", "0");
    _worker.removePosition(uid);
    PositionDocument test = _queryWorker.getPosition(uid);
    
    assertEquals(uid, test.getPositionId());
    assertEquals(UniqueIdentifier.of("DbPos", "101"), test.getPortfolioId());
    assertEquals(UniqueIdentifier.of("DbPos", "112"), test.getParentNodeId());
    assertEquals(_version1Instant, test.getVersionFromInstant());
    assertEquals(now, test.getVersionToInstant());
    assertEquals(_version1Instant, test.getCorrectionFromInstant());
    assertEquals(null, test.getCorrectionToInstant());
    PortfolioTreePosition position = test.getPosition();
    assertNotNull(position);
    assertEquals(uid, position.getUniqueIdentifier());
    assertEquals(BigDecimal.valueOf(122.987), position.getQuantity());
    IdentifierBundle secKey = position.getSecurityKey();
    assertNotNull(secKey);
    assertEquals(1, secKey.size());
    assertEquals(Identifier.of("TICKER", "ORCL"), secKey.getIdentifiers().iterator().next());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_worker.getClass().getSimpleName() + "[DbPos]", _worker.toString());
  }

}
