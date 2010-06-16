/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.securities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.opengamma.financial.GICSCode;

/**
 * 
 *
 */
public class GICSCodeTest {

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid1() {
    GICSCode.getInstance(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid2() {
    GICSCode.getInstance(100);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid3() {
    GICSCode.getInstance(10100);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalid4() {
    GICSCode.getInstance(1010100);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidString1() {
    GICSCode.getInstance("Kirk Wylie");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidString2() {
    GICSCode.getInstance("-5");
  }

  @Test
  public void testValid1() {
    for (int i = 1; i <= 99; i++) {
      GICSCode.getInstance(i);
    }
  }

  @Test
  public void testValid2() {
    for (int i = 1; i <= 99; i++) {
      GICSCode.getInstance(100 + i);
    }
  }

  @Test
  public void testValid3() {
    for (int i = 1; i <= 99; i++) {
      GICSCode.getInstance(10100 + i);
    }
  }

  @Test
  public void testValid4() {
    for (int i = 1; i <= 99; i++) {
      GICSCode.getInstance(1010100 + i);
    }
  }

  @Test
  public void testValidString() {
    GICSCode.getInstance("10101001");
  }

  @Test
  public void testEquals() {
    GICSCode c1 = GICSCode.getInstance(1);
    GICSCode c2 = GICSCode.getInstance(1);
    GICSCode c3 = GICSCode.getInstance(10);
    assertTrue(c1.equals(c1));
    assertTrue(c1.equals(c2));
    assertFalse(c1.equals(c3));
    assertFalse(c1.equals("1"));
  }

  @Test
  public void testHashCode() {
    GICSCode c1 = GICSCode.getInstance(1);
    assertEquals(c1.getCode(), c1.hashCode());
  }

  @Test
  public void codeDeconstruction() {
    GICSCode c1 = GICSCode.getInstance(45103020);
    assertEquals(45, c1.getSectorCode());
    assertEquals(10, c1.getIndustryGroupCode());
    assertEquals(30, c1.getIndustryCode());
    assertEquals(20, c1.getSubIndustryCode());
  }

}
