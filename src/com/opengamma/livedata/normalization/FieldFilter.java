/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.Validate;
import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeField;
import org.fudgemsg.MutableFudgeFieldContainer;

import com.google.common.collect.Sets;
import com.opengamma.livedata.server.FieldHistoryStore;
import com.opengamma.util.ArgumentChecker;

/**
 * Strips all fields out of the message except the ones you want to explicitly accept.
 * <p>
 * If no field is accepted, the message is extinguished. 
 */
public class FieldFilter implements NormalizationRule {
  
  private final Collection<String> _fieldsToAccept;
  private final FudgeContext _context;
  
  public FieldFilter(String... fieldsToAccept) {
    this(FudgeContext.GLOBAL_DEFAULT, fieldsToAccept);
  }
  
  public FieldFilter(FudgeContext context, String... fieldsToAccept) {
    this(Sets.newHashSet(fieldsToAccept), context);
  }
  
  public FieldFilter(Collection<String> fieldsToAccept) {
    this(fieldsToAccept, FudgeContext.GLOBAL_DEFAULT);
  }

  public FieldFilter(Collection<String> fieldsToAccept, FudgeContext context) {
    ArgumentChecker.notNull(fieldsToAccept, "List of accepted fields");
    Validate.notNull(context, "Must provide a FudgeContext");
    _fieldsToAccept = new HashSet<String>(fieldsToAccept);
    _context = context;
  }

  /**
   * @return the context
   */
  public FudgeContext getContext() {
    return _context;
  }

  @Override
  public MutableFudgeFieldContainer apply(
      MutableFudgeFieldContainer msg,
      FieldHistoryStore fieldHistory) {
    
    MutableFudgeFieldContainer normalizedMsg = getContext().newMessage();
    // REVIEW kirk 2010-04-15 -- Run through the fields in the order of the
    // original message and check for containment in _fieldsToAccept as it's
    // faster for large messages.
    // It also supports multiple values with the same name.
    for (FudgeField field : msg.getAllFields()) {
      if (field.getName() == null) {
        // Don't allow non-named fields.
        continue;
      }
      if (!_fieldsToAccept.contains(field.getName())) {
        continue;
      }
      normalizedMsg.add(field);
    }
    
    if (normalizedMsg.getAllFields().isEmpty()) {
      return null; // extinguish message
    }
    
    return normalizedMsg;
  }
  
}
