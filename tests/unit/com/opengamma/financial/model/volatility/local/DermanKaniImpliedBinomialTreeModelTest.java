/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.local;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.model.volatility.surface.FunctionalVolatilitySurface;
import com.opengamma.math.function.Function1D;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;
import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class DermanKaniImpliedBinomialTreeModelTest {
  private static final double SPOT = 100;
  private static final YieldAndDiscountCurve R = new ConstantYieldCurve(0.05);
  private static final double B = 0.05;
  private static final double ATM_VOL = 0.15;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final OptionDefinition OPTION = new EuropeanVanillaOptionDefinition(SPOT, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 5)), true);
  private static final ImpliedTreeModel<OptionDefinition, StandardOptionDataBundle> MODEL = new DermanKaniImpliedBinomialTreeModel();
  private static final Function1D<DoublesPair, Double> SMILE = new Function1D<DoublesPair, Double>() {

    @Override
    public Double evaluate(final DoublesPair pair) {
      final double k = pair.second;
      return ATM_VOL + (SPOT - k) * 0.0005;
    }

  };
  private static final StandardOptionDataBundle DATA = new StandardOptionDataBundle(R, B, new FunctionalVolatilitySurface(SMILE), SPOT, DATE);

  @Test(expected = IllegalArgumentException.class)
  public void testNullDefinition() {
    MODEL.getImpliedTrees(null, DATA);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullData() {
    MODEL.getImpliedTrees(OPTION, null);
  }

  @Test
  public void test() {
    final Double[][] expectedSpot = new Double[][] {new Double[] {100.}, new Double[] {86.07, 116.18}, new Double[] {70.49, 100., 131.94}, new Double[] {60.63, 85.97, 116.32, 148.04},
        new Double[] {44.05, 70.46, 100., 132.13, 163.24}, new Double[] {41.04, 60.47, 85.86, 116.47, 148.14, 177.53}};
    final Double[][] expectedLocalVol = new Double[][] {new Double[] {.145}, new Double[] {.163, .128}, new Double[] {.174, .146, .110}, new Double[] {.205, .164, .128, .091},
        new Double[] {.172, .175, .147, .109, .073}};
    final ImpliedTreeResult result = MODEL.getImpliedTrees(OPTION, DATA);
    final Double[][] spot = result.getSpotPriceTree().getTree();
    assertEquals(spot.length, expectedSpot.length);
    for (int i = 0; i < expectedSpot.length; i++) {
      for (int j = 0; j < expectedSpot[i].length; j++) {
        assertEquals(spot[i][j], expectedSpot[i][j], 1e-2);
      }
    }
    final Double[][] localVol = result.getLocalVolatilityTree().getTree();
    assertEquals(localVol.length, expectedLocalVol.length);
    for (int i = 0; i < expectedLocalVol.length; i++) {
      for (int j = 0; j < expectedLocalVol[i].length; j++) {
        assertEquals(localVol[i][j], expectedLocalVol[i][j], 1e-3);
      }
    }
  }
}
