/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.payments;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.InterestRateDerivativeVisitor;

/**
 * 
 */
public class ForwardLiborPayment implements Payment {

  private final double _paymentTime;
  private final double _paymentYearFraction;
  private final double _forwardYearFraction;
  private final double _liborFixingTime;
  private final double _liborMaturityTime;
  private final double _spread;
  private final double _notional;
  private final String _fundingCurveName;
  private final String _liborCurveName;

  public ForwardLiborPayment(double paymentTime, double liborFixingTime, double liborMaturityTime, double paymentYearFraction, double forwardYearFraction, String fundingCurve, String liborCurve) {
    this(paymentTime, 1.0, liborFixingTime, liborMaturityTime, paymentYearFraction, forwardYearFraction, 0.0, fundingCurve, liborCurve);
  }

  public ForwardLiborPayment(double paymentTime, double notional, double liborFixingTime, double liborMaturityTime, double paymentYearFraction, double forwardYearFraction, double spread,
      String fundingCurve, String liborCurve) {

    Validate.isTrue(paymentTime >= 0.0, "payment time < 0");
    Validate.isTrue(liborFixingTime <= paymentTime, "libor Fixing > payement time");
    Validate.isTrue(liborFixingTime >= 0.0, "libor Fixing < 0");
    Validate.isTrue(liborMaturityTime > liborFixingTime, "libor maturity < fixing time");
    Validate.isTrue(paymentYearFraction > 0, "payment year fraction <=0");
    Validate.isTrue(forwardYearFraction > 0, "forward year fraction <=0");
    Validate.notNull(fundingCurve);
    Validate.notNull(liborCurve);

    double actActYearFrac = liborMaturityTime - liborFixingTime;
    // TODO try to catch a wrongly entered year fraction with very lose bounds around the ACT/ACT value (i.e. maturity - fixing). Needs more thought as to whether this should be tightened or removed
    Validate.isTrue(paymentYearFraction < 1.1 * actActYearFrac + 0.1 && paymentYearFraction > 0.9 * actActYearFrac - 0.1, "input payment year fraction is " + paymentYearFraction
        + " but ACT/ACT value is " + actActYearFrac);
    Validate.isTrue(forwardYearFraction < 1.1 * actActYearFrac + 0.1 && forwardYearFraction > 0.9 * actActYearFrac - 0.1, "input forward year fraction is " + forwardYearFraction
        + " but ACT/ACT value is " + actActYearFrac);
    _paymentTime = paymentTime;
    _liborFixingTime = liborFixingTime;
    _liborMaturityTime = liborMaturityTime;
    _paymentYearFraction = paymentYearFraction;
    _forwardYearFraction = forwardYearFraction;
    _spread = spread;
    _notional = notional;
    _fundingCurveName = fundingCurve;
    _liborCurveName = liborCurve;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(_forwardYearFraction);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((_fundingCurveName == null) ? 0 : _fundingCurveName.hashCode());
    result = prime * result + ((_liborCurveName == null) ? 0 : _liborCurveName.hashCode());
    temp = Double.doubleToLongBits(_liborFixingTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_liborMaturityTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_notional);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_paymentTime);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_paymentYearFraction);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_spread);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ForwardLiborPayment other = (ForwardLiborPayment) obj;
    if (Double.doubleToLongBits(_forwardYearFraction) != Double.doubleToLongBits(other._forwardYearFraction)) {
      return false;
    }
    if (_fundingCurveName == null) {
      if (other._fundingCurveName != null) {
        return false;
      }
    } else if (!_fundingCurveName.equals(other._fundingCurveName)) {
      return false;
    }
    if (_liborCurveName == null) {
      if (other._liborCurveName != null) {
        return false;
      }
    } else if (!_liborCurveName.equals(other._liborCurveName)) {
      return false;
    }
    if (Double.doubleToLongBits(_liborFixingTime) != Double.doubleToLongBits(other._liborFixingTime)) {
      return false;
    }
    if (Double.doubleToLongBits(_liborMaturityTime) != Double.doubleToLongBits(other._liborMaturityTime)) {
      return false;
    }
    if (Double.doubleToLongBits(_notional) != Double.doubleToLongBits(other._notional)) {
      return false;
    }
    if (Double.doubleToLongBits(_paymentTime) != Double.doubleToLongBits(other._paymentTime)) {
      return false;
    }
    if (Double.doubleToLongBits(_paymentYearFraction) != Double.doubleToLongBits(other._paymentYearFraction)) {
      return false;
    }
    if (Double.doubleToLongBits(_spread) != Double.doubleToLongBits(other._spread)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the year fraction used to calculated the payment amount, i.e payment = paymentYearFraction*(liborRate + spread).
   * @return the yearFraction
   */
  public double getPaymentYearFraction() {
    return _paymentYearFraction;
  }

  /**
   * Gets the year fraction used to calculate the forward libor rate, i.e. forward = (p(fixing)/p(maturity)-1)/forwardYearFraction where p(fixing) 
   * and p(maturity) are the discount factors at fixing and maturity
   * @return the forwardYearFraction
   */
  public double getForwardYearFraction() {
    return _forwardYearFraction;
  }

  /**
   * Gets the liborFixingTime field.
   * @return the liborFixingTime
   */
  public double getLiborFixingTime() {
    return _liborFixingTime;
  }

  /**
   * Gets the liborMaturityTime field.
   * @return the liborMaturityTime
   */
  public double getLiborMaturityTime() {
    return _liborMaturityTime;
  }

  /**
   * Gets the spread field.
   * @return the spread
   */
  public double getSpread() {
    return _spread;
  }

  public double getNotional() {
    return _notional;
  }

  public ForwardLiborPayment withZeroSpread() {
    if (getSpread() == 0.0) {
      return this;
    } else {
      return withRate(0.0);
    }
  }

  @Override
  public ForwardLiborPayment withRate(double spread) {
    return new ForwardLiborPayment(getPaymentTime(), getNotional(), getLiborFixingTime(), getLiborMaturityTime(), getPaymentYearFraction(), getForwardYearFraction(), spread, getFundingCurveName(),
        getLiborCurveName());
  }

  public FixedCouponPayment withUnitCoupon() {
    return new FixedCouponPayment(getPaymentTime(), getNotional(), getPaymentYearFraction(), 1.0, getFundingCurveName());
  }

  @Override
  public String getFundingCurveName() {
    return _fundingCurveName;
  }

  public String getLiborCurveName() {
    return _liborCurveName;
  }

  @Override
  public double getPaymentTime() {
    return _paymentTime;
  }

  @Override
  public <S, T> T accept(InterestRateDerivativeVisitor<S, T> visitor, S data) {
    return visitor.visitForwardLiborPayment(this, data);
  }

}
