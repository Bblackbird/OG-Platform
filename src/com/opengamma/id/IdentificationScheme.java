/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.id;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.opengamma.util.ArgumentChecker;

/**
 * A classification scheme for identifiers.
 * <p>
 * The scheme defines a universe of unique identifiers.
 * Each identifier is only unique with respect to the scheme.
 * The same identifier may have a different meaning in a different scheme.
 * <p>
 * Fundamentally, this is nothing other than a type-safe wrapper on top of
 * a name describing the identification scheme.
 * <p/>
 * Real-world examples of a scheme include:
 * <ul>
 *   <li>ISIN, CUSIP for globally unique identifiers on traded securities.</li>
 *   <li>A trading system instance name for trades and positions.</li>
 *   <li>RIC for a Reuters-provided market data object.</li>
 * </ul>
 *
 * @author kirk
 */
public class IdentificationScheme implements Serializable, Cloneable, Comparable<IdentificationScheme> {

  /**
   * Scheme for Bloomberg BUIDs.
   */
  public static final IdentificationScheme BLOOMBERG_BUID = new IdentificationScheme("BLOOMBERG_BUID");
  /**
   * Scheme for Bloomberg tickers.
   */
  public static final IdentificationScheme BLOOMBERG_TICKER = new IdentificationScheme("BLOOMBERG_TICKER");
  /**
   * Scheme for CUSIPs.
   */
  public static final IdentificationScheme CUSIP = new IdentificationScheme("CUSIP");
  /**
   * Scheme for ISIN.
   */
  public static final IdentificationScheme ISIN = new IdentificationScheme("ISIN");
  /**
   * Scheme for Reuters RICs.
   */
  public static final IdentificationScheme RIC = new IdentificationScheme("RIC");
  /**
   * Scheme for SEDOL1.
   */
  public static final IdentificationScheme SEDOL1 = new IdentificationScheme("SEDOL1");

  /**
   * The scheme name.
   */
  private final String _name;

  /**
   * Constructs a scheme using the specified name.
   * @param name  the scheme name, not null
   */
  public IdentificationScheme(String name) {
    ArgumentChecker.checkNotNull(name, "name");
    _name = name;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the scheme name.
   * @return the scheme name, never null
   */
  public String getName() {
    return _name;
  }

  //-------------------------------------------------------------------------
  @Override
  protected IdentificationScheme clone() {
    try {
      return (IdentificationScheme) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError("Cloning actually IS supported");
    }
  }

  @Override
  public int compareTo(IdentificationScheme obj) {
    return _name.compareTo(obj._name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof IdentificationScheme) {
      IdentificationScheme other = (IdentificationScheme) obj;
      return ObjectUtils.equals(_name, other._name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return _name.hashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
