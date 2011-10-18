/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.opengamma.util.db.DbSource;
import com.opengamma.util.db.DbSourceFactoryBean;

/**
 * DB test involving Hibernate.
 */
public abstract class HibernateTest extends DbTest {
  
  private SessionFactory _sessionFactory;
  
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

  @BeforeMethod
  public void setUp() throws Exception {
    super.setUp();
    
    Configuration configuration = getDbTool().getTestHibernateConfiguration();
    for (Class<?> clazz : getHibernateMappingClasses()) {
      configuration.addClass(clazz);
    }

    SessionFactory sessionFactory = configuration.buildSessionFactory();
    setSessionFactory(sessionFactory);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    if (_sessionFactory != null) {
      _sessionFactory.close();
    }
    super.tearDown();
  }

  @Override
  public DbSource getDbSource() {
    DbSource source = super.getDbSource();
    DbSourceFactoryBean factory = new DbSourceFactoryBean(source);
    factory.setHibernateSessionFactory(getSessionFactory());
    return factory.createObject();
  }

}
