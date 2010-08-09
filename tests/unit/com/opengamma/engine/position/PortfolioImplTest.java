/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.position;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.opengamma.id.UniqueIdentifier;

/**
 * Test PortfolioImpl.
 */
public class PortfolioImplTest {

  @Test
  public void test_construction_String() {
    PortfolioImpl test = new PortfolioImpl("Name");
    assertEquals(null, test.getUniqueIdentifier());
    assertEquals("Name", test.getName());
    assertEquals(true, test.getRootNode() instanceof PortfolioNodeImpl);
    assertEquals(0, test.getRootNode().size());
    assertEquals("Portfolio[]", test.toString());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_String_null() {
    new PortfolioImpl(null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_construction_PortfolioIdString() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    assertEquals(id("Scheme", "Id"), test.getUniqueIdentifier());
    assertEquals("Name", test.getName());
    assertEquals(true, test.getRootNode() instanceof PortfolioNodeImpl);
    assertEquals(0, test.getRootNode().size());
    assertEquals("Portfolio[Scheme::Id]", test.toString());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_PortfolioIdString_nullId() {
    new PortfolioImpl(null, "Name");
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_PortfolioIdString_nullName() {
    new PortfolioImpl(id("Scheme", "Id"), null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_construction_PortfolioIdStringNode() {
    PortfolioNodeImpl root = new PortfolioNodeImpl();
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name", root);
    assertEquals(id("Scheme", "Id"), test.getUniqueIdentifier());
    assertEquals("Name", test.getName());
    assertEquals(true, test.getRootNode() == root);
    assertEquals("Portfolio[Scheme::Id]", test.toString());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_PortfolioIdStringNode_nullId() {
    new PortfolioImpl(null, "Name", new PortfolioNodeImpl());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_PortfolioIdStringNode_nullName() {
    new PortfolioImpl(id("Scheme", "Id"), null, new PortfolioNodeImpl());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_construction_PortfolioIdStringNode_nullRoot() {
    new PortfolioImpl(id("Scheme", "Id"), "Name", null);
  }

  private UniqueIdentifier id(String scheme, String value) {
    return UniqueIdentifier.of(scheme, value);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_setUniqueIdentifier() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    test.setUniqueIdentifier(id("Scheme2", "Id2"));
    assertEquals(id("Scheme2", "Id2"), test.getUniqueIdentifier());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_setUniqueIdentifier_null() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    test.setUniqueIdentifier(null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_setName() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    test.setName("Name2");
    assertEquals("Name2", test.getName());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_setName_null() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    test.setName(null);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_setRootNode() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    PortfolioNodeImpl root = new PortfolioNodeImpl();
    test.setRootNode(root);
    assertSame(root, test.getRootNode());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_setRootNode_null() {
    PortfolioImpl test = new PortfolioImpl(id("Scheme", "Id"), "Name");
    test.setRootNode(null);
  }

}
