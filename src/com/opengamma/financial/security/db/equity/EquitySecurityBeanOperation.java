/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db.equity;

import static com.opengamma.financial.security.db.Converters.currencyBeanToCurrency;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.financial.GICSCode;
import com.opengamma.financial.security.db.AbstractBeanOperation;
import com.opengamma.financial.security.db.CurrencyBean;
import com.opengamma.financial.security.db.ExchangeBean;
import com.opengamma.financial.security.db.HibernateSecurityMasterDao;
import com.opengamma.financial.security.db.OperationContext;
import com.opengamma.financial.security.equity.EquitySecurity;

/**
 * 
 */
public final class EquitySecurityBeanOperation extends AbstractBeanOperation<EquitySecurity, EquitySecurityBean> {

  /**
   * Singleton.
   */
  public static final EquitySecurityBeanOperation INSTANCE = new EquitySecurityBeanOperation();

  private EquitySecurityBeanOperation() {
    super("EQUITY", EquitySecurity.class, EquitySecurityBean.class);
  }

  public static GICSCode gicsCodeBeanToGICSCode(final GICSCodeBean gicsCodeBean) {
    if (gicsCodeBean == null) {
      return null;
    }
    return GICSCode.getInstance(gicsCodeBean.getName());
  }

  @Override
  public EquitySecurityBean createBean(final OperationContext context, final HibernateSecurityMasterDao secMasterSession, final EquitySecurity security) {
    GICSCodeBean gicsCodeBean = null;
    if (security.getGicsCode() != null) {
      gicsCodeBean = secMasterSession.getOrCreateGICSCodeBean(security.getGicsCode().toString(), "");
    }
    final EquitySecurityBean bean = createBean(secMasterSession.getOrCreateExchangeBean(security.getExchangeCode(), security.getExchange()), security.getCompanyName(), secMasterSession
        .getOrCreateCurrencyBean(security
        .getCurrency().getISOCode()), gicsCodeBean);
    bean.setShortName(security.getShortName());
    return bean;
  }

  public EquitySecurityBean createBean(final ExchangeBean exchange, final String companyName, final CurrencyBean currency, final GICSCodeBean gicsCode) {
    final EquitySecurityBean equity = new EquitySecurityBean();
    equity.setExchange(exchange);
    equity.setCompanyName(companyName);
    equity.setCurrency(currency);
    equity.setGICSCode(gicsCode);
    return equity;
  }

  public EquitySecurityBean createBean(final OperationContext context, final HibernateSecurityMasterDao secMasterSession, final Date effectiveDateTime, final boolean deleted, final Date lastModified,
      final String modifiedBy, final EquitySecurityBean firstVersion, final String displayName, final ExchangeBean exchange, final String companyName, final CurrencyBean currency,
      final GICSCodeBean gicsCode) {
    final EquitySecurityBean equity = createBean(exchange, companyName, currency, gicsCode);
    // base properties
    equity.setEffectiveDateTime(effectiveDateTime);
    equity.setDeleted(deleted);
    equity.setLastModifiedDateTime(lastModified);
    equity.setLastModifiedBy(modifiedBy);
    equity.setDisplayName(displayName);
    // first version
    equity.setFirstVersion(firstVersion);
    secMasterSession.persistSecurityBean(context, equity);
    return equity;
  }

  @Override
  public EquitySecurity createSecurity(final OperationContext context, final EquitySecurityBean bean) {
    final EquitySecurity security = new EquitySecurity(bean.getExchange().getDescription(), bean.getExchange().getName(), bean.getCompanyName(), currencyBeanToCurrency(bean.getCurrency()));
    security.setShortName(bean.getShortName());
    security.setGicsCode(gicsCodeBeanToGICSCode(bean.getGICSCode()));
    return security;
  }

  @Override
  public boolean beanEquals(final OperationContext context, EquitySecurityBean bean, EquitySecurity security) {
    return ObjectUtils.equals(bean.getShortName(), security.getShortName()) && ObjectUtils.equals(bean.getCompanyName(), security.getCompanyName())
        && ObjectUtils.equals(currencyBeanToCurrency(bean.getCurrency()), security.getCurrency())
        && ObjectUtils.equals(bean.getExchange().getName(), security.getExchange()) && ObjectUtils.equals(gicsCodeBeanToGICSCode(bean.getGICSCode()), security.getGicsCode());
  }

}
