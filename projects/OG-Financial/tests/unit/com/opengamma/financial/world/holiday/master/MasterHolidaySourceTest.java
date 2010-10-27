/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.master;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.time.Instant;
import javax.time.calendar.LocalDate;

import org.junit.Test;

import com.opengamma.financial.Currency;
import com.opengamma.financial.world.holiday.HolidayType;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;

/**
 * Test MasterHolidaySource.
 */
public class MasterHolidaySourceTest {

  private static final LocalDate DATE_MONDAY = LocalDate.of(2010, 10, 25);
  private static final LocalDate DATE_SUNDAY = LocalDate.of(2010, 10, 24);
  private static final Currency GBP = Currency.getInstance("GBP");
  private static final Identifier ID = Identifier.of("C", "D");
  private static final IdentifierBundle BUNDLE = IdentifierBundle.of(ID);

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor_1arg_nullMaster() throws Exception {
    new MasterHolidaySource(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor_2arg_nullMaster() throws Exception {
    new MasterHolidaySource(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor3arg_nullMaster() throws Exception {
    new MasterHolidaySource(null, null, null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_isHoliday_LocalDateCurrency_holiday() throws Exception {
    Instant now = Instant.nowSystemClock();
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    boolean testResult = test.isHoliday(DATE_MONDAY, GBP);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

  @Test
  public void test_isHoliday_LocalDateCurrency_workday() throws Exception {
    Instant now = Instant.nowSystemClock();
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    HolidaySearchResult result = new HolidaySearchResult();
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    boolean testResult = test.isHoliday(DATE_MONDAY, GBP);
    verify(mock, times(1)).search(request);
    
    assertEquals(false, testResult);
  }

  @Test
  public void test_isHoliday_LocalDateCurrency_sunday() throws Exception {
    Instant now = Instant.nowSystemClock();
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_SUNDAY);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    HolidaySearchResult result = new HolidaySearchResult();
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    boolean testResult = test.isHoliday(DATE_SUNDAY, GBP);
    verify(mock, times(0)).search(request);
    
    assertEquals(true, testResult);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_isHoliday_LocalDateTypeIdentifier_holiday() throws Exception {
    Instant now = Instant.nowSystemClock();
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(HolidayType.BANK, IdentifierBundle.of(ID));
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    boolean testResult = test.isHoliday(DATE_MONDAY, HolidayType.BANK, ID);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_isHoliday_LocalDateTypeIdentifierBundle_holiday() throws Exception {
    Instant now = Instant.nowSystemClock();
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(HolidayType.BANK, BUNDLE);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    boolean testResult = test.isHoliday(DATE_MONDAY, HolidayType.BANK, BUNDLE);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

}
