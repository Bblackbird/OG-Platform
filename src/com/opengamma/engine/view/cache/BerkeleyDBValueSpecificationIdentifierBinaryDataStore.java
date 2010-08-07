/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.util.ArgumentChecker;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * An implementation of {@link ValueSpecificationIdentifierBinaryDataStore} which backs all data
 * on a BerkeleyDB table. 
 */
public class BerkeleyDBValueSpecificationIdentifierBinaryDataStore extends AbstractBerkeleyDBComponent implements ValueSpecificationIdentifierBinaryDataStore {
  private static final Logger s_logger = LoggerFactory.getLogger(BerkeleyDBValueSpecificationIdentifierBinaryDataStore.class);

  public BerkeleyDBValueSpecificationIdentifierBinaryDataStore(Environment dbEnvironment, String databaseName) {
    super(dbEnvironment, databaseName);
  }

  @Override
  protected DatabaseConfig getDatabaseConfig() {
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setAllowCreate(true);
    dbConfig.setTransactional(false);
    // TODO kirk 2010-08-07 -- For Batch operation, this should be set to false probably.
    dbConfig.setTemporary(true);
    // TODO kirk 2010-08-07 -- For Batch operation, this should be set to true probably.
    dbConfig.setDeferredWrite(false);
    return dbConfig;
  }

  @Override
  public void delete() {
    getDatabase().close();
    // TODO kirk 2010-08-07 -- For batch operation, we'd have to explicitly remove the DB as well.
    //getDbEnvironment().removeDatabase(null, getDatabaseName());
    stop();
  }

  @Override
  public byte[] get(long identifier) {
    if (!isRunning()) {
      s_logger.info("Starting on first call as wasn't called as part of lifecycle interface");
      start();
    }
    DatabaseEntry keyEntry = new DatabaseEntry();
    LongBinding.longToEntry(identifier, keyEntry);
    DatabaseEntry valueEntry = new DatabaseEntry();
    OperationStatus opStatus = getDatabase().get(null, keyEntry, valueEntry, LockMode.READ_UNCOMMITTED);
    switch (opStatus) {
      case SUCCESS:
        return valueEntry.getData();
      default:
        s_logger.debug("{} - No record available for identifier {} status {}", new Object[]{getDatabaseName(), identifier, opStatus});
        return null;
    }
  }

  @Override
  public void put(long identifier, byte[] data) {
    if (!isRunning()) {
      s_logger.info("Starting on first call as wasn't called as part of lifecycle interface");
      start();
    }
    ArgumentChecker.notNull(data, "data to store");
    DatabaseEntry keyEntry = new DatabaseEntry();
    LongBinding.longToEntry(identifier, keyEntry);
    DatabaseEntry valueEntry = new DatabaseEntry(data);
    OperationStatus opStatus = getDatabase().put(null, keyEntry, valueEntry);
    switch (opStatus) {
      case SUCCESS:
        return;
      default:
        s_logger.warn("{} - Unable to write to identifier {} status {}", new Object[]{getDatabaseName(), identifier, opStatus});
    }
  }

}
