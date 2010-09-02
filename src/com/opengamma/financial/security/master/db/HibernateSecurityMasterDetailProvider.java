/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.master.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.base.Objects;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.engine.security.DefaultSecurity;
import com.opengamma.engine.security.Security;
import com.opengamma.financial.security.db.SecurityBeanOperation;
import com.opengamma.financial.security.db.HibernateSecurityMasterDao;
import com.opengamma.financial.security.db.HibernateSecurityMasterSession;
import com.opengamma.financial.security.db.OperationContext;
import com.opengamma.financial.security.db.SecurityBean;
import com.opengamma.financial.security.db.bond.BondSecurityBeanOperation;
import com.opengamma.financial.security.db.cash.CashSecurityBeanOperation;
import com.opengamma.financial.security.db.equity.EquitySecurityBeanOperation;
import com.opengamma.financial.security.db.fra.FRASecurityBeanOperation;
import com.opengamma.financial.security.db.future.FutureSecurityBeanOperation;
import com.opengamma.financial.security.db.option.OptionSecurityBeanOperation;
import com.opengamma.financial.security.db.swap.SwapSecurityBeanOperation;

/**
 * Provides access to persist the full bean structure of the security.
 * This supports the default {@link DbSecurityMaster} implementations.
 */
public class HibernateSecurityMasterDetailProvider implements SecurityMasterDetailProvider {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(HibernateSecurityMasterDetailProvider.class);
  private static final ConcurrentMap<Class<?>, SecurityBeanOperation<?, ?>> BEAN_OPERATIONS_BY_SECURITY = new ConcurrentHashMap<Class<?>, SecurityBeanOperation<?, ?>>();
  private static final ConcurrentMap<Class<?>, SecurityBeanOperation<?, ?>> BEAN_OPERATIONS_BY_BEAN = new ConcurrentHashMap<Class<?>, SecurityBeanOperation<?, ?>>();

  /**
   * The Hibernate Spring template.
   */
  private HibernateTemplate _hibernateTemplate;
  /**
   * The operation context for management additional resources.
   */
  private final OperationContext _operationContext = new OperationContext();

  //-------------------------------------------------------------------------
  private static void loadBeanOperation(final SecurityBeanOperation<?, ?> beanOperation) {
    BEAN_OPERATIONS_BY_SECURITY.put(beanOperation.getSecurityClass(), beanOperation);
    BEAN_OPERATIONS_BY_BEAN.put(beanOperation.getBeanClass(), beanOperation);
  }

  private static SecurityBeanOperation<?, ?> getBeanOperation(final ConcurrentMap<Class<?>, SecurityBeanOperation<?, ?>> map, final Class<?> clazz) {
    SecurityBeanOperation<?, ?> beanOperation = map.get(clazz);
    if (beanOperation != null) {
      return beanOperation;
    }
    if (clazz.getSuperclass() == null) {
      return null;
    }
    beanOperation = getBeanOperation(map, clazz.getSuperclass());
    if (beanOperation != null) {
      map.put(clazz, beanOperation);
    }
    return beanOperation;
  }

  @SuppressWarnings("unchecked")
  private static <T extends Security> SecurityBeanOperation<T, SecurityBean> getBeanOperation(final T security) {
    final SecurityBeanOperation<?, ?> beanOperation = getBeanOperation(BEAN_OPERATIONS_BY_SECURITY, security.getClass());
    if (beanOperation == null) {
      throw new OpenGammaRuntimeException("can't find BeanOperation for " + security);
    }
    return (SecurityBeanOperation<T, SecurityBean>) beanOperation;
  }

  @SuppressWarnings("unchecked")
  private static <T extends SecurityBean> SecurityBeanOperation<Security, T> getBeanOperation(final T bean) {
    final SecurityBeanOperation<?, ?> beanOperation = getBeanOperation(BEAN_OPERATIONS_BY_BEAN, bean.getClass());
    if (beanOperation == null) {
      throw new OpenGammaRuntimeException("can't find BeanOperation for " + bean);
    }
    return (SecurityBeanOperation<Security, T>) beanOperation;
  }

  static {
    // TODO 2010-07-21 Should we load these from a .properties file like the other factories
    loadBeanOperation(BondSecurityBeanOperation.INSTANCE);
    loadBeanOperation(CashSecurityBeanOperation.INSTANCE);
    loadBeanOperation(EquitySecurityBeanOperation.INSTANCE);
    loadBeanOperation(FRASecurityBeanOperation.INSTANCE);
    loadBeanOperation(OptionSecurityBeanOperation.INSTANCE);
    loadBeanOperation(FutureSecurityBeanOperation.INSTANCE);
    loadBeanOperation(SwapSecurityBeanOperation.INSTANCE);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the context for additional resources.
   * @return the context
   */
  protected OperationContext getOperationContext() {
    return _operationContext;
  }

  /**
   * Sets the Hibernate session factory.
   * @param sessionFactory  the session factory.
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    _hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  /**
   * Gets the Hibernate Spring template.
   * @return the template
   */
  protected HibernateTemplate getHibernateTemplate() {
    return _hibernateTemplate;
  }

  /**
   * Gets the session DAO.
   * @param session  the session
   * @return the DAO
   */
  protected HibernateSecurityMasterDao getHibernateSecurityMasterSession(final Session session) {
    return new HibernateSecurityMasterSession(session);
  }

  //-------------------------------------------------------------------------
  @Override
  public DefaultSecurity loadSecurityDetail(final DefaultSecurity base) {
    s_logger.debug("loading detail for security {}", base.getUniqueIdentifier());
    return (DefaultSecurity) getHibernateTemplate().execute(new HibernateCallback() {
      @SuppressWarnings("unchecked")
      @Override
      public Object doInHibernate(Session session) throws HibernateException, SQLException {
        HibernateSecurityMasterDao secMasterSession = getHibernateSecurityMasterSession(session);
        SecurityBean security = secMasterSession.getSecurityBean(base);
        if (security == null) {
          s_logger.debug("no detail found for security {}", base.getUniqueIdentifier());
          return base;
        }
        final SecurityBeanOperation beanOperation = getBeanOperation(security);
        security = beanOperation.resolve(getOperationContext(), secMasterSession, null, security);
        final DefaultSecurity result = (DefaultSecurity) beanOperation.createSecurity(getOperationContext(), security);
        if (Objects.equal(base.getSecurityType(), result.getSecurityType()) == false) {
          throw new IllegalStateException("Security type returned by Hibernate load does not match");
        }
        result.setUniqueIdentifier(base.getUniqueIdentifier());
        result.setName(base.getName());
        result.setIdentifiers(base.getIdentifiers());
        return result;
      }
    });
  }

  @Override
  public void storeSecurityDetail(final DefaultSecurity security) {
    s_logger.debug("storing detail for security {}", security.getUniqueIdentifier());
    if (security.getClass() == DefaultSecurity.class) {
      return;  // no detail to store
    }
    getHibernateTemplate().execute(new HibernateCallback() {
      @SuppressWarnings("unchecked")
      @Override
      public Object doInHibernate(final Session session) throws HibernateException, SQLException {
        final HibernateSecurityMasterDao secMasterSession = getHibernateSecurityMasterSession(session);
        final SecurityBeanOperation beanOperation = getBeanOperation(security);
        final Date now = new Date();
        secMasterSession.createSecurityBean(getOperationContext(), beanOperation, now, security);
        return null;
      }
    });
  }

}
