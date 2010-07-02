/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import com.opengamma.OpenGammaRuntimeException;

/**
 * 
 * 
 */
public class TestProperties {

  private static final String DEFAULT_PROPS_FILE_NAME = "tests.properties";

  private static String _baseDir = null;
  private static Properties _props = null;
  
  public static synchronized void setBaseDir(String dir) {
    if (_props != null) {
      throw new IllegalStateException("Properties already loaded");
    }
    _baseDir = dir;
  }
  
  public static synchronized Properties getTestProperties() {
    if (_props == null) {
      _props = new Properties();
      
      String propsFileName = DEFAULT_PROPS_FILE_NAME;
      String overridePropsFileName = System.getProperty("test.properties"); // passed in by Ant
      if (overridePropsFileName != null) {
        propsFileName = overridePropsFileName;
      }
      
      File file = new File(_baseDir, propsFileName);
      System.err.println(file.getAbsoluteFile());
      try {
        FileInputStream fis = new FileInputStream(file);
        _props.load(fis);
        fis.close();
      } catch (IOException e) {
        throw new OpenGammaRuntimeException("Could not read " + propsFileName, e);
      }
    }
    return _props;
  }

  public static Collection<String> getAllSupportedDatabaseTypes() {
    return Arrays.asList(new String[] { "derby", "postgres" });
  }

  /**
   * @return A singleton collection containing the String passed in), except if the type is ALL (case
   *         insensitive), in which case all supported database types are returned.
   */
  public static Collection<String> getDatabaseTypes(String commandLineDbType) {
    ArrayList<String> dbTypes = new ArrayList<String>();
    if (commandLineDbType.trim().equalsIgnoreCase("all")) {
      dbTypes.addAll(getAllSupportedDatabaseTypes());
    } else {
      dbTypes.add(commandLineDbType);
    }
    return dbTypes;
  }

  public static String getDbHost(String databaseType) {
    String dbHostProperty = databaseType + ".jdbc.url";
    String dbHost = getTestProperties().getProperty(dbHostProperty);
    if (dbHost == null) {
      throw new OpenGammaRuntimeException("Property " + dbHostProperty
          + " not found");
    }
    return dbHost;
  }

  public static String getDbUsername(String databaseType) {
    String userProperty = databaseType + ".jdbc.username";
    String user = getTestProperties().getProperty(userProperty);
    if (user == null) {
      throw new OpenGammaRuntimeException("Property " + userProperty
          + " not found");
    }
    return user;
  }

  public static String getDbPassword(String databaseType) {
    String passwordProperty = databaseType + ".jdbc.password";
    String password = getTestProperties().getProperty(passwordProperty);
    if (password == null) {
      throw new OpenGammaRuntimeException("Property " + passwordProperty
          + " not found");
    }
    return password;
  }
  
  public static Collection<String> getScriptDirs() {
    Collection<String> dirs = new ArrayList<String>();

    int i = 1;
    String dir = getTestProperties().getProperty("db.script.dir." + i);
    while (dir != null) {
      dirs.add(dir); // may be null -> 
      i++;
      dir = getTestProperties().getProperty("db.script.dir." + i);
    } 
    
    if (dirs.isEmpty()) {
      dirs.add(_baseDir); // if _baseDir == null -> working directory
    }
    
    return dirs;    
  }
  
  public static DBTool getDbTool(String databaseType) {
    String dbHost = getDbHost(databaseType);
    String user = getDbUsername(databaseType);
    String password = getDbPassword(databaseType);
    
    DBTool dbtool = new DBTool(dbHost, user, password);
    
    for (String scriptDir : getScriptDirs()) {
      dbtool.addDbScriptDirectory(scriptDir);      
    }
    
    dbtool.initialise();
    return dbtool;
  }

}
