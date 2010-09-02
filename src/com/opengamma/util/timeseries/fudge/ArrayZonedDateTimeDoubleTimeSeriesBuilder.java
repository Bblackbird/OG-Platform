/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.fudge;

import javax.time.calendar.ZonedDateTime;

import org.fudgemsg.mapping.FudgeBuilderFor;

import com.opengamma.util.timeseries.DateTimeConverter;
import com.opengamma.util.timeseries.fast.FastTimeSeries;
import com.opengamma.util.timeseries.fast.longint.FastLongDoubleTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.ArrayZonedDateTimeDoubleTimeSeries;

/**
 * Fudge message encoder/decoder (builder) for ArrayZonedDateTimeDoubleTimeSeries
 */
@FudgeBuilderFor(ArrayZonedDateTimeDoubleTimeSeries.class)
public class ArrayZonedDateTimeDoubleTimeSeriesBuilder extends FastBackedDoubleTimeSeriesBuilder<ZonedDateTime, ArrayZonedDateTimeDoubleTimeSeries> {
  @Override
  public ArrayZonedDateTimeDoubleTimeSeries makeSeries(DateTimeConverter<ZonedDateTime> converter, FastTimeSeries<?> dts) {
    return new ArrayZonedDateTimeDoubleTimeSeries(converter, (FastLongDoubleTimeSeries) dts);
  }
}
