/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.fudge;

import com.opengamma.util.timeseries.DateTimeConverter;
import com.opengamma.util.timeseries.fast.FastTimeSeries;
import com.opengamma.util.timeseries.fast.longint.FastLongDoubleTimeSeries;
import com.opengamma.util.timeseries.yearoffset.ArrayYearOffsetDoubleTimeSeries;

/**
 * Fudge message encoder/decoder (builder) for ArrayYearOffsetDoubleTimeSeries
 */
public class ArrayYearOffsetDoubleTimeSeriesBuilder extends FastBackedDoubleTimeSeriesBuilder<Double, ArrayYearOffsetDoubleTimeSeries> {
  @Override
  public ArrayYearOffsetDoubleTimeSeries makeSeries(DateTimeConverter<Double> converter, FastTimeSeries<?> dts) {
    return new ArrayYearOffsetDoubleTimeSeries(converter, (FastLongDoubleTimeSeries) dts);
  }
}
