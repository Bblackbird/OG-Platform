/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.exchange.master.memory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.world.exchange.ExchangeUtils;
import com.opengamma.financial.world.exchange.master.ExchangeDocument;
import com.opengamma.financial.world.exchange.master.ExchangeSearchRequest;
import com.opengamma.financial.world.exchange.master.ExchangeSearchResult;
import com.opengamma.financial.world.exchange.master.ManageableExchange;
import com.opengamma.financial.world.region.RegionUtils;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Test InMemoryExchangeMaster.
 */
public class InMemoryExchangeMasterTest {

  private static String NAME = "LIFFE";
  private static Identifier ID_LIFFE_MIC = Identifier.of(ExchangeUtils.ISO_MIC, "XLIF");
  private static Identifier ID_LIFFE_CCID = Identifier.of(ExchangeUtils.COPP_CLARK_CENTER_ID, "979");
  private static Identifier ID_LIFFE_CCNAME = Identifier.of(ExchangeUtils.COPP_CLARK_NAME, "Euronext LIFFE (UK contracts)");
  private static Identifier ID_OTHER1 = Identifier.of("TEST_SCHEME", "EURONEXT LIFFE");
  private static Identifier ID_OTHER2 = Identifier.of("TEST_SCHEME", "LIFFE");
  private static IdentifierBundle BUNDLE_FULL = IdentifierBundle.of(ID_LIFFE_MIC, ID_LIFFE_CCNAME, ID_LIFFE_CCID);
  private static IdentifierBundle BUNDLE_PART = IdentifierBundle.of(ID_LIFFE_MIC, ID_LIFFE_CCID);
  private static IdentifierBundle BUNDLE_OTHER = IdentifierBundle.of(ID_LIFFE_MIC, ID_LIFFE_CCNAME, ID_OTHER1);
  private static Identifier GB = RegionUtils.countryRegionId("GB");

  private InMemoryExchangeMaster master;
  private ExchangeDocument addedDoc;

  @Before
  public void setUp() {
    master = new InMemoryExchangeMaster();
    ManageableExchange inputExchange = new ManageableExchange(BUNDLE_FULL, NAME, GB);
    ExchangeDocument inputDoc = new ExchangeDocument(inputExchange);
    addedDoc = master.add(inputDoc);
  }

  //-------------------------------------------------------------------------
  @Test(expected = DataNotFoundException.class)
  public void test_get_noMatch() {
    master.get(UniqueIdentifier.of("A", "B"));
  }

  public void test_get_match() {
    ExchangeDocument result = master.get(addedDoc.getExchangeId());
    assertEquals(Identifier.of("MemExg", "1"), result.getExchangeId());
    assertEquals(addedDoc, result);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_oneId_noMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(ID_OTHER1);
    ExchangeSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_oneId_mic() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(ID_LIFFE_MIC);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_oneId_ccid() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(ID_LIFFE_MIC);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_oneBundle_noMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(BUNDLE_OTHER);
    ExchangeSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_oneBundle_full() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(BUNDLE_FULL);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_oneBundle_part() {
    ExchangeSearchRequest request = new ExchangeSearchRequest(BUNDLE_PART);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_twoBundles_noMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest();
    request.addIdentifierBundle(ID_OTHER1);
    request.addIdentifierBundle(ID_OTHER2);
    ExchangeSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_twoBundles_oneMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest();
    request.addIdentifierBundle(ID_LIFFE_MIC);
    request.addIdentifierBundle(ID_OTHER1);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_twoBundles_bothMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest();
    request.addIdentifierBundle(ID_LIFFE_MIC);
    request.addIdentifierBundle(ID_LIFFE_CCID);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_name_noMatch() {
    ExchangeSearchRequest request = new ExchangeSearchRequest();
    request.setName("No match");
    ExchangeSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_name_match() {
    ExchangeSearchRequest request = new ExchangeSearchRequest();
    request.setName(NAME);
    ExchangeSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

}
