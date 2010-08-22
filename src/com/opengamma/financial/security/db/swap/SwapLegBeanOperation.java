/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */

package com.opengamma.financial.security.db.swap;

import static com.opengamma.financial.security.db.Converters.businessDayConventionBeanToBusinessDayConvention;
import static com.opengamma.financial.security.db.Converters.dayCountBeanToDayCount;
import static com.opengamma.financial.security.db.Converters.frequencyBeanToFrequency;
import static com.opengamma.financial.security.db.Converters.identifierBeanToIdentifier;
import static com.opengamma.financial.security.db.Converters.identifierToIdentifierBean;

import com.opengamma.financial.RegionRepository;
import com.opengamma.financial.security.db.HibernateSecurityMasterDao;
import com.opengamma.financial.security.swap.FixedInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingInterestRateLeg;
import com.opengamma.financial.security.swap.InterestRateLeg;
import com.opengamma.financial.security.swap.SwapLeg;
import com.opengamma.financial.security.swap.SwapLegVisitor;

/**
 * Bean conversion operations.
 */
public final class SwapLegBeanOperation {

  private SwapLegBeanOperation() {
  }

  public static SwapLegBean createBean(final HibernateSecurityMasterDao secMasterSession, final SwapLeg swapLeg) {
    return swapLeg.accept(new SwapLegVisitor<SwapLegBean>() {

      private SwapLegBean createSwapLegBean(SwapLeg swapLeg) {
        final SwapLegBean bean = new SwapLegBean();
        bean.setSwapLegType(SwapLegType.identify(swapLeg));
        bean.setBusinessDayConvention(secMasterSession.getOrCreateBusinessDayConventionBean(swapLeg.getBusinessDayConvention().getConventionName()));
        bean.setDayCount(secMasterSession.getOrCreateDayCountBean(swapLeg.getDayCount().getConventionName()));
        bean.setFrequency(secMasterSession.getOrCreateFrequencyBean(swapLeg.getFrequency().getConventionName()));
        bean.setNotional(NotionalBeanOperation.createBean(secMasterSession, swapLeg.getNotional()));
        bean.setRegion(identifierToIdentifierBean(swapLeg.getRegionIdentifier()));
        return bean;
      }

      private SwapLegBean createInterestRateLegBean(InterestRateLeg swapLeg) {
        final SwapLegBean bean = createSwapLegBean(swapLeg);
        return bean;
      }

      @Override
      public SwapLegBean visitFixedInterestRateLeg(FixedInterestRateLeg swapLeg) {
        final SwapLegBean bean = createInterestRateLegBean(swapLeg);
        bean.setRate(swapLeg.getRate());
        return bean;
      }

      @Override
      public SwapLegBean visitFloatingInterestRateLeg(FloatingInterestRateLeg swapLeg) {
        final SwapLegBean bean = createInterestRateLegBean(swapLeg);
        bean.setRate(swapLeg.getInitialFloatingRate());
        bean.setRateIdentifier(identifierToIdentifierBean(swapLeg.getFloatingReferenceRateIdentifier().toIdentifier()));
        bean.setSpread(swapLeg.getSpread());
        return bean;
      }
    });
  }

  public static SwapLeg createSwapLeg(final SwapLegBean bean) {
    return bean.getSwapLegType().accept(new SwapLegVisitor<SwapLeg>() {

      @Override
      public SwapLeg visitFixedInterestRateLeg(FixedInterestRateLeg ignore) {
        return new FixedInterestRateLeg(dayCountBeanToDayCount(bean.getDayCount()), frequencyBeanToFrequency(bean.getFrequency()), identifierBeanToIdentifier(bean.getRegion()),
            businessDayConventionBeanToBusinessDayConvention(bean.getBusinessDayConvention()), NotionalBeanOperation.createNotional(bean.getNotional()), bean.getRate());
      }

      @Override
      public SwapLeg visitFloatingInterestRateLeg(FloatingInterestRateLeg ignore) {
        return new FloatingInterestRateLeg(dayCountBeanToDayCount(bean.getDayCount()), frequencyBeanToFrequency(bean.getFrequency()), identifierBeanToIdentifier(bean.getRegion()),
            businessDayConventionBeanToBusinessDayConvention(bean.getBusinessDayConvention()), NotionalBeanOperation.createNotional(bean.getNotional()), identifierBeanToIdentifier(
                bean.getRateIdentifier()).toUniqueIdentifier(), bean.getRate(), bean.getSpread());
      }
    });
  }

}
