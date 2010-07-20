/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db;

import static com.opengamma.financial.security.db.Converters.currencyBeanToCurrency;
import static com.opengamma.financial.security.db.Converters.dateToExpiry;
import static com.opengamma.financial.security.db.Converters.identifierBeanToIdentifier;
import static com.opengamma.financial.security.db.Converters.identifierToIdentifierBean;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.financial.security.option.AmericanExerciseType;
import com.opengamma.financial.security.option.AsianExerciseType;
import com.opengamma.financial.security.option.AsymmetricPoweredPayoffStyle;
import com.opengamma.financial.security.option.BarrierPayoffStyle;
import com.opengamma.financial.security.option.BermudanExerciseType;
import com.opengamma.financial.security.option.BondOptionSecurity;
import com.opengamma.financial.security.option.CappedPoweredPayoffStyle;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.EuropeanExerciseType;
import com.opengamma.financial.security.option.ExerciseType;
import com.opengamma.financial.security.option.ExerciseTypeVisitor;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.FixedStrikePayoffStyle;
import com.opengamma.financial.security.option.FutureOptionSecurity;
import com.opengamma.financial.security.option.OptionOptionSecurity;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.financial.security.option.OptionSecurityVisitor;
import com.opengamma.financial.security.option.PayoffStyle;
import com.opengamma.financial.security.option.PayoffStyleVisitor;
import com.opengamma.financial.security.option.PoweredPayoffStyle;
import com.opengamma.financial.security.option.SwapOptionSecurity;
import com.opengamma.financial.security.option.VanillaPayoffStyle;

/**
 * Conversion operation for OptionSecurity to/from OptionSecurityBean 
 */
public final class OptionSecurityBeanOperation extends AbstractBeanOperation<OptionSecurity, OptionSecurityBean> {

  /**
   * Singleton instance.
   */
  public static final OptionSecurityBeanOperation INSTANCE = new OptionSecurityBeanOperation();

  private OptionSecurityBeanOperation() {
    super("OPTION", OptionSecurity.class, OptionSecurityBean.class);
  }

