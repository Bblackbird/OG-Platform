/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db;


/**
 * Custom Hibernate usertype for the FutureType enum
 * 
 * @author andrew
 */
public class FutureTypeUserType extends EnumUserType<FutureType> {
  
  private static final String AGRICULTURE_FUTURE_TYPE = "Agriculture";
  private static final String BOND_FUTURE_TYPE = "Bond";
  private static final String CURRENCY_FUTURE_TYPE = "Currency";
  private static final String ENERGY_FUTURE_TYPE = "Energy";
  private static final String INTEREST_RATE_FUTURE_TYPE = "Interest Rate";
  private static final String METAL_FUTURE_TYPE = "Metal";

  public FutureTypeUserType () {
    super (FutureType.class, FutureType.values ());
  }

  @Override
  protected String enumToStringNoCache(FutureType value) {
    return value.accept (new FutureType.Visitor<String> () {

      @Override
      public String visitBondFutureType() {
        return BOND_FUTURE_TYPE;
      }

      @Override
      public String visitCurrencyFutureType() {
        return CURRENCY_FUTURE_TYPE;
      }

      @Override
      public String visitInterestRateFutureType() {
        return INTEREST_RATE_FUTURE_TYPE;
      }

      @Override
      public String visitAgricultureFutureType() {
        return AGRICULTURE_FUTURE_TYPE;
      }

      @Override
      public String visitEnergyFutureType() {
        return ENERGY_FUTURE_TYPE;
      }

      @Override
      public String visitMetalFutureType() {
        return METAL_FUTURE_TYPE;
      }

    });
  }
  
}