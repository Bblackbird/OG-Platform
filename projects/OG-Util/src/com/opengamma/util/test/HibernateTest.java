/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

public abstract class HibernateTest extends DBTest {
  
  private SessionFactory _sessionFactory;
  private static int testCount = 0;
  
  protected HibernateTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
  }
  
  public SessionFactory getSessionFactory() {
    return _sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    _sessionFactory = sessionFactory;
  }
  
  public abstract Class<?>[] getHibernateMappingClasses();

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    Configuration configuration = getDbTool().getHibernateConfiguration();
    for (Class<?> clazz : getHibernateMappingClasses()) {
      configuration.addClass(clazz);
    }

    SessionFactory sessionFactory = configuration.buildSessionFactory();
    setSessionFactory(sessionFactory);

    testCount++;
  }
  
  @After
  public void tearDown() throws Exception {
    if (_sessionFactory != null) {
      _sessionFactory.close();
    }
    super.tearDown();
  }
  
}
