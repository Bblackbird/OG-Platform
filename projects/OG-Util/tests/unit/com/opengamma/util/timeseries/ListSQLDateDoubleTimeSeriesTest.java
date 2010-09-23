/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries;


import java.sql.Date;
import java.util.List;

import com.opengamma.util.timeseries.sqldate.ListSQLDateDoubleTimeSeries;
import com.opengamma.util.timeseries.sqldate.SQLDateDoubleTimeSeries;

public class ListSQLDateDoubleTimeSeriesTest extends SQLDateDoubleTimeSeriesTest {

  @Override
  public SQLDateDoubleTimeSeries createEmptyTimeSeries() {
    return new ListSQLDateDoubleTimeSeries();
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(Date[] times, double[] values) {
    return new ListSQLDateDoubleTimeSeries(times, values);
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(List<Date> times, List<Double> values) {
    return new ListSQLDateDoubleTimeSeries(times, values);
  }

  @Override
  public SQLDateDoubleTimeSeries createTimeSeries(DoubleTimeSeries<Date> dts) {
    return new ListSQLDateDoubleTimeSeries(dts);
  }

}
