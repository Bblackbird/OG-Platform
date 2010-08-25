/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import com.opengamma.util.ArgumentChecker;
import com.sleepycat.je.Environment;

/**
 * Creates {@link BerkeleyDBBinaryDataStore} instances.
 */
public class BerkeleyDBBinaryDataStoreFactory implements BinaryDataStoreFactory {

  private final Environment _dbEnvironment;

  public BerkeleyDBBinaryDataStoreFactory(final Environment dbEnvironment) {
    ArgumentChecker.notNull(dbEnvironment, "dbEnvironment");
    _dbEnvironment = dbEnvironment;
  }

  @Override
  public BinaryDataStore createDataStore(ViewComputationCacheKey cacheKey) {
    String dbName = cacheKey.getViewName() + "-" + cacheKey.getCalculationConfigurationName() + "-" + cacheKey.getSnapshotTimestamp();
    BerkeleyDBBinaryDataStore dataStore = new BerkeleyDBBinaryDataStore(_dbEnvironment, dbName);
    dataStore.start();
    return dataStore;
  }

}
