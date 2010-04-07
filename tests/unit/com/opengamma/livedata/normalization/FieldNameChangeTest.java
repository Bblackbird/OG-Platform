/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.junit.Test;

/**
 * 
 *
 * @author pietari
 */
public class FieldNameChangeTest {
  
  @Test
  public void fieldNameChange() {
    FieldNameChange nameChange = new FieldNameChange("Foo", "Bar");
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    MutableFudgeFieldContainer normalized = nameChange.apply(msg);
    assertEquals(3, normalized.getAllFields().size());
    assertNull(normalized.getByName("Foo"));
    assertEquals(2.0, (Double) normalized.getAllByName("Bar").get(0).getValue(), 0.0001);
    assertEquals("1", (String) normalized.getAllByName("Bar").get(1).getValue());
    assertEquals(500, normalized.getInt("Baz").intValue());
  }

}
