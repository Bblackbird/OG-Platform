/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.interestrate;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.model.interestrate.definition.VasicekDataBundle;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class VasicekInterestRateModel implements DiscountBondModel<VasicekDataBundle> {

  @Override
  public Function1D<VasicekDataBundle, Double> getDiscountBondFunction(final ZonedDateTime time, final ZonedDateTime maturity) {
    Validate.notNull(time);
    Validate.notNull(maturity);
    return new Function1D<VasicekDataBundle, Double>() {

      @Override
      public Double evaluate(final VasicekDataBundle data) {
        Validate.notNull(data);
        final double lt = data.getLongTermInterestRate();
        final double speed = data.getReversionSpeed();
        final double dt = DateUtil.getDifferenceInYears(time, maturity);
        final double t = DateUtil.getDifferenceInYears(data.getDate(), time);
        final double sigma = data.getShortRateVolatility(t);
        final double r = data.getShortRate(t);
        final double sigmaSq = sigma * sigma;
        final double speedSq = speed * speed;
        final double rInfinity = lt - 0.5 * sigmaSq / speedSq;
        final double factor = 1 - Math.exp(-speed * dt);
        final double a = rInfinity * (factor / speed - dt) - sigmaSq * factor * factor / (4 * speedSq * speed);
        final double b = factor / speed;
        return Math.exp(a - r * b);
      }

    };
  }
}
