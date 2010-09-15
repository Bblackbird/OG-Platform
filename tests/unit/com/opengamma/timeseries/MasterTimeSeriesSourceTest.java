/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.timeseries;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.time.calendar.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.timeseries.localdate.ArrayLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.ListLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.MutableLocalDateDoubleTimeSeries;
import com.opengamma.util.tuple.Pair;

/**
 * Test that MasterTimeSeriesSource makes the right method calls to the underlying TimeSeriesMaster and TimeSeriesResolver
 */
public class MasterTimeSeriesSourceTest {
  
  private static final UniqueIdentifier UID = UniqueIdentifier.of("A", "1");
  private static final String CLOSE_DATA_FIELD = "CLOSE";
  private static final String CMPL_DATA_PROVIDER = "CMPL";
  private static final String BBG_DATA_SOURCE = "BLOOMBERG";
  private static final IdentifierBundle IDENTIFIERS = IdentifierBundle.of(Identifier.of("A", "B"));
  
  private TimeSeriesMaster<LocalDate> _mockMaster;
  private TimeSeriesMetaDataResolver _mockResolver;
  private MasterTimeSeriesSource _tsSource;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    _mockMaster = mock(TimeSeriesMaster.class);
    _mockResolver = mock(TimeSeriesMetaDataResolver.class);
    _tsSource = new MasterTimeSeriesSource(_mockMaster, _mockResolver);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    _tsSource = null;
    _mockResolver = null;
    _mockMaster = null;
  }
  

  @Test(expected = IllegalArgumentException.class)
  public void constructorWith1ArgNull() throws Exception {
    TimeSeriesMetaDataResolver mock = mock(TimeSeriesMetaDataResolver.class);
    new MasterTimeSeriesSource(null, mock);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void constructorWith2ArgNull() throws Exception {
    TimeSeriesMaster<LocalDate> mock = mock(TimeSeriesMaster.class);
    new MasterTimeSeriesSource(mock, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void constructorWithNull() throws Exception {
    new MasterTimeSeriesSource(null, null);
  }
  
  @Test
  public void getHistoricalDataByIdentifier() throws Exception {
    TimeSeriesSearchRequest<LocalDate> request = new TimeSeriesSearchRequest<LocalDate>();
    request.getIdentifiers().addAll(IDENTIFIERS.getIdentifiers());
    request.setDataSource(BBG_DATA_SOURCE);
    request.setDataProvider(CMPL_DATA_PROVIDER);
    request.setDataField(CLOSE_DATA_FIELD);
    request.setStart(null);
    request.setEnd(null);
    request.setLoadTimeSeries(true);
    
    TimeSeriesSearchResult<LocalDate> searchResult = new TimeSeriesSearchResult<LocalDate>();
    TimeSeriesDocument<LocalDate> tsDoc = new TimeSeriesDocument<LocalDate>();
    tsDoc.setTimeSeries(randomTimeSeries());
    tsDoc.setUniqueIdentifier(UID);
    searchResult.getDocuments().add(tsDoc);
    
    when(_mockMaster.searchTimeSeries(request)).thenReturn(searchResult);
    
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = _tsSource.getHistoricalData(IDENTIFIERS, BBG_DATA_SOURCE, CMPL_DATA_PROVIDER, CLOSE_DATA_FIELD);
    verify(_mockMaster, times(1)).searchTimeSeries(request);
    
    assertEquals(UID, tsPair.getFirst());
    assertEquals(tsDoc.getTimeSeries().times(), tsPair.getSecond().times());
    assertEquals(tsDoc.getTimeSeries().values(), tsPair.getSecond().values());
    
  }
  
  private LocalDateDoubleTimeSeries randomTimeSeries() {
    MutableLocalDateDoubleTimeSeries dts = new ListLocalDateDoubleTimeSeries();
    LocalDate start = LocalDate.of(2000, 1, 2);
    LocalDate end = start.plusYears(10);
    LocalDate current = start;
    while (current.isBefore(end)) {
      current = current.plusDays(1);
      dts.putDataPoint(current, Math.random());
    }
    return dts;
  }
  
  @Test
  public void getHistoricalDataByUID() throws Exception {
    TimeSeriesSearchRequest<LocalDate> request = new TimeSeriesSearchRequest<LocalDate>();
    request.setTimeSeriesId(UID);
    request.setLoadTimeSeries(true);
    request.setStart(null);
    request.setEnd(null);
    request.setLoadTimeSeries(true);
    
    TimeSeriesSearchResult<LocalDate> searchResult = new TimeSeriesSearchResult<LocalDate>();
    TimeSeriesDocument<LocalDate> tsDoc = new TimeSeriesDocument<LocalDate>();
    tsDoc.setTimeSeries(new ArrayLocalDateDoubleTimeSeries());
    tsDoc.setUniqueIdentifier(UID);
    tsDoc.setTimeSeries(randomTimeSeries());
    searchResult.getDocuments().add(tsDoc);
    
    when(_mockMaster.searchTimeSeries(request)).thenReturn(searchResult);
    
    LocalDateDoubleTimeSeries timeSeries = _tsSource.getHistoricalData(UID);
    verify(_mockMaster, times(1)).searchTimeSeries(request);
    
    assertEquals(tsDoc.getTimeSeries().times(), timeSeries.times());
    assertEquals(tsDoc.getTimeSeries().values(), timeSeries.values());
  }
  
  @Test
  public void getHistoricalDataWithoutMetaData() throws Exception {
    TimeSeriesSearchRequest<LocalDate> request = new TimeSeriesSearchRequest<LocalDate>();
    request.getIdentifiers().addAll(IDENTIFIERS.getIdentifiers());
    request.setDataSource(BBG_DATA_SOURCE);
    request.setDataProvider(CMPL_DATA_PROVIDER);
    request.setDataField(CLOSE_DATA_FIELD);
    request.setStart(null);
    request.setEnd(null);
    request.setLoadTimeSeries(true);
    
    TimeSeriesSearchResult<LocalDate> searchResult = new TimeSeriesSearchResult<LocalDate>();
    TimeSeriesDocument<LocalDate> tsDoc = new TimeSeriesDocument<LocalDate>();
    tsDoc.setTimeSeries(randomTimeSeries());
    tsDoc.setUniqueIdentifier(UID);
    searchResult.getDocuments().add(tsDoc);
    
    TimeSeriesMetaData metaData = new TimeSeriesMetaData();
    metaData.setDataField(CLOSE_DATA_FIELD);
    metaData.setDataProvider(CMPL_DATA_PROVIDER);
    metaData.setDataSource(BBG_DATA_SOURCE);
    metaData.setIdentifiers(IDENTIFIERS);
    
    when(_mockMaster.searchTimeSeries(request)).thenReturn(searchResult);
    when(_mockResolver.getDefaultMetaData(IDENTIFIERS)).thenReturn(metaData);
    
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = _tsSource.getHistoricalData(IDENTIFIERS);
    verify(_mockMaster, times(1)).searchTimeSeries(request);
    
    assertEquals(UID, tsPair.getFirst());
    assertEquals(tsDoc.getTimeSeries().times(), tsPair.getSecond().times());
    assertEquals(tsDoc.getTimeSeries().values(), tsPair.getSecond().values());
  }

}
