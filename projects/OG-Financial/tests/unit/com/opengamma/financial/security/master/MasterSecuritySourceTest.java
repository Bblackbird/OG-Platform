/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.master;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.time.Instant;

import org.junit.Test;

import com.opengamma.engine.security.DefaultSecurity;
import com.opengamma.engine.security.Security;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Test MasterSecuritySource.
 */
public class MasterSecuritySourceTest {

  private static final UniqueIdentifier UID = UniqueIdentifier.of("A", "B");
  private static final Identifier ID = Identifier.of("C", "D");
  private static final IdentifierBundle BUNDLE = IdentifierBundle.of(ID);

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor_1arg_nullMaster() throws Exception {
    new MasterSecuritySource(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor_2arg_nullMaster() throws Exception {
    new MasterSecuritySource(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_constructor3arg_nullMaster() throws Exception {
    new MasterSecuritySource(null, null, null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_getSecurityByUID() throws Exception {
    Instant now = Instant.nowSystemClock();
    SecurityMaster mock = mock(SecurityMaster.class);
    SecuritySearchHistoricRequest request = new SecuritySearchHistoricRequest(UID, now.minusSeconds(2), now.minusSeconds(1));
    request.setFullDetail(true);
    DefaultSecurity security = new DefaultSecurity(UID, "Test", "EQUITY", IdentifierBundle.EMPTY);
    SecuritySearchHistoricResult result = new SecuritySearchHistoricResult();
    result.getDocuments().add(new SecurityDocument(security));
    
    when(mock.searchHistoric(request)).thenReturn(result);
    MasterSecuritySource test = new MasterSecuritySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    Security testResult = test.getSecurity(UID);
    verify(mock, times(1)).searchHistoric(request);
    
    assertEquals(UID, testResult.getUniqueIdentifier());
    assertEquals("Test", testResult.getName());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_getSecuritiesByIdentifierBundle() throws Exception {
    Instant now = Instant.nowSystemClock();
    SecurityMaster mock = mock(SecurityMaster.class);
    SecuritySearchRequest request = new SecuritySearchRequest();
    request.setIdentityKey(BUNDLE);
    request.setFullDetail(true);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    DefaultSecurity security = new DefaultSecurity(UID, "Test", "EQUITY", IdentifierBundle.EMPTY);
    SecuritySearchResult result = new SecuritySearchResult();
    result.getDocuments().add(new SecurityDocument(security));
    
    when(mock.search(request)).thenReturn(result);
    MasterSecuritySource test = new MasterSecuritySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    Collection<Security> testResult = test.getSecurities(BUNDLE);
    verify(mock, times(1)).search(request);
    
    assertEquals(UID, testResult.iterator().next().getUniqueIdentifier());
    assertEquals("Test", testResult.iterator().next().getName());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_getSecurityByIdentifier() throws Exception {
    Instant now = Instant.nowSystemClock();
    SecurityMaster mock = mock(SecurityMaster.class);
    SecuritySearchRequest request = new SecuritySearchRequest();
    request.setIdentityKey(BUNDLE);
    request.setFullDetail(true);
    request.setVersionAsOfInstant(now.minusSeconds(2));
    request.setCorrectedToInstant(now.minusSeconds(1));
    DefaultSecurity security = new DefaultSecurity(UID, "Test", "EQUITY", IdentifierBundle.EMPTY);
    SecuritySearchResult result = new SecuritySearchResult();
    result.getDocuments().add(new SecurityDocument(security));
    
    when(mock.search(request)).thenReturn(result);
    MasterSecuritySource test = new MasterSecuritySource(mock, now.minusSeconds(2), now.minusSeconds(1));
    Security testResult = test.getSecurity(BUNDLE);
    verify(mock, times(1)).search(request);
    
    assertEquals(UID, testResult.getUniqueIdentifier());
    assertEquals("Test", testResult.getName());
  }

}
