/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.future.pricing;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.greeks.Greek;
import com.opengamma.financial.greeks.GreekResultCollection;
import com.opengamma.financial.model.forward.definition.FXForwardDataBundle;
import com.opengamma.financial.model.forward.definition.ForwardDefinition;
import com.opengamma.financial.model.forward.pricing.FXForwardModel;
import com.opengamma.financial.model.forward.pricing.ForwardModel;
import com.opengamma.financial.model.future.definition.FXFutureDataBundle;
import com.opengamma.financial.model.future.definition.FutureDefinition;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;

/**
 * @author emcleod
 *
 */
public class FXFutureAsForwardModelTest {
  private static final double R1 = 0.05;
  private static final double R2 = 0.07;
  private static final double SPOT = 1.4;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 1, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.5));
  private static final ForwardModel<FXForwardDataBundle> FORWARD_MODEL = new FXForwardModel();
  private static final ForwardDefinition FORWARD_DEFINITION = new ForwardDefinition(EXPIRY);
  private static final FXForwardDataBundle FORWARD_DATA = new FXForwardDataBundle(
      new ConstantYieldCurve(R1), new ConstantYieldCurve(R2), SPOT, DATE);
  private static final FutureModel<FXFutureDataBundle> MODEL = new FXFutureAsForwardModel();
  private static final FutureDefinition DEFINITION = new FutureDefinition(EXPIRY);
  private static final FXFutureDataBundle DATA = new FXFutureDataBundle(new ConstantYieldCurve(R1),
      new ConstantYieldCurve(R2), SPOT, DATE);
  private static final Set<Greek> GREEKS = EnumSet.of(Greek.FAIR_PRICE, Greek.DELTA);

  @Test(expected = NullPointerException.class)
  public void testNullDefinition() {
    MODEL.getGreeks(null, DATA, GREEKS);
  }

  @Test(expected = NullPointerException.class)
  public void testNullData() {
    MODEL.getGreeks(DEFINITION, null, GREEKS);
  }

  @Test(expected = NullPointerException.class)
  public void testNullGreekSet() {
    MODEL.getGreeks(DEFINITION, DATA, null);
  }

  @Test
  public void testRequiredGreeks() {
    assertEquals(new GreekResultCollection(), MODEL.getGreeks(DEFINITION, DATA, Collections.<Greek> emptySet()));
    assertEquals(new GreekResultCollection(), MODEL.getGreeks(DEFINITION, DATA, EnumSet.of(Greek.DELTA)));
  }

  @Test
  public void test() {
    final GreekResultCollection forwardResult = FORWARD_MODEL.getGreeks(FORWARD_DEFINITION, FORWARD_DATA, GREEKS);
    final GreekResultCollection futureResult = MODEL.getGreeks(DEFINITION, DATA, GREEKS);
    assertEquals(futureResult.size(), 1);
    assertEquals(forwardResult.size(), futureResult.size());
    assertEquals(forwardResult.keySet().iterator().next(), futureResult.keySet().iterator().next());
    assertEquals(forwardResult.values().iterator().next(), futureResult.values().iterator().next(), 1e-12);
  }
}
