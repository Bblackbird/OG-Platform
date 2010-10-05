/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.businessday;

import javax.time.calendar.LocalDate;

import com.opengamma.financial.convention.calendar.Calendar;

/**
 * The modified preceding business day convention
 * <p>
 * This chooses the previous working day before a non-working day, unless than date is in a different month. 
 * In that case, the date is adjusted to be the following business day. 
 * <p>
 */
public class ModifiedPrecedingBusinessDayConvention extends BusinessDayConvention {
  private static final BusinessDayConvention PRECEDING = new PrecedingBusinessDayConvention();
  private static final BusinessDayConvention FOLLOWING = new FollowingBusinessDayConvention();

  @Override
  public LocalDate adjustDate(final Calendar workingDayCalendar, final LocalDate date) {
    final LocalDate precedingDate = PRECEDING.adjustDate(workingDayCalendar, date);
    if (precedingDate.getMonthOfYear() == date.getMonthOfYear()) {
      return precedingDate;
    }
    return FOLLOWING.adjustDate(workingDayCalendar, date);
  }

  @Override
  public String getConventionName() {
    return "Modified Preceding";
  }

}
