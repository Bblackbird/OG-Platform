/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.util.CompareUtils;

@Ignore
public abstract class DoubleTimeSeriesTest<E> {
  
  @SuppressWarnings("unused")
  private static final Logger s_logger = LoggerFactory.getLogger(DoubleTimeSeriesTest.class);
   
  public abstract DoubleTimeSeries<E> createEmptyTimeSeries();
  public abstract DoubleTimeSeries<E> createTimeSeries(E[] times, double[] values);
  public abstract DoubleTimeSeries<E> createTimeSeries(List<E> times, List<Double> values);
  public abstract DoubleTimeSeries<E> createTimeSeries(DoubleTimeSeries<E> dts);

  public abstract E[] emptyTimes();
  public abstract E[] testTimes();
  public abstract E[] testTimes2();
  
  @Test
  public void testArrayConstructor() {
    DoubleTimeSeries<E> dts = createTimeSeries(emptyTimes(), new double[0]);
    assertEquals(0, dts.size());
    E[] times = testTimes();
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    dts = createTimeSeries(times, values);
    assertEquals(6, dts.size());
    Iterator<Double> valuesIter = dts.valuesIterator();
    for (double i=1.0; i<=6.0; i+=1.0) {
      assertTrue(CompareUtils.closeEquals(i, valuesIter.next()));
    }
  }
  
  @Test
  public void testListConstructor() {
    DoubleTimeSeries<E> dts = createTimeSeries(new ArrayList<E>(), new ArrayList<Double>());
    assertEquals(0, dts.size());
    E[] times = testTimes();
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    List<E> timesList = new ArrayList<E>();
    List<Double> valuesList = new ArrayList<Double>();
    for (int i=0; i<times.length; i++) {
      timesList.add(times[i]);
      valuesList.add(values[i]);
    }
    dts = createTimeSeries(timesList, valuesList);
    assertEquals(6, dts.size());
    Iterator<Double> valuesIter = dts.valuesIterator();
    for (double i=1.0; i<=6.0; i+=1.0) {
      assertTrue(CompareUtils.closeEquals(i, valuesIter.next()));
    }
  }
  
  @Test
  public void testTimeSeriesConstructor() {
    DoubleTimeSeries<E> dts = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts2 = createTimeSeries(dts);
    assertEquals(0, dts2.size());
    E[] times = testTimes();
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    dts = createTimeSeries(times, values);
    dts2 = createTimeSeries(dts);
    assertEquals(6, dts2.size());
    Iterator<Double> valuesIter = dts2.valuesIterator();
    for (double i=1.0; i<=6.0; i+=1.0) {
      assertTrue(CompareUtils.closeEquals(i, valuesIter.next()));
    }    
  }
  
  public DoubleTimeSeries<E> createStandardTimeSeries() {
    E[] times = testTimes();
    double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
    return createTimeSeries(times, values);
  }
  
