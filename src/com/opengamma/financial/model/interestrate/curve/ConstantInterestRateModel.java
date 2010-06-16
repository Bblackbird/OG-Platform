/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.interestrate.curve;

import com.opengamma.financial.model.interestrate.InterestRateModel;
import com.opengamma.util.ArgumentChecker;

/**
 * 
 */
public class ConstantInterestRateModel implements InterestRateModel<Double> {
  private final double _r;

  public ConstantInterestRateModel(final double r) {
    ArgumentChecker.notNegative(r, "rate");
    _r = r;
  }

  @Override
  public double getInterestRate(final Double t) {
    ArgumentChecker.notNegative(t, "time");
    return _r;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(_r);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ConstantInterestRateModel other = (ConstantInterestRateModel) obj;
    if (Double.doubleToLongBits(_r) != Double.doubleToLongBits(other._r)) {
      return false;
    }
    return true;
  }

}
