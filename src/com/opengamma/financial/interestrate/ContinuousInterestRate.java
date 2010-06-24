/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import org.apache.commons.lang.Validate;

/**
 * 
 */
public class ContinuousInterestRate extends InterestRate {
  private final double _annualEquivalent;

  public ContinuousInterestRate(final double rate) {
    super(rate);
    _annualEquivalent = Math.exp(getRate()) - 1;
  }

  @Override
  public InterestRate fromAnnual(final AnnualInterestRate annual) {
    Validate.notNull(annual);
    return new ContinuousInterestRate(Math.log(1 + annual.getRate()));
  }

  @Override
  public InterestRate fromContinuous(final ContinuousInterestRate continuous) {
    Validate.notNull(continuous);
    return new ContinuousInterestRate(continuous.getRate());
  }

  @Override
  public InterestRate fromPeriodic(final PeriodicInterestRate periodic) {
    Validate.notNull(periodic);
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double getDiscountFactor(final double t) {
    return Math.exp(-getRate() * t);
  }

  @Override
  public AnnualInterestRate toAnnual() {
    return new AnnualInterestRate(_annualEquivalent);
  }

  @Override
  public ContinuousInterestRate toContinuous() {
    return new ContinuousInterestRate(getRate());
  }

  @Override
  public PeriodicInterestRate toPeriodic(final int periodsPerYear) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return "Continuous[r = " + getRate() + "]";
  }
}
