/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 
 */
abstract public class TransactionalHibernateTest extends HibernateTest {
  
  private HibernateTransactionManager _transactionManager;
  private TransactionStatus _transaction;
  
  public TransactionalHibernateTest(String databaseType, final String databaseVersion) {
    super(databaseType, databaseVersion);
  }
  
  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    _transactionManager = new HibernateTransactionManager(getSessionFactory());
    _transaction = _transactionManager.getTransaction(new DefaultTransactionDefinition());
  }
  
  @After
  public void tearDown() throws Exception {
    if (_transaction != null && !_transaction.isRollbackOnly() && !_transaction.isCompleted()) {
      _transactionManager.commit(_transaction);
    }
  }

  public HibernateTransactionManager getHibernateTransactionManager() {
    return _transactionManager;
  }

  public TransactionStatus getHibernateTransaction() {
    return _transaction;
  }
  
}
