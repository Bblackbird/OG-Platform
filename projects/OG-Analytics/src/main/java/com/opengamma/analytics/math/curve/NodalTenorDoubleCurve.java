/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.math.curve;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.time.Tenor;
import com.opengamma.util.tuple.Pair;

/**
 * Specialization of {@code NodalObjectsCurve} with Tenor and Double.
 */
@BeanDefinition
public class NodalTenorDoubleCurve
    extends NodalObjectsCurve<Tenor, Double> {

  public static NodalTenorDoubleCurve from(final Tenor[] xData, final Double[] yData) {
    return new NodalTenorDoubleCurve(xData, yData, false);
  }

  public static NodalTenorDoubleCurve from(final Tenor[] xData, final Double[] yData, final String name) {
    return new NodalTenorDoubleCurve(xData, yData, false, name);
  }

  public static NodalTenorDoubleCurve fromSorted(final Tenor[] xData, final Double[] yData) {
    return new NodalTenorDoubleCurve(xData, yData, true);
  }

  public static NodalTenorDoubleCurve fromSorted(final Tenor[] xData, final Double[] yData, final String name) {
    return new NodalTenorDoubleCurve(xData, yData, true, name);
  }

  //-------------------------------------------------------------------------
  /**
   * Constructor for Joda-Beans.
   */
  protected NodalTenorDoubleCurve() {
  }

  public NodalTenorDoubleCurve(List<Tenor> xData, List<Double> yData, boolean isSorted, String name) {
    super(xData, yData, isSorted, name);
  }

  public NodalTenorDoubleCurve(List<Tenor> xData, List<Double> yData, boolean isSorted) {
    super(xData, yData, isSorted);
  }

  public NodalTenorDoubleCurve(Map<Tenor, Double> data, boolean isSorted, String name) {
    super(data, isSorted, name);
  }

  public NodalTenorDoubleCurve(Map<Tenor, Double> data, boolean isSorted) {
    super(data, isSorted);
  }

  public NodalTenorDoubleCurve(Set<Pair<Tenor, Double>> data, boolean isSorted, String name) {
    super(data, isSorted, name);
  }

  public NodalTenorDoubleCurve(Set<Pair<Tenor, Double>> data, boolean isSorted) {
    super(data, isSorted);
  }

  public NodalTenorDoubleCurve(Tenor[] xData, Double[] yData, boolean isSorted, String name) {
    super(xData, yData, isSorted, name);
  }

  public NodalTenorDoubleCurve(Tenor[] xData, Double[] yData, boolean isSorted) {
    super(xData, yData, isSorted);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code NodalTenorDoubleCurve}.
   * @return the meta-bean, not null
   */
  public static NodalTenorDoubleCurve.Meta meta() {
    return NodalTenorDoubleCurve.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(NodalTenorDoubleCurve.Meta.INSTANCE);
  }

  @Override
  public NodalTenorDoubleCurve.Meta metaBean() {
    return NodalTenorDoubleCurve.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  @Override
  public NodalTenorDoubleCurve clone() {
    return (NodalTenorDoubleCurve) super.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(32);
    buf.append("NodalTenorDoubleCurve{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code NodalTenorDoubleCurve}.
   */
  public static class Meta extends NodalObjectsCurve.Meta<Tenor, Double> {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends NodalTenorDoubleCurve> builder() {
      return new DirectBeanBuilder<NodalTenorDoubleCurve>(new NodalTenorDoubleCurve());
    }

    @Override
    public Class<? extends NodalTenorDoubleCurve> beanType() {
      return NodalTenorDoubleCurve.class;
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
