/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries.sampling;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;

import org.junit.Test;

import com.opengamma.financial.analytics.timeseries.DailyScheduleCalculator;
import com.opengamma.financial.analytics.timeseries.WeeklyScheduleOnDayCalculator;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.util.timeseries.localdate.ArrayLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;

/**
 * 
 */
public class PreviousAndFirstValuePaddingTimeSeriesSamplingFunctionTest {
  //TODO test for start date = holiday
  private static final LocalDate START = LocalDate.of(2009, 1, 1);
  private static final LocalDate END = LocalDate.of(2010, 10, 1);
  private static final DailyScheduleCalculator DAILY = new DailyScheduleCalculator();
  private static final WeeklyScheduleOnDayCalculator WEEKLY_MONDAY = new WeeklyScheduleOnDayCalculator(DayOfWeek.MONDAY);
  private static final PreviousAndFirstValuePaddingTimeSeriesSamplingFunction F = new PreviousAndFirstValuePaddingTimeSeriesSamplingFunction();
  private static final Calendar WEEKEND_CALENDAR = new Calendar() {

    @Override
    public boolean isWorkingDay(final LocalDate date) {
      return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    @Override
    public String getConventionName() {
      return null;
    }

  };
  private static final LocalDate[] DAILY_SCHEDULE = DAILY.getScheduleWorkingDaysOnly(START, END, true, WEEKEND_CALENDAR);
  private static final LocalDate[] MONDAY_SCHEDULE = WEEKLY_MONDAY.getScheduleWorkingDaysOnly(START, END, true, WEEKEND_CALENDAR);
  private static final LocalDate MISSING_DAY_MONDAY_1 = LocalDate.of(2009, 2, 9);
  private static final LocalDate MISSING_DAY_MONDAY_2 = LocalDate.of(2009, 2, 16);
  private static final LocalDateDoubleTimeSeries TS_NO_MISSING_DATA;
  private static final LocalDateDoubleTimeSeries TS_TWO_MISSING_DATA_POINTS;

  static {
    final List<LocalDate> t1 = new ArrayList<LocalDate>();
    final List<Double> d1 = new ArrayList<Double>();
    final List<LocalDate> t2 = new ArrayList<LocalDate>();
    final List<Double> d2 = new ArrayList<Double>();
    for (int i = 0; i < DAILY_SCHEDULE.length; i++) {
      t1.add(DAILY_SCHEDULE[i]);
      d1.add(Double.valueOf(i));
      if (!(DAILY_SCHEDULE[i].equals(MISSING_DAY_MONDAY_1) || DAILY_SCHEDULE[i].equals(MISSING_DAY_MONDAY_2))) {
        t2.add(DAILY_SCHEDULE[i]);
        d2.add(Double.valueOf(i));
      }
    }
    TS_NO_MISSING_DATA = new ArrayLocalDateDoubleTimeSeries(t1, d1);
    TS_TWO_MISSING_DATA_POINTS = new ArrayLocalDateDoubleTimeSeries(t2, d2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullTS() {
    F.getSampledTimeSeries(null, DAILY_SCHEDULE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSchedule() {
    F.getSampledTimeSeries(TS_NO_MISSING_DATA, null);
  }

  @Test
  public void testMissingFirstData() {
    final LocalDate start = START.minusDays(21);
    final LocalDate[] daily = DAILY.getScheduleWorkingDaysOnly(start, END, true, WEEKEND_CALENDAR);
    final List<LocalDate> t = new ArrayList<LocalDate>();
    final List<Double> d = new ArrayList<Double>();
    for (int i = 0; i < daily.length; i++) {
      if (daily[i].isAfter(START)) {
        t.add(daily[i]);
        d.add(Double.valueOf(i));
      }
    }
    final LocalDateDoubleTimeSeries ts = new ArrayLocalDateDoubleTimeSeries(t, d);
    final LocalDateDoubleTimeSeries result = F.getSampledTimeSeries(ts, daily).toLocalDateDoubleTimeSeries();
    assertEquals(result.size(), daily.length);
    final int offset = 16;
    int i = 0;
    for (final Entry<LocalDate, Double> entry : result) {
      assertEquals(entry.getKey(), daily[i]);
      if (i < 16) {
        assertEquals(entry.getValue(), offset, 0);
      } else {
        assertEquals(entry.getValue(), i, 0);
      }
      i++;
    }
  }

  @Test
  public void testNoMissingDataDaily() {
    final LocalDateDoubleTimeSeries result = F.getSampledTimeSeries(TS_NO_MISSING_DATA, DAILY_SCHEDULE).toLocalDateDoubleTimeSeries();
    assertEquals(result.size(), DAILY_SCHEDULE.length);
    int i = 0;
    for (final Entry<LocalDate, Double> entry : result) {
      assertEquals(entry.getKey(), DAILY_SCHEDULE[i]);
      assertEquals(entry.getValue(), i++, 0);
    }
  }

  @Test
  public void testMissingDataWeekly() {
    final LocalDateDoubleTimeSeries result = F.getSampledTimeSeries(TS_TWO_MISSING_DATA_POINTS, MONDAY_SCHEDULE).toLocalDateDoubleTimeSeries();
    assertEquals(result.size(), MONDAY_SCHEDULE.length);
    int i = 0;
    int j = 2;
    for (final Entry<LocalDate, Double> entry : result) {
      assertEquals(entry.getKey(), MONDAY_SCHEDULE[i++]);
      if (entry.getKey().equals(MISSING_DAY_MONDAY_1) || entry.getKey().equals(MISSING_DAY_MONDAY_2)) {
        assertEquals(entry.getValue(), j - 1, 0);
      } else {
        assertEquals(entry.getValue(), j, 0);
      }
      j += 5;
    }
  }

  @Test
  public void testDaysMissingDataDaily() {
    final LocalDateDoubleTimeSeries result = F.getSampledTimeSeries(TS_TWO_MISSING_DATA_POINTS, DAILY_SCHEDULE).toLocalDateDoubleTimeSeries();
    assertEquals(result.size(), DAILY_SCHEDULE.length);
    int i = 0;
    for (final Entry<LocalDate, Double> entry : result) {
      assertEquals(entry.getKey(), DAILY_SCHEDULE[i]);
      if (entry.getKey().equals(MISSING_DAY_MONDAY_1) || entry.getKey().equals(MISSING_DAY_MONDAY_2)) {
        assertEquals(entry.getValue(), i - 1, 0);
      } else {
        assertEquals(entry.getValue(), i, 0);
      }
      i++;
    }
  }

}
