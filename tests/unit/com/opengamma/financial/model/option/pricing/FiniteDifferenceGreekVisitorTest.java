/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.opengamma.financial.greeks.GreekVisitor;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * 
 */
public class FiniteDifferenceGreekVisitorTest {
  private static final Function1D<StandardOptionDataBundle, Double> FUNCTION = new Function1D<StandardOptionDataBundle, Double>() {

    @Override
    public Double evaluate(final StandardOptionDataBundle x) {
      return 0.;
    }

  };
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(new ConstantYieldCurve(1.), 0.03, new ConstantVolatilitySurface(0.1), 100., DateUtil.getUTCDate(
      2010, 5, 1));
  private static final OptionDefinition DEFINITION = new EuropeanVanillaOptionDefinition(110, new Expiry(DateUtil.getUTCDate(2011, 5, 1)), true);
  private static final GreekVisitor<Double> VISITOR = new FiniteDifferenceGreekVisitor<StandardOptionDataBundle, OptionDefinition>(FUNCTION, DATA, DEFINITION);

  @Test(expected = NullPointerException.class)
  public void testNullFunction() {
    new FiniteDifferenceGreekVisitor<StandardOptionDataBundle, OptionDefinition>(null, DATA, DEFINITION);
  }

  @Test(expected = NullPointerException.class)
  public void testNullData() {
    new FiniteDifferenceGreekVisitor<StandardOptionDataBundle, OptionDefinition>(FUNCTION, null, DEFINITION);
  }

  @Test(expected = NullPointerException.class)
  public void testNullDefinition() {
    new FiniteDifferenceGreekVisitor<StandardOptionDataBundle, OptionDefinition>(FUNCTION, DATA, null);
  }

  @Test
  public void testNotCalculated() {
    assertNull(VISITOR.visitDZetaDVol());
    assertNull(VISITOR.visitDriftlessTheta());
    assertNull(VISITOR.visitStrikeDelta());
    assertNull(VISITOR.visitStrikeGamma());
    assertNull(VISITOR.visitZeta());
    assertNull(VISITOR.visitZetaBleed());
  }
}
