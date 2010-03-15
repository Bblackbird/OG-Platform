 package com.opengamma.util.timeseries;


import java.util.Date;
import java.util.List;

import com.opengamma.util.timeseries.date.time.ArrayDateTimeDoubleTimeSeries;
import com.opengamma.util.timeseries.date.time.DateTimeDoubleTimeSeries;

public class ArrayDateTimeDoubleTimeSeriesTest extends DateDoubleTimeSeriesTest {

  @Override
  public DateTimeDoubleTimeSeries createEmptyTimeSeries() {
    return new ArrayDateTimeDoubleTimeSeries();
  }

  @Override
  public DateTimeDoubleTimeSeries createTimeSeries(Date[] times, double[] values) {
    return new ArrayDateTimeDoubleTimeSeries(times, values);
  }

  @Override
  public DateTimeDoubleTimeSeries createTimeSeries(List<Date> times, List<Double> values) {
    return new ArrayDateTimeDoubleTimeSeries(times, values);
  }

  @Override
  public DateTimeDoubleTimeSeries createTimeSeries(DoubleTimeSeries<Date> dts) {
    return new ArrayDateTimeDoubleTimeSeries(dts);
  }
}
