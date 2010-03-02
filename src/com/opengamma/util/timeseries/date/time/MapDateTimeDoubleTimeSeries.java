/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.date.time;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.opengamma.util.timeseries.DateTimeConverter;
import com.opengamma.util.timeseries.fast.DateTimeNumericEncoding;
import com.opengamma.util.timeseries.fast.longint.FastMapLongDoubleTimeSeries;
import com.opengamma.util.timeseries.fast.longint.FastMutableLongDoubleTimeSeries;

/**
 * @author jim
 * 
 */
public class MapDateTimeDoubleTimeSeries extends MutableDateTimeDoubleTimeSeries.Long {
  public static final MapDateTimeDoubleTimeSeries EMPTY_SERIES = new MapDateTimeDoubleTimeSeries();
  private static final DateTimeConverter<Date> s_converter = new DateEpochMillisConverter();

  private MapDateTimeDoubleTimeSeries() {
    super(new DateEpochMillisConverter(), new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS));
  }

  public MapDateTimeDoubleTimeSeries(final Date[] dates, final double[] values) {
    super(s_converter, new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS, s_converter.convertToLong(dates), values));
  }

  public MapDateTimeDoubleTimeSeries(final TimeZone timeZone, final Date[] dates, final double[] values) {
    super(new DateEpochMillisConverter(timeZone), new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS, new DateEpochMillisConverter(timeZone)
        .convertToLong(dates), values));
  }

  public MapDateTimeDoubleTimeSeries(final List<Date> dates, final List<Double> values) {
    super(s_converter, new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS, s_converter.convertToLong(dates), values));
  }

  public MapDateTimeDoubleTimeSeries(final TimeZone timeZone, final List<Date> dates, final List<Double> values) {
    super(new DateEpochMillisConverter(timeZone), new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS, new DateEpochMillisConverter(timeZone)
        .convertToLong(dates), values));
  }

  public MapDateTimeDoubleTimeSeries(final DateTimeDoubleTimeSeries dts) {
    super(s_converter, (FastMutableLongDoubleTimeSeries) s_converter.convertToLong(new FastMapLongDoubleTimeSeries(DateTimeNumericEncoding.TIME_EPOCH_MILLIS), dts));
  }

  public MapDateTimeDoubleTimeSeries(final TimeZone timeZone, final DateTimeDoubleTimeSeries dts) {
    super(new DateEpochMillisConverter(timeZone), (FastMutableLongDoubleTimeSeries) new DateEpochMillisConverter(timeZone).convertToLong(new FastMapLongDoubleTimeSeries(
        DateTimeNumericEncoding.TIME_EPOCH_MILLIS), dts));
  }

  public MapDateTimeDoubleTimeSeries(final FastMutableLongDoubleTimeSeries pmidts) {
    super(s_converter, pmidts);
  }

  public MapDateTimeDoubleTimeSeries(final TimeZone timeZone, final FastMutableLongDoubleTimeSeries pmidts) {
    super(new DateEpochMillisConverter(timeZone), pmidts);
  }

  @Override
  public DateTimeDoubleTimeSeries newInstanceFast(final Date[] dateTimes, final double[] values) {
    return new MapDateTimeDoubleTimeSeries(((DateEpochMillisConverter) getConverter()).getTimeZone(), dateTimes, values);
  }
}
