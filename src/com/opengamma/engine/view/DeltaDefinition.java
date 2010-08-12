/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import org.apache.commons.lang.ObjectUtils;
import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.engine.value.ComputedValue;

/**
 * Encapsulates the logic for deciding whether the difference between any two {@link ComputedValue}s is sufficient to
 * be treated as a delta (in the context of a change). In the absence of a specific comparer, the implementation will
 * fall back onto {@link ObjectUtils.#equals(Object)}.
 */
public class DeltaDefinition {
  
  private static final String NUMBER_COMPARER_FIELD = "numberComparer";
  
  private DeltaComparer<Number> _numberComparer;
  
  /**
   * Sets a {@link DeltaComparer} to be used for numbers.
   * 
   * @param numberComparer  the comparer to use for numbers.
   */
  public void setNumberComparer(DeltaComparer<Number> numberComparer) {
    _numberComparer = numberComparer;
  }

  /**
   * @return  the comparer being used for numbers.
   */
  public DeltaComparer<Number> getNumberComparer() {
    return _numberComparer;
  }

  public boolean isDelta(ComputedValue previousComputed, ComputedValue newComputed) {
    if (previousComputed == null && newComputed == null) {
      return false;
    }
    if (previousComputed == null || newComputed == null) {
      return true;
    }
    if (!ObjectUtils.equals(previousComputed.getSpecification(), newComputed.getSpecification())) {
      // At least the specifications differ, which we want to report as a delta.
      return true;
    }

    // REVIEW jonathan 2010-05-10 -- Written with the assumption that we only really want to compare doubles and
    // BigDecimals, hence the specific Number check here rather than anything more generic.
    Object previousValue = previousComputed.getValue();
    Object newValue = newComputed.getValue();
    if (getNumberComparer() != null && previousValue instanceof Number && newValue instanceof Number) {
      return getNumberComparer().isDelta((Number) previousValue, (Number) newValue);
    }
    
    // Finally, fall back onto the most basic check
    return !ObjectUtils.equals(previousValue, newValue);
  }
  
  /**
   * Serialises this object to a Fudge message.
   * 
   * @param fudgeContext  the context
   * @return  a Fudge representation of this object
   */
  public FudgeFieldContainer toFudgeMsg(FudgeSerializationContext fudgeContext) {
    MutableFudgeFieldContainer msg = fudgeContext.newMessage();
    MutableFudgeFieldContainer numberComparerMsg = fudgeContext.objectToFudgeMsg(_numberComparer);
    FudgeSerializationContext.addClassHeader(numberComparerMsg, _numberComparer.getClass());
    msg.add(NUMBER_COMPARER_FIELD, numberComparerMsg);
    return msg;
  }
  
  /**
   * Deserialises a DeltaDefinition from a Fudge message.
   * 
   * @param fudgeContext  the context
   * @param msg  the message
   * @return  the deserialised DeltaDefinition
   */
  @SuppressWarnings("unchecked")
  public static DeltaDefinition fromFudgeMsg(FudgeDeserializationContext fudgeContext, FudgeFieldContainer msg) {
    DeltaDefinition deltaDefinition = new DeltaDefinition();
    FudgeField numberComparerField = msg.getByName(NUMBER_COMPARER_FIELD);
    if (numberComparerField != null) {
      deltaDefinition.setNumberComparer(fudgeContext.fieldValueToObject(DeltaComparer.class, numberComparerField)); 
    }
    return deltaDefinition;
  }

  @Override
  public int hashCode() {
    return ObjectUtils.hashCode(_numberComparer);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeltaDefinition)) {
      return false;
    }
    
    DeltaDefinition other = (DeltaDefinition) obj;
    if (getNumberComparer() == null) {
      return other.getNumberComparer() == null;
    }
    return getNumberComparer().equals(other.getNumberComparer());
  }
  
}
