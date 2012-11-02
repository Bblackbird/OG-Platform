/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.index;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;

import com.opengamma.analytics.financial.instrument.cash.DepositIborDefinition;

/**
 * Class with the description of Ibor deposit.
 */
public class GeneratorDepositIbor extends GeneratorInstrument {

  /**
   * The index. Not null.
   */
  private final IborIndex _index;

  /**
   * Constructor.
   * @param name The generator name.
   * @param index The index.
   */
  public GeneratorDepositIbor(final String name, final IborIndex index) {
    super(name);
    _index = index;
  }

  /**
   * Gets the index.
   * @return The index.
   */
  public IborIndex getIndex() {
    return _index;
  }

  @Override
  /**
   * tenor is not used. No objects required.
   */
  public DepositIborDefinition generateInstrument(ZonedDateTime date, Period tenor, double marketQuote, double notional, Object... objects) {
    return DepositIborDefinition.fromTrade(date, notional, marketQuote, _index);
  }

  @Override
  /**
   * startTenor and endTenor are not used. No objects required.
   */
  public DepositIborDefinition generateInstrument(ZonedDateTime date, Period startTenor, Period endTenor, double marketQuote, double notional, Object... objects) {
    return DepositIborDefinition.fromTrade(date, notional, marketQuote, _index);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + _index.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GeneratorDepositIbor other = (GeneratorDepositIbor) obj;
    if (!ObjectUtils.equals(_index, other._index)) {
      return false;
    }
    return true;
  }

}
