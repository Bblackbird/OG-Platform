/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic.twoasset;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.twoasset.StandardTwoAssetOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class TwoAssetAnalyticOptionModelTest {
  private static final double RESULT = 100;
  private static final Function1D<StandardTwoAssetOptionDataBundle, Double> F = new Function1D<StandardTwoAssetOptionDataBundle, Double>() {

    @Override
    public Double evaluate(final StandardTwoAssetOptionDataBundle x) {
      return RESULT;
    }

  };
  private static final TwoAssetAnalyticOptionModel<OptionDefinition, StandardTwoAssetOptionDataBundle> DUMMY = new TwoAssetAnalyticOptionModel<OptionDefinition, StandardTwoAssetOptionDataBundle>() {

    @SuppressWarnings("synthetic-access")
    @Override
    public Function1D<StandardTwoAssetOptionDataBundle, Double> getPricingFunction(final OptionDefinition definition) {
      return F;
    }
  };
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final OptionDefinition OPTION = new EuropeanVanillaOptionDefinition(100, new Expiry(DATE), true);
  private static final StandardTwoAssetOptionDataBundle DATA = new StandardTwoAssetOptionDataBundle(new YieldCurve(ConstantDoublesCurve.from(0.1)), 0, 0, new ConstantVolatilitySurface(0.1),
      new ConstantVolatilitySurface(0.15), 100, 90, 1, DATE);
  private static final Set<Greek> REQUIRED_GREEKS = Sets.newHashSet(Greek.FAIR_PRICE, Greek.DELTA, Greek.GAMMA);

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    DUMMY.getGreeks(null, DATA, REQUIRED_GREEKS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    DUMMY.getGreeks(OPTION, null, REQUIRED_GREEKS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullGreeks() {
    DUMMY.getGreeks(OPTION, DATA, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyGreeks() {
    DUMMY.getGreeks(OPTION, DATA, Collections.<Greek> emptySet());
  }

  @Test
  public void test() {
    final GreekResultCollection result = DUMMY.getGreeks(OPTION, DATA, REQUIRED_GREEKS);
    assertEquals(result.size(), 1);
    assertEquals(result.get(Greek.FAIR_PRICE), RESULT, 0);
  }
}
