/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import org.apache.commons.lang.Validate;

import com.opengamma.util.time.Expiry;

/**
 * 
 * Definition for a log option. The exercise style is European.
 * <p>
 * When the spot price is <i>S</i>, an option with strike <i>K</i> has payoff
 * <i>max(0, ln(S / K))</i> for a call and <i>max(0, ln(K / S))</i> for a put.
 * 
 */
public class LogOptionDefinition extends OptionDefinition {
  private final OptionPayoffFunction<StandardOptionDataBundle> _payoffFunction = new OptionPayoffFunction<StandardOptionDataBundle>() {

    @Override
    public double getPayoff(final StandardOptionDataBundle data, final Double optionPrice) {
      Validate.notNull(data);
      final double spot = data.getSpot();
      return Math.max(0, Math.log(spot / getStrike()));
    }
  };
  private final OptionExerciseFunction<StandardOptionDataBundle> _exerciseFunction = new EuropeanExerciseFunction<StandardOptionDataBundle>();

  public LogOptionDefinition(final double strike, final Expiry expiry) {
    super(strike, expiry, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public OptionExerciseFunction<StandardOptionDataBundle> getExerciseFunction() {
    return _exerciseFunction;
  }

  @SuppressWarnings("unchecked")
  @Override
  public OptionPayoffFunction<StandardOptionDataBundle> getPayoffFunction() {
    return _payoffFunction;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (getClass() != obj.getClass()) {
      return false;
    }
    return super.equals(obj);
  }
}
