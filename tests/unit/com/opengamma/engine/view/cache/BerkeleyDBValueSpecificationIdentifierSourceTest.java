/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.fudgemsg.FudgeContext;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.monitor.OperationTimer;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

// TODO kirk 2010-08-02 -- We should have concurrency performance tests as well.
// The correctness is largely taken care of by the database transactions, which go
// far beyond normal synchronization in handling concurrency.

/**
 * A simple unit test of {@link BerkeleyDBIdentifierMap}.
 */
public class BerkeleyDBValueSpecificationIdentifierSourceTest {
  private static final Logger s_logger = LoggerFactory.getLogger(BerkeleyDBValueSpecificationIdentifierSourceTest.class);
  private static Set<File> s_dbDirsToDelete = new HashSet<File>();
  
  protected File createDbDir(String methodName) {
    File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    File dbDir = new File(tmpDir, "BerkeleyDBValueSpecification-" + System.currentTimeMillis() + "-" + methodName);
    dbDir.mkdirs();
    s_dbDirsToDelete.add(dbDir);
    return dbDir;
  }
  
  protected Environment createDbEnvironment(File dbDir) {
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    envConfig.setTransactional(true);
    Environment dbEnvironment = new Environment(dbDir, envConfig);
    return dbEnvironment;
  }
  
  @AfterClass
  public static void deleteDbDirs() {
    for (File f : s_dbDirsToDelete) {
      try {
        s_logger.info("Deleting temp directory {}", f);
        FileUtils.deleteDirectory(f);
      } catch (IOException ioe) {
        s_logger.warn("Unable to recursively delete directory {}", f);
        // Just swallow it.
      }
    }
    s_dbDirsToDelete.clear();
  }
  
  private ValueSpecification getValueSpec(String valueName) {
    ValueSpecification valueSpec = new ValueSpecification(
        new ValueRequirement("value", 
            new ComputationTargetSpecification(
                ComputationTargetType.PRIMITIVE, 
                UniqueIdentifier.of("scheme", valueName))),
        "mockFunctionId");
    return valueSpec;
  }
  
  @Test
  public void simpleOperation() throws IOException {
    File dbDir = createDbDir("simpleOperation");
    Environment dbEnvironment = createDbEnvironment(dbDir);
    FudgeContext fudgeContext = new FudgeContext();
    
    BerkeleyDBIdentifierMap idSource = new BerkeleyDBIdentifierMap(dbEnvironment, BerkeleyDBIdentifierMap.DEFAULT_DATABASE_NAME, fudgeContext);
    idSource.start();
    
    Map<String, Long> identifiers = new HashMap<String, Long>();
    Set<Long> seenIdentifiers = new HashSet<Long>();
    for (int i = 0; i < 10; i++) {
      String valueName = "value-" + i;
      ValueSpecification valueSpec = getValueSpec(valueName);
      long identifier = idSource.getIdentifier(valueSpec);
      assertFalse(seenIdentifiers.contains(identifier));
      identifiers.put(valueName, identifier);
    }
    
    for (int j = 0; j < 5; j++) {
      for (int i = 0; i < 10; i++) {
        String valueName = "value-" + i;
        ValueSpecification valueSpec = getValueSpec(valueName);
        long identifier = idSource.getIdentifier(valueSpec);
        long existingIdentifier = identifiers.get(valueName);
        assertEquals(identifier, existingIdentifier);
      }
    }
    
    idSource.stop();
    
    dbEnvironment.close();
  }

