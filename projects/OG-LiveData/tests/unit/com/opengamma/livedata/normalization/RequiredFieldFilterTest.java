/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.junit.Test;

import com.opengamma.livedata.server.FieldHistoryStore;

/**
 * 
 *
 * @author kirk
 */
public class RequiredFieldFilterTest {

  @Test
  public void noRequiredFields() {
    RequiredFieldFilter filter = new RequiredFieldFilter();
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertNotNull(normalized);
    assertSame(normalized, msg);
  }

  @Test
  public void requiredFieldsNotSatisfied() {
    RequiredFieldFilter filter = new RequiredFieldFilter("Foo", "Fibble");
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertNull(normalized);
  }

  @Test
  public void requiredFieldsSatisfied() {
    RequiredFieldFilter filter = new RequiredFieldFilter("Foo");
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    MutableFudgeFieldContainer normalized = filter.apply(msg, new FieldHistoryStore());
    assertNotNull(normalized);
    assertSame(normalized, msg);
  }
}
