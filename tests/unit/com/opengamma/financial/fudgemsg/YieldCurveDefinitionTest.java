/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.fudgemsg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.Currency;
import com.opengamma.financial.analytics.ircurve.FixedIncomeStrip;
import com.opengamma.financial.analytics.ircurve.StripInstrumentType;
import com.opengamma.financial.analytics.ircurve.YieldCurveDefinition;
import com.opengamma.util.time.Tenor;

public class YieldCurveDefinitionTest extends FinancialTestBase {

  @Test
  public void testCycle() {
    final YieldCurveDefinition curveDefinition = new YieldCurveDefinition(Currency.getInstance("USD"), "ANNOYING", "STUPID");
    curveDefinition.addStrip(new FixedIncomeStrip(StripInstrumentType.CASH, Tenor.DAY, "Convention"));
    assertEquals(curveDefinition, cycleObject(YieldCurveDefinition.class, curveDefinition));
    curveDefinition.addStrip(new FixedIncomeStrip(StripInstrumentType.FUTURE, Tenor.TWO_YEARS, 3, "CONVENTIONAL"));
    assertEquals(curveDefinition, cycleObject(YieldCurveDefinition.class, curveDefinition));
  }

}
