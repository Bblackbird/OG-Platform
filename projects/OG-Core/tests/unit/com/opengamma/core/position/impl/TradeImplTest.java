/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.core.position.impl;


import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.math.BigDecimal;
import java.util.Set;

import javax.time.calendar.OffsetDateTime;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.opengamma.core.position.Counterparty;
import com.opengamma.core.position.Position;
import com.opengamma.core.security.SecurityLink;
import com.opengamma.core.security.test.MockSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.UniqueId;
import com.opengamma.util.money.Currency;

/**
 * Test TradeImpl.
 */
@Test
public class TradeImplTest {

  private static final Counterparty COUNTERPARTY = new CounterpartyImpl(ExternalId.of("CPARTY", "C100"));
  private static final UniqueId POSITION_UID = UniqueId.of("P", "A");
  private static final Position POSITION = new PositionImpl(POSITION_UID, BigDecimal.ONE, ExternalId.of("A", "B"));
  private static final OffsetDateTime TRADE_OFFSET_DATETIME = OffsetDateTime.now();
  private static final ExternalIdBundle BUNDLE = POSITION.getSecurityLink().getExternalId();

  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime() {
    TradeImpl test = new TradeImpl(POSITION.getUniqueId(), new SecurityLink(BUNDLE), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertNull(test.getUniqueId());
    assertEquals(BigDecimal.ONE, test.getQuantity());
    assertEquals(1, test.getSecurityLink().getExternalId().size());
    assertEquals(ExternalId.of("A", "B"), test.getSecurityLink().getExternalId().iterator().next());
    assertEquals(POSITION_UID, test.getParentPositionId());
    assertEquals(COUNTERPARTY, test.getCounterparty());
    assertNull(test.getSecurityLink().getTarget());
    assertEquals(TRADE_OFFSET_DATETIME.toLocalDate(), test.getTradeDate());
    assertEquals(TRADE_OFFSET_DATETIME.toOffsetTime(), test.getTradeTime());
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime_nullUniqueId() {
    new TradeImpl(null, new SecurityLink(BUNDLE), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime_nullLink() {
    new TradeImpl(POSITION.getUniqueId(), (SecurityLink) null, BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime_nullBigDecimal() {
    new TradeImpl(POSITION.getUniqueId(), new SecurityLink(BUNDLE), null, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime_nullCounterparty() {
    new TradeImpl(POSITION.getUniqueId(), new SecurityLink(BUNDLE), BigDecimal.ONE, null, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
  }

  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_construction_UniqueId_ExternalIdBundle_BigDecimal_Counterparty_LocalDate_OffsetTime_nullLocalDate() {
    new TradeImpl(POSITION.getUniqueId(), new SecurityLink(BUNDLE), BigDecimal.ONE, COUNTERPARTY, null, TRADE_OFFSET_DATETIME.toOffsetTime());
  }

  public void test_construction_UniqueId_Security_BigDecimal_Counterparty_Instant() {
    ExternalIdBundle securityKey = ExternalIdBundle.of(ExternalId.of("A", "B"));
    MockSecurity security = new MockSecurity("A");
    security.setExternalIdBundle(securityKey);
    
    TradeImpl test = new TradeImpl(POSITION_UID, security, BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertNull(test.getUniqueId());
    assertEquals(BigDecimal.ONE, test.getQuantity());
    assertEquals(1, test.getSecurityLink().getExternalId().size());
    assertEquals(ExternalId.of("A", "B"), test.getSecurityLink().getExternalId().iterator().next());
    assertEquals(POSITION_UID, test.getParentPositionId());
    assertEquals(COUNTERPARTY, test.getCounterparty());
    assertEquals(security, test.getSecurityLink().getTarget());
  }

  public void test_construction_copyFromPosition() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    trade.addAttribute("A", "B");
    trade.addAttribute("C", "D");
    
    TradeImpl copy = new TradeImpl(trade);
    assertEquals(copy, trade);
  }
  
  public void test_collectionsOfTradesWithDifferentFields() {
    Set<TradeImpl> trades = Sets.newHashSet();
    
    TradeImpl trade1 = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    trades.add(trade1);
    
    TradeImpl trade2 = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("C", "D")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    trade2.setPremium(100.00);
    trade2.setPremiumCurrency(Currency.USD);
    trade2.setPremiumDate(TRADE_OFFSET_DATETIME.toLocalDate().plusDays(1));
    trade2.setPremiumTime(TRADE_OFFSET_DATETIME.toOffsetTime().plusHours(1));
    trades.add(trade2);
    
    TradeImpl trade3 = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("E", "F")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    trades.add(trade3);
    
    trades.add(new TradeImpl(trade3));
    
    TradeImpl trade4 = new TradeImpl(trade1);
    trade4.addAttribute("key1", "value1");
    trade4.addAttribute("key2", "value2");
    trades.add(trade4);
    
    assertEquals(4, trades.size());
    assertTrue(trades.contains(trade1));
    assertTrue(trades.contains(trade2));
    assertTrue(trades.contains(trade3));
    assertTrue(trades.contains(trade4));
    
    trades.remove(trade1);
    assertEquals(3, trades.size());
    assertFalse(trades.contains(trade1));
    
    trades.remove(trade2);
    assertEquals(2, trades.size());
    assertFalse(trades.contains(trade2));
    
    trades.remove(trade3);
    assertEquals(1, trades.size());
    assertFalse(trades.contains(trade3));
    
    trades.remove(trade4);
    assertTrue(trades.isEmpty());
  }
  
  //------------------------------------------------------------------------
  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_addAttribute_null_key() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertTrue(trade.getAttributes().isEmpty());
    trade.addAttribute(null, "B");
  }
  
  @Test(expectedExceptions=IllegalArgumentException.class)
  public void test_addAttribute_null_value() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertTrue(trade.getAttributes().isEmpty());
    trade.addAttribute("A", null);
  }
  
  public void test_addAttribute() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertTrue(trade.getAttributes().isEmpty());
    trade.addAttribute("A", "B");
    assertEquals(1, trade.getAttributes().size());
    assertEquals("B", trade.getAttributes().get("A"));
    trade.addAttribute("C", "D");
    assertEquals(2, trade.getAttributes().size());
    assertEquals("D", trade.getAttributes().get("C"));
  }
  
  public void test_removeAttribute() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertTrue(trade.getAttributes().isEmpty());
    trade.addAttribute("A", "B");
    trade.addAttribute("C", "D");
    assertEquals(2, trade.getAttributes().size());
    trade.removeAttribute("A");
    assertEquals(1, trade.getAttributes().size());
    assertNull(trade.getAttributes().get("A"));
  }
  
  public void test_clearAttributes() {
    TradeImpl trade = new TradeImpl(POSITION_UID, new SecurityLink(ExternalId.of("A", "B")), BigDecimal.ONE, COUNTERPARTY, TRADE_OFFSET_DATETIME.toLocalDate(), TRADE_OFFSET_DATETIME.toOffsetTime());
    assertTrue(trade.getAttributes().isEmpty());
    trade.addAttribute("A", "B");
    trade.addAttribute("C", "D");
    assertEquals(2, trade.getAttributes().size());
    trade.clearAttributes();
    assertTrue(trade.getAttributes().isEmpty());
  }
  
}
