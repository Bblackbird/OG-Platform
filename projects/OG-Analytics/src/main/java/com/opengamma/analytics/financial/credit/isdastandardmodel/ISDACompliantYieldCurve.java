/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.isdastandardmodel;

import java.util.Map;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * An ISDA compliant yield curve.
 */
public class ISDACompliantYieldCurve
    extends ISDACompliantCurve {

  /**
   * Creates a flat yield curve at level r.
   * 
   * @param t  the (arbitrary) single knot point (t > 0)
   * @param r  the level
   */
  public ISDACompliantYieldCurve(final double t, final double r) {
    super(t, r);
  }

  /**
   * Creates a yield (discount) curve with knots at times, t, zero rates, r, at the knots and piecewise constant
   * forward  rates between knots (i.e. linear interpolation of r*t or the -log(discountFactor).
   * 
   * @param t  the set of times that form the knots of the curve. Must be ascending with the first value >= 0, not null
   * @param r  the set of zero rates, not null
   */
  public ISDACompliantYieldCurve(final double[] t, final double[] r) {
    super(t, r);
  }

  /**
   * A curve in which the knots are measured (in fractions of a year) from a particular base-date but the curve is 'observed'
   * from a different base-date. As an example<br>
   * Today (the observation point) is 11-Jul-13, but the yield curve is snapped (bootstrapped from money market and swap rates)
   * on 10-Jul-13 - seen from today there is an offset of -1/365 (assuming a day count of ACT/365) that must be applied to use
   * the yield curve today.  <br>
   * In general, a discount curve observed at time $t_1$ can be written as $P(t_1,T)$. Observed from time $t_2$ this is
   * $P(t_2,T) = \frac{P(t_1,T)}{P(t_1,t_2)}$
   * 
   * @param timesFromBaseDate  the times measured from the base date of the curve, not null
   * @param rates  the zero rates, not null
   * @param newBaseFromOriginalBase  if this curve is to be used from a new base-date, what is the offset of the new base from the original 
   */
  ISDACompliantYieldCurve(final double[] timesFromBaseDate, final double[] rates, final double newBaseFromOriginalBase) {
    super(timesFromBaseDate, rates, newBaseFromOriginalBase);
  }

  /**
   * Creates a shallow copy of the specified curve, used to down cast from ISDACompliantCurve.
   * 
   * @param from  the curve to copy from, not null
   */
  public ISDACompliantYieldCurve(final ISDACompliantCurve from) {
    super(from);
  }

  /**
   * Creates an instance, used by deserialization.
   * 
   * @param t  the set of times that form the knots of the curve. Must be ascending with the first value >= 0.
   * @param r  the set of zero rates
   * @param rt  the set of rates at the knot times
   * @param df  the set of discount factors at the knot times
   * @deprecated This constructor is deprecated
   */
  @Deprecated
  public ISDACompliantYieldCurve(final double[] t, final double[] r, final double[] rt, final double[] df) {
    super(t, r, rt, df);
  }

  //-------------------------------------------------------------------------
  @Override
  public ISDACompliantYieldCurve withOffset(final double offsetFromNewBaseDate) {
    return new ISDACompliantYieldCurve(super.withOffset(offsetFromNewBaseDate));
  }

  @Override
  public ISDACompliantYieldCurve withRates(final double[] r) {
    return new ISDACompliantYieldCurve(super.withRates(r));
  }

  @Override
  public ISDACompliantYieldCurve withRate(final double rate, final int index) {
    return new ISDACompliantYieldCurve(super.withRate(rate, index));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ISDACompliantYieldCurve}.
   * @return the meta-bean, not null
   */
  public static ISDACompliantYieldCurve.Meta meta() {
    return ISDACompliantYieldCurve.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ISDACompliantYieldCurve.Meta.INSTANCE);
  }

  @Override
  public ISDACompliantYieldCurve.Meta metaBean() {
    return ISDACompliantYieldCurve.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(final String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  @Override
  public ISDACompliantYieldCurve clone() {
    final BeanBuilder<? extends ISDACompliantYieldCurve> builder = metaBean().builder();
    for (final MetaProperty<?> mp : metaBean().metaPropertyIterable()) {
      if (mp.style().isBuildable()) {
        Object value = mp.get(this);
        if (value instanceof Bean) {
          value = ((Bean) value).clone();
        }
        builder.set(mp.name(), value);
      }
    }
    return builder.build();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int hash = getClass().hashCode();
    return hash;
  }

  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder(32);
    buf.append("ISDACompliantYieldCurve{");
    final int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(final StringBuilder buf) {
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ISDACompliantYieldCurve}.
   */
  public static class Meta extends ISDACompliantCurve.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends ISDACompliantYieldCurve> builder() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends ISDACompliantYieldCurve> beanType() {
      return ISDACompliantYieldCurve.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