  @Override
  public OptionSecurity createSecurity(final OptionSecurityBean bean) {
    final ExerciseType exerciseType = bean.getOptionExerciseType().accept(new ExerciseTypeVisitor<ExerciseType>() {

      @Override
      public ExerciseType visitAmericanExerciseType(AmericanExerciseType exerciseType) {
        return new AmericanExerciseType();
      }

      @Override
      public ExerciseType visitAsianExerciseType(AsianExerciseType exerciseType) {
        return new AsianExerciseType();
      }

      @Override
      public ExerciseType visitBermudanExerciseType(BermudanExerciseType exerciseType) {
        return new BermudanExerciseType();
      }

      @Override
      public ExerciseType visitEuropeanExerciseType(EuropeanExerciseType exerciseType) {
        return new EuropeanExerciseType();
      }
    });
    final PayoffStyle payoffStyle = bean.getOptionPayoffStyle().accept(new PayoffStyleVisitor<PayoffStyle>() {

      @Override
      public PayoffStyle visitAsymmetricPoweredPayoffStyle(AsymmetricPoweredPayoffStyle payoffStyle) {
        return new AsymmetricPoweredPayoffStyle(bean.getPower());
      }

      @Override
      public PayoffStyle visitBarrierPayoffStyle(BarrierPayoffStyle payoffStyle) {
        return new BarrierPayoffStyle();
      }

      @Override
      public PayoffStyle visitCappedPoweredPayoffStyle(CappedPoweredPayoffStyle payoffStyle) {
        return new CappedPoweredPayoffStyle(bean.getPower(), bean.getCap());
      }

      @Override
      public PayoffStyle visitFixedStrikePayoffStyle(FixedStrikePayoffStyle payoffStyle) {
        return new FixedStrikePayoffStyle();
      }

      @Override
      public PayoffStyle visitPoweredPayoffStyle(PoweredPayoffStyle payoffStyle) {
        return new PoweredPayoffStyle(bean.getPower());
      }

      @Override
      public PayoffStyle visitVanillaPayoffStyle(VanillaPayoffStyle payoffStyle) {
        return new VanillaPayoffStyle();
      }
    });
    OptionSecurity sec = bean.getOptionSecurityType().accept(new OptionSecurityVisitor<OptionSecurity>() {

      @Override
      public OptionSecurity visitFXOptionSecurity(FXOptionSecurity security) {
        return new FXOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean
            .getUnderlying()), currencyBeanToCurrency(bean.getCurrency()), bean.getCounterparty(), currencyBeanToCurrency(bean.getPutCurrency()), currencyBeanToCurrency(bean.getCallCurrency()));
      }

      @Override
      public OptionSecurity visitBondOptionSecurity(BondOptionSecurity security) {
        return new BondOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean.getUnderlying()),
            currencyBeanToCurrency(bean.getCurrency()));
      }

      @Override
      public OptionSecurity visitEquityOptionSecurity(EquityOptionSecurity security) {
        return new EquityOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean.getUnderlying()),
            currencyBeanToCurrency(bean.getCurrency()), bean.getPointValue(), bean.getExchange().getName());
      }

      @Override
      public OptionSecurity visitFutureOptionSecurity(FutureOptionSecurity security) {
        return new FutureOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean.getUnderlying()),
            currencyBeanToCurrency(bean.getCurrency()), bean.getPointValue(), bean.getExchange().getName(), bean.isMargined());
      }

      @Override
      public OptionSecurity visitOptionOptionSecurity(OptionOptionSecurity security) {
        return new OptionOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean.getUnderlying()),
            currencyBeanToCurrency(bean.getCurrency()));
      }

      @Override
      public OptionSecurity visitSwapOptionSecurity(SwapOptionSecurity security) {
        return new SwapOptionSecurity(exerciseType, payoffStyle, bean.getOptionType(), bean.getStrike(), dateToExpiry(bean.getExpiry()), identifierBeanToIdentifier(bean.getUnderlying()),
            currencyBeanToCurrency(bean.getCurrency()));
      }
    });
    return sec;
  }

  @Override
  public boolean beanEquals(final OptionSecurityBean bean, final OptionSecurity security) {
    return security.accept(new OptionSecurityVisitor<Boolean>() {

      private Boolean beanEquals(final OptionSecurity security) {
        return ObjectUtils.equals(bean.getOptionExerciseType(), security.getExerciseType()) && ObjectUtils.equals(bean.getOptionPayoffStyle(), security.getPayoffStyle())
            && ObjectUtils.equals(bean.getOptionSecurityType(), OptionSecurityType.identify(security)) && ObjectUtils.equals(bean.getOptionType(), security.getOptionType())
            && ObjectUtils.equals(bean.getStrike(), security.getStrike()) && ObjectUtils.equals(dateToExpiry(bean.getExpiry()), security.getExpiry())
            && ObjectUtils.equals(bean.getUnderlying(), security.getUnderlyingIdentifier()) && ObjectUtils.equals(currencyBeanToCurrency(bean.getCurrency()), security.getCurrency());
      }

      @Override
      public Boolean visitFXOptionSecurity(FXOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return ObjectUtils.equals(bean.getCounterparty(), security.getCounterparty()) && ObjectUtils.equals(currencyBeanToCurrency(bean.getPutCurrency()), security.getPutCurrency())
            && ObjectUtils.equals(currencyBeanToCurrency(bean.getCallCurrency()), security.getCallCurrency());
      }

      @Override
      public Boolean visitBondOptionSecurity(BondOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return true;
      }

      @Override
      public Boolean visitEquityOptionSecurity(EquityOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return ObjectUtils.equals(bean.getPointValue(), security.getPointValue()) && ObjectUtils.equals(bean.getExchange(), security.getExchange());
      }

      @Override
      public Boolean visitFutureOptionSecurity(FutureOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return ObjectUtils.equals(bean.getPointValue(), security.getPointValue()) && ObjectUtils.equals(bean.getExchange(), security.getExchange())
            && ObjectUtils.equals(bean.isMargined(), security.getIsMargined());
      }

      @Override
      public Boolean visitOptionOptionSecurity(OptionOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return true;
      }

      @Override
      public Boolean visitSwapOptionSecurity(SwapOptionSecurity security) {
        if (!beanEquals(security)) {
          return false;
        }
        return true;
      }
    });
  }

  @Override
  public OptionSecurityBean createBean(final HibernateSecurityMasterDao secMasterSession, final OptionSecurity security) {
    return security.accept(new OptionSecurityVisitor<OptionSecurityBean>() {

      private OptionSecurityBean createSecurityBean(final OptionSecurity security) {
        final OptionSecurityBean bean = new OptionSecurityBean();
        bean.setOptionExerciseType(OptionExerciseType.identify(security.getExerciseType()));
        bean.setOptionPayoffStyle(security.getPayoffStyle().accept(new PayoffStyleVisitor<OptionPayoffStyle>() {

          @Override
          public OptionPayoffStyle visitAsymmetricPoweredPayoffStyle(AsymmetricPoweredPayoffStyle payoffStyle) {
            bean.setPower(payoffStyle.getPower());
            return OptionPayoffStyle.ASYMMETRIC_POWERED;
          }

          @Override
          public OptionPayoffStyle visitBarrierPayoffStyle(BarrierPayoffStyle payoffStyle) {
            return OptionPayoffStyle.BARRIER;
          }

          @Override
          public OptionPayoffStyle visitCappedPoweredPayoffStyle(CappedPoweredPayoffStyle payoffStyle) {
            bean.setPower(payoffStyle.getPower());
            bean.setPower(payoffStyle.getCap());
            return OptionPayoffStyle.CAPPED_POWERED;
          }

          @Override
          public OptionPayoffStyle visitFixedStrikePayoffStyle(FixedStrikePayoffStyle payoffStyle) {
            return OptionPayoffStyle.FIXED_STRIKE;
          }

          @Override
          public OptionPayoffStyle visitPoweredPayoffStyle(PoweredPayoffStyle payoffStyle) {
            bean.setPower(payoffStyle.getPower());
            return OptionPayoffStyle.POWERED;
          }

          @Override
          public OptionPayoffStyle visitVanillaPayoffStyle(VanillaPayoffStyle payoffStyle) {
            return OptionPayoffStyle.VANILLA;
          }
        }));
        bean.setOptionSecurityType(OptionSecurityType.identify(security));
        bean.setOptionType(security.getOptionType());
        bean.setStrike(security.getStrike());
        bean.setExpiry(new Date(security.getExpiry().toInstant().toEpochMillisLong()));
        bean.setUnderlying(identifierToIdentifierBean(security.getUnderlyingIdentifier()));
        bean.setCurrency(secMasterSession.getOrCreateCurrencyBean(security.getCurrency().getISOCode()));
        return bean;
      }

      @Override
      public OptionSecurityBean visitFXOptionSecurity(FXOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        bean.setCounterparty(security.getCounterparty());
        bean.setPutCurrency(secMasterSession.getOrCreateCurrencyBean(security.getPutCurrency().getISOCode()));
        bean.setCallCurrency(secMasterSession.getOrCreateCurrencyBean(security.getCallCurrency().getISOCode()));
        return bean;
      }

      @Override
      public OptionSecurityBean visitBondOptionSecurity(BondOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        return bean;
      }

      @Override
      public OptionSecurityBean visitEquityOptionSecurity(EquityOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        bean.setExchange(secMasterSession.getOrCreateExchangeBean(security.getExchange(), ""));
        bean.setPointValue(security.getPointValue());
        return bean;
      }

      @Override
      public OptionSecurityBean visitFutureOptionSecurity(FutureOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        bean.setExchange(secMasterSession.getOrCreateExchangeBean(security.getExchange(), ""));
        bean.setPointValue(security.getPointValue());
        bean.setMargined(security.getIsMargined());
        return bean;
      }

      @Override
      public OptionSecurityBean visitOptionOptionSecurity(OptionOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        return bean;
      }

      @Override
      public OptionSecurityBean visitSwapOptionSecurity(SwapOptionSecurity security) {
        final OptionSecurityBean bean = createSecurityBean(security);
        return bean;
      }

    });
  }

}
