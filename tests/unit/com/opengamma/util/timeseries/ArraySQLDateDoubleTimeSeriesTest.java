 package com.opengamma.util.timeseries;


import java.sql.Date;
import java.util.List;

import com.opengamma.util.timeseries.sqldate.ArraySQLDateDoubleTimeSeries;
import com.opengamma.util.timeseries.sqldate.SQLDateDoubleTimeSeries;

public class ArraySQLDateDoubleTimeSeriesTest extends SQLDateDoubleTimeSeriesTest {

  @Override
  public SQLDateDoubleTimeSeries createEmptyTimeSeries() {
    return new ArraySQLDateDoubleTimeSeries();
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(Date[] times, double[] values) {
    return new ArraySQLDateDoubleTimeSeries(times, values);
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(List<Date> times, List<Double> values) {
    return new ArraySQLDateDoubleTimeSeries(times, values);
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(DoubleTimeSeries<Date> dts) {
    return new ArraySQLDateDoubleTimeSeries(dts);
  }
}
