/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.masterdb.security.hibernate.option;

import static com.opengamma.masterdb.security.hibernate.Converters.currencyBeanToCurrency;
import static com.opengamma.masterdb.security.hibernate.Converters.expiryBeanToExpiry;
import static com.opengamma.masterdb.security.hibernate.Converters.expiryToExpiryBean;
import static com.opengamma.masterdb.security.hibernate.Converters.externalIdBeanToExternalId;
import static com.opengamma.masterdb.security.hibernate.Converters.externalIdToExternalIdBean;

import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.masterdb.security.hibernate.AbstractSecurityBeanOperation;
import com.opengamma.masterdb.security.hibernate.HibernateSecurityMasterDao;
import com.opengamma.masterdb.security.hibernate.OperationContext;

/**
 * Conversion operation for OptionSecurity to/from OptionSecurityBean
 */
public final class SwaptionSecurityBeanOperation extends AbstractSecurityBeanOperation<SwaptionSecurity, SwaptionSecurityBean> {

  /**
   * Singleton instance.
   */
  public static final SwaptionSecurityBeanOperation INSTANCE = new SwaptionSecurityBeanOperation();

  private SwaptionSecurityBeanOperation() {
    super(SwaptionSecurity.SECURITY_TYPE, SwaptionSecurity.class, SwaptionSecurityBean.class);
  }

  @Override
  public SwaptionSecurityBean createBean(final OperationContext context, final HibernateSecurityMasterDao secMasterSession, final SwaptionSecurity security) {
    SwaptionSecurityBean bean = new SwaptionSecurityBean();
    bean.setCashSettled(security.isCashSettled());
    bean.setLongShort(security.isLong());
    bean.setExpiry(expiryToExpiryBean(security.getExpiry()));
    bean.setUnderlying(externalIdToExternalIdBean(security.getUnderlyingId()));
    bean.setPayer(security.isPayer());
    bean.setCurrency(secMasterSession.getOrCreateCurrencyBean(security.getCurrency().getCode()));
    return bean;
  }

  @Override
  public SwaptionSecurity createSecurity(OperationContext context, SwaptionSecurityBean bean) {
    return new SwaptionSecurity(bean.getPayer(), 
        externalIdBeanToExternalId(bean.getUnderlying()), 
        bean.getLongShort(), 
        expiryBeanToExpiry(bean.getExpiry()), 
        bean.getCashSettled(), 
        currencyBeanToCurrency(bean.getCurrency()));
  }

}