  public DoubleTimeSeries<E> createStandardTimeSeries2() {
    E[] times = testTimes2();
    double[] values = {4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
    return createTimeSeries(times, values);
  }
  
  @Test
  public void testHead() {
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    DoubleTimeSeries<E> head5 = (DoubleTimeSeries<E>) dts.head(5);
    Iterator<Entry<E, Double>> iterator = head5.iterator();
    for (int i=0; i<5; i++) {
      Entry<E, Double> entry = iterator.next();
      assertEquals(testTimes()[i], entry.getKey());
      assertEquals(Double.valueOf(i+1), entry.getValue());
    }
    assertEquals(dts.head(0), createEmptyTimeSeries());
    assertEquals(createEmptyTimeSeries().head(0), createEmptyTimeSeries());
  }
  
  @Test
  public void testTail() {
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    DoubleTimeSeries<E> tail5 = (DoubleTimeSeries<E>) dts.tail(5);
    Iterator<Entry<E, Double>> iterator = tail5.iterator();
    for (int i=1; i<6; i++) {
      Entry<E, Double> entry = iterator.next();
      assertEquals(testTimes()[i], entry.getKey());
      assertEquals(Double.valueOf(i+1), entry.getValue());
    }
    assertEquals(dts.tail(0), createEmptyTimeSeries());
    assertEquals(createEmptyTimeSeries().tail(0), createEmptyTimeSeries());
  }
  
  @Test
  public void testSize() {
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    assertEquals(6, dts.size());
    DoubleTimeSeries<E> emptyTS = createEmptyTimeSeries();
    assertEquals(0, emptyTS.size());
  }

  @Test
  public void testIsEmpty() {
    DoubleTimeSeries<E> empty = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    assertTrue(empty.isEmpty());
    assertFalse(dts.isEmpty());
  }

  @Test
  public void testGetLatestInstant() {
    DoubleTimeSeries<E> empty = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    E[] testDates = testTimes();
    assertEquals(testDates[5], dts.getLatestTime());
    try {
      empty.getLatestTime();
    } catch (NoSuchElementException nsee) {
      return;
    }
    fail();
  }

  @Test
  public void testGetLatestValue() {
    DoubleTimeSeries<E> empty = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    assertTrue(CompareUtils.closeEquals(6.0d, dts.getLatestValue()));
    try {
      empty.getLatestValue();
    } catch (NoSuchElementException nsee) {
      return;
    }
    fail();
  }

  @Test
  public void testGetEarliestInstant() {
    DoubleTimeSeries<E> empty = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    E[] testDates = testTimes();
    assertEquals(testDates[0], dts.getEarliestTime());
    try {
      empty.getEarliestTime();
    } catch (NoSuchElementException nsee) {
      return;
    }
    fail();    
  }

  @Test
  public void testGetEarliestValue() {
    DoubleTimeSeries<E> empty = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    assertTrue(CompareUtils.closeEquals(1d, dts.getEarliestValue()));
    try {
      empty.getEarliestValue();
    } catch (NoSuchElementException nsee) {
      return;
    }
    fail();
  }

  @Test
  public void testValuesIterator() {
    Iterator<Double> emptyValuesIter = createEmptyTimeSeries().valuesIterator();
    Iterator<Double> dtsValuesIter = createStandardTimeSeries().valuesIterator();
    for (double i=1; i<=6.0; i+=1.0d) {
      assertTrue(dtsValuesIter.hasNext());
      Double val = dtsValuesIter.next();
      CompareUtils.closeEquals(val, i);
    }
    try {
      dtsValuesIter.next();
    } catch (NoSuchElementException nsee) {
      assertFalse(emptyValuesIter.hasNext());
      try {
        emptyValuesIter.next();
      } catch (NoSuchElementException nsuchee) {
        return;
      }      
    }
    fail();
  }

  @Test
  public void testTimeIterator() {    
    Iterator<E> emptyTimesIter = createEmptyTimeSeries().timeIterator();
    Iterator<E> dtsTimesIter = createStandardTimeSeries().timeIterator();
    E[] testDates = testTimes();
    for (int i=0; i<6; i++) {
      assertTrue(dtsTimesIter.hasNext());
      E time = dtsTimesIter.next();
      assertEquals(testDates[i], time);
    }
    try {
      dtsTimesIter.next();
    } catch (NoSuchElementException nsee) {
      assertFalse(emptyTimesIter.hasNext());
      try {
        emptyTimesIter.next();
      } catch (NoSuchElementException nsuchee) {
        return;
      }      
    }
    fail();
  }

  @Test
  public void testIterator() {
    Iterator<Entry<E, Double>> emptyIter = createEmptyTimeSeries().iterator();
    Iterator<Entry<E, Double>> dtsIter = createStandardTimeSeries().iterator();
    E[] testDates = testTimes();
    for (int i=0; i<6; i++) {
      assertTrue(dtsIter.hasNext());
      Entry<E, Double> entry = dtsIter.next();
      E time = entry.getKey();
      CompareUtils.closeEquals(entry.getValue(), (double)i+1);
      assertEquals(testDates[i], time);
    }
    try {
      dtsIter.next();
    } catch (NoSuchElementException nsee) {
      assertFalse(emptyIter.hasNext());
      try {
        emptyIter.next();
      } catch (NoSuchElementException nsuchee) {
        return;
      }      
    }
    fail();
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetDataPoint() {
    DoubleTimeSeries<E> emptyTS = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    E[] testDates = testTimes();
    for (int i=0; i<6; i++) {
      Double val = dts.getValue(testDates[i]);
      CompareUtils.closeEquals(val, i+1);
      val = dts.getValueAt(i);
      CompareUtils.closeEquals(val, i+1);
    }
    emptyTS.getValueAt(0);
  }

  @SuppressWarnings("cast")
  @Test
  public void testSubSeriesInstantProviderInstantProvider() {
    DoubleTimeSeries<E> emptyTS = createEmptyTimeSeries();
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    E[] testDates = testTimes();
    DoubleTimeSeries<E> threeToFive = dts.subSeries(testDates[3], testDates[5]);
    assertEquals(2, threeToFive.size());
    Iterator<Entry<E, Double>> iterator = threeToFive.iterator();
    for (int i=3; i<5; i++) {
      Entry<E, Double> item = iterator.next();
      assertEquals(testDates[i], item.getKey());
      assertTrue(CompareUtils.closeEquals((double)i+1, item.getValue()));
    }
    assertEquals(4, dts.subSeries(testDates[0], testDates[4]).size());
    assertEquals(5, dts.subSeries(testDates[0], true, testDates[4], true).size());
    assertEquals(1, dts.subSeries(testDates[4], testDates[5]).size());
    assertEquals(0, dts.subSeries(testDates[4], false, testDates[5], false).size());
    assertEquals(emptyTS, emptyTS.subSeries(testDates[1], testDates[1]));
  }

  @Test
  public void testHashCode() {
    assertEquals(createStandardTimeSeries().hashCode(), createStandardTimeSeries().hashCode());
    assertEquals(createEmptyTimeSeries().hashCode(), createEmptyTimeSeries().hashCode());
  }

  @Test
  public void testEquals() {
    assertEquals(createStandardTimeSeries(), createStandardTimeSeries());
    assertFalse(createStandardTimeSeries().equals(createEmptyTimeSeries()));
    assertFalse(createEmptyTimeSeries().equals(createStandardTimeSeries()));
    assertEquals(createEmptyTimeSeries(), createEmptyTimeSeries());
//    FastBackedDoubleTimeSeries<E> createStandardTimeSeries = (FastBackedDoubleTimeSeries<E>) createStandardTimeSeries();
//    FastBackedDoubleTimeSeries<E> createStandardTimeSeries2 = (FastBackedDoubleTimeSeries<E>) (createStandardTimeSeries().toDateTimeDoubleTimeSeries());
//    s_logger.info(createStandardTimeSeries.getFastSeries().toString());
//    s_logger.info(createStandardTimeSeries2.getFastSeries().toString());
//    assertEquals(createStandardTimeSeries.getFastSeries(), createStandardTimeSeries2.getFastSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toDateDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toMutableDateDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toDateTimeDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toMutableDateTimeDoubleTimeSeries());
//    try {
//      assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toFastIntDoubleTimeSeries());
//      assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toFastMutableIntDoubleTimeSeries());
//    } catch (OpenGammaRuntimeException ogre) {
//      // some combinations of classes don't support converting to fast int time series (e.g. things with millis precision).
//    }
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toFastLongDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toFastMutableLongDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toLocalDateDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toMutableLocalDateDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toZonedDateTimeDoubleTimeSeries());
//    assertEquals(createStandardTimeSeries(), createStandardTimeSeries().toMutableZonedDateTimeDoubleTimeSeries());
  }
  
  @Test
  public void testOperators() {
    DoubleTimeSeries<E> dts = createStandardTimeSeries();
    DoubleTimeSeries<E> dts2 = createStandardTimeSeries2();
    DoubleTimeSeries<E> ets = createEmptyTimeSeries();
    assertEquals(ets, dts.add(ets));
    assertEquals(ets, ets.add(dts));
    assertEquals(dts, dts.unionAdd(ets));
    assertEquals(dts, ets.unionAdd(dts));
    DoubleTimeSeries<E> result = (DoubleTimeSeries<E>) dts.add(dts2);
    assertEquals(3, result.size());
    assertEquals(Double.valueOf(8.0), result.getValueAt(0));
    assertEquals(Double.valueOf(10.0), result.getValueAt(1));
    assertEquals(Double.valueOf(12.0), result.getValueAt(2));
    assertEquals(dts.getTime(3), result.getTime(0));
    assertEquals(dts.getTime(4), result.getTime(1));
    assertEquals(dts.getTime(5), result.getTime(2));
    DoubleTimeSeries<E> unionResult = (DoubleTimeSeries<E>) dts.unionAdd(dts2);
    assertEquals(9, unionResult.size());
    assertEquals(Double.valueOf(1.0), unionResult.getValueAt(0));
    assertEquals(Double.valueOf(2.0), unionResult.getValueAt(1));
    assertEquals(Double.valueOf(3.0), unionResult.getValueAt(2));
    assertEquals(Double.valueOf(8.0), unionResult.getValueAt(3));
    assertEquals(Double.valueOf(10.0), unionResult.getValueAt(4));
    assertEquals(Double.valueOf(12.0), unionResult.getValueAt(5));
    assertEquals(Double.valueOf(7.0), unionResult.getValueAt(6));
    assertEquals(Double.valueOf(8.0), unionResult.getValueAt(7));
    assertEquals(Double.valueOf(9.0), unionResult.getValueAt(8));
    assertEquals(dts.getTime(0), unionResult.getTime(0));
    assertEquals(dts.getTime(1), unionResult.getTime(1));
    assertEquals(dts.getTime(2), unionResult.getTime(2));
    assertEquals(dts.getTime(3), unionResult.getTime(3));
    assertEquals(dts.getTime(4), unionResult.getTime(4));
    assertEquals(dts.getTime(5), unionResult.getTime(5));
    assertEquals(dts2.getTime(3), unionResult.getTime(6));
    assertEquals(dts2.getTime(4), unionResult.getTime(7));
    assertEquals(dts2.getTime(5), unionResult.getTime(8));
  }


}