  @Test
  public void reloadPreservesMaxValue() throws IOException {
    File dbDir = createDbDir("reloadPreservesMaxValue");
    Environment dbEnvironment = createDbEnvironment(dbDir);
    FudgeContext fudgeContext = new FudgeContext();
    
    BerkeleyDBIdentifierMap idSource = new BerkeleyDBIdentifierMap(dbEnvironment, BerkeleyDBIdentifierMap.DEFAULT_DATABASE_NAME, fudgeContext);
    idSource.start();
    String valueName = "value-5";
    ValueSpecification valueSpec = getValueSpec(valueName);
    long initialIdentifier = idSource.getIdentifier(valueSpec);
    
    // Cycle everything to simulate a clean shutdown and restart.
    idSource.stop();
    dbEnvironment.close();
    dbEnvironment = createDbEnvironment(dbDir);
    idSource = new BerkeleyDBIdentifierMap(dbEnvironment, BerkeleyDBIdentifierMap.DEFAULT_DATABASE_NAME, fudgeContext);
    idSource.start();
    
    // Check we get the same thing back.
    valueName = "value-5";
    valueSpec = getValueSpec(valueName);
    long identifier = idSource.getIdentifier(valueSpec);
    assertEquals(initialIdentifier, identifier);
    
    // Check that the next one is the previous max + 1
    valueName = "value-99999";
    valueSpec = getValueSpec(valueName);
    identifier = idSource.getIdentifier(valueSpec);
    assertEquals(initialIdentifier + 1, identifier);
  }
  
  @Test
  public void putPerformanceTest() {
    final int numRequirementNames = 100;
    final int numIdentifiers = 100;
    final long numSpecifications = ((long) numRequirementNames) * ((long) numIdentifiers);
    File dbDir = createDbDir("putPerformanceTest");
    Environment dbEnvironment = createDbEnvironment(dbDir);
    FudgeContext fudgeContext = new FudgeContext();
    BerkeleyDBIdentifierMap idSource = new BerkeleyDBIdentifierMap(dbEnvironment, BerkeleyDBIdentifierMap.DEFAULT_DATABASE_NAME, fudgeContext);
    idSource.start();
    
    OperationTimer timer = new OperationTimer(s_logger, "Put performance test with {} elements", numSpecifications);
    
    bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idSource);
    
    idSource.stop();
    long numMillis = timer.finished();
    
    double msPerPut = ((double) numMillis) / ((double) numSpecifications);
    double putsPerSecond = 1000.0 / msPerPut;
    
    s_logger.info("Split time was {}ms/put, {}puts/sec", msPerPut, putsPerSecond);
    
    dbEnvironment.close();
  }

  /**
   * @param numRequirementNames
   * @param numIdentifiers
   * @param idSource
   */
  private void bulkOperationGetIdentifier(final int numRequirementNames, final int numIdentifiers, BerkeleyDBIdentifierMap idSource) {
    for (int iRequirementName = 0; iRequirementName < numRequirementNames; iRequirementName++) {
      String requirementName = "req-" + iRequirementName;
      
      for (int iIdentifier = 0; iIdentifier < numIdentifiers; iIdentifier++) {
        String identifierName = "identifier-" + iIdentifier;
        ValueSpecification valueSpec = new ValueSpecification(new ValueRequirement(requirementName, 
            new ComputationTargetSpecification(
                ComputationTargetType.PRIMITIVE, 
                UniqueIdentifier.of("scheme", identifierName))),
            "mockFunctionId");

        // Just throw away the actual identifier. We don't care.
        idSource.getIdentifier(valueSpec);
      }
    }
  }

  @Test
  public void getPerformanceTest() {
    final int numRequirementNames = 100;
    final int numIdentifiers = 100;
    final long numSpecifications = ((long) numRequirementNames) * ((long) numIdentifiers);
    File dbDir = createDbDir("getPerformanceTest");
    Environment dbEnvironment = createDbEnvironment(dbDir);
    FudgeContext fudgeContext = new FudgeContext();
    BerkeleyDBIdentifierMap idSource = new BerkeleyDBIdentifierMap(dbEnvironment, BerkeleyDBIdentifierMap.DEFAULT_DATABASE_NAME, fudgeContext);
    idSource.start();
    
    bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idSource);
    
    OperationTimer timer = new OperationTimer(s_logger, "Get performance test with {} elements", numSpecifications);
    
    bulkOperationGetIdentifier(numRequirementNames, numIdentifiers, idSource);
    
    long numMillis = timer.finished();
    idSource.stop();
    
    double msPerPut = ((double) numMillis) / ((double) numSpecifications);
    double putsPerSecond = 1000.0 / msPerPut;
    
    s_logger.info("Split time was {}ms/get, {}gets/sec", msPerPut, putsPerSecond);
    
    dbEnvironment.close();
  }
}
