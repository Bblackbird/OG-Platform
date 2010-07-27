/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import com.opengamma.financial.model.tree.ConstantRecombiningBinomialTree;
import com.opengamma.financial.model.tree.RecombiningBinomialTree;

/**
 * 
 */
public class CoxRossRubinsteinBinomialOptionModelDefinition extends BinomialOptionModelDefinition<OptionDefinition, StandardOptionDataBundle> {

  @Override
  public double getUpFactor(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    return 1. / getDownFactor(option, data, n, j);
  }

  @Override
  public double getDownFactor(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    final double t = option.getTimeToExpiry(data.getDate());
    final double k = option.getStrike();
    final double sigma = data.getVolatility(t, k);
    final double dt = t / n;
    return Math.exp(-sigma * Math.sqrt(dt));
  }

  @Override
  public RecombiningBinomialTree<Double> getUpProbabilityTree(final OptionDefinition option, final StandardOptionDataBundle data, final int n, final int j) {
    final double b = data.getCostOfCarry();
    final double t = option.getTimeToExpiry(data.getDate());
    final double dt = t / n;
    final double u = getUpFactor(option, data, n, j);
    final double d = getDownFactor(option, data, n, j);
    System.out.println(u + " " + d + " " + b);
    System.out.println((Math.exp(b * dt) - d) / (u - d));
    return new ConstantRecombiningBinomialTree<Double>((Math.exp(b * dt) - d) / (u - d));
  }
}
