/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.daycount;

import javax.time.calendar.ZonedDateTime;

/**
 * The 1/1 day count convention.
 * <p>
 * The 1/1 day count always returns one as the fraction of a year.
 */
public class OneOneDayCount extends StatelessDayCount {

  @Override
  public double getBasis(final ZonedDateTime date) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double getDayCountFraction(final ZonedDateTime firstDate, final ZonedDateTime secondDate) {
    return 1;
  }

  @Override
  public String getConventionName() {
    return "1/1";
  }

}
