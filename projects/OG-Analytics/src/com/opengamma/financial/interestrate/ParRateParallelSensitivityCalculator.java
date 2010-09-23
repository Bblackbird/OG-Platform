/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class ParRateParallelSensitivityCalculator {

  private final ParRateCurveSensitivityCalculator _prcsc = ParRateCurveSensitivityCalculator.getInstance();

  /**
   * Calculates the change in par rate of an instrument due to a parallel move of each yield curve the instrument is sensitive to
   * @param ird instrument 
   * @param curves bundle of relevant yield curves 
   * @return a Map between curve name and sensitivity for that curve 
   */
  public Map<String, Double> getValue(final InterestRateDerivative ird, final YieldCurveBundle curves) {

    final Map<String, List<DoublesPair>> sense = _prcsc.getValue(ird, curves);
    final Map<String, Double> res = new HashMap<String, Double>();
    final Iterator<Entry<String, List<DoublesPair>>> iterator = sense.entrySet().iterator();
    while (iterator.hasNext()) {
      final Entry<String, List<DoublesPair>> entry = iterator.next();
      final String name = entry.getKey();
      final double temp = sumListPair(entry.getValue());
      res.put(name, temp);
    }
    return res;
  }

  private double sumListPair(final List<DoublesPair> list) {
    double sum = 0.0;
    for (final DoublesPair pair : list) {
      sum += pair.getSecond();
    }
    return sum;
  }

}
