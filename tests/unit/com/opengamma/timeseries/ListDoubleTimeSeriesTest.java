package com.opengamma.timeseries;


import java.util.Date;
import java.util.List;

import com.opengamma.util.timeseries.date.DateDoubleTimeSeries;
import com.opengamma.util.timeseries.date.ListDateDoubleTimeSeries;

public class ListDoubleTimeSeriesTest extends DateDoubleTimeSeriesTest {

  @Override
  public DateDoubleTimeSeries createEmptyTimeSeries() {
    return ListDateDoubleTimeSeries.EMPTY_SERIES;
  }

  @Override
  public DateDoubleTimeSeries createTimeSeries(Date[] times, double[] values) {
    return new ListDateDoubleTimeSeries(times, values);
  }

  @Override
  public DateDoubleTimeSeries createTimeSeries(List<Date> times, List<Double> values) {
    return new ListDateDoubleTimeSeries(times, values);
  }

  @Override
  public DateDoubleTimeSeries createTimeSeries(DateDoubleTimeSeries dts) {
    return new ListDateDoubleTimeSeries(dts);
  }

}
