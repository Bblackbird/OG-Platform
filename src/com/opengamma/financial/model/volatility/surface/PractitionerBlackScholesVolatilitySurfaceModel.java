/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.surface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.math.function.Function;
import com.opengamma.math.function.Function2D;
import com.opengamma.math.regression.LeastSquaresRegression;
import com.opengamma.math.regression.LeastSquaresRegressionResult;
import com.opengamma.math.regression.OrdinaryLeastSquaresRegression;
import com.opengamma.util.tuple.Pair;

/**
 * 
 */
public class PractitionerBlackScholesVolatilitySurfaceModel implements VolatilitySurfaceModel<OptionDefinition, StandardOptionDataBundle> {
  private static final Logger s_logger = LoggerFactory.getLogger(PractitionerBlackScholesVolatilitySurfaceModel.class);
  private final VolatilitySurfaceModel<OptionDefinition, StandardOptionDataBundle> _bsmVolatilityModel = new BlackScholesMertonImpliedVolatilitySurfaceModel();
  private static final int DEGREE = 5;
  private final LeastSquaresRegression _regression;
  private static final Double[] EMPTY_ARRAY = new Double[0];
  private final Function<Double, double[]> _independentVariableFunction = new Function2D<Double, double[]>() {

    @Override
    public double[] evaluate(final Double t, final Double k) {
      final double[] result = new double[DEGREE];
      result[0] = k;
      result[1] = k * k;
      result[2] = t;
      result[3] = t * t;
      result[4] = k * t;
      return result;
    }

  };

  public PractitionerBlackScholesVolatilitySurfaceModel() {
    _regression = new OrdinaryLeastSquaresRegression();
  }

  @Override
  public VolatilitySurface getSurface(final Map<OptionDefinition, Double> prices, final StandardOptionDataBundle data) {
    Validate.notNull(prices, "prices");
    Validate.notNull(data, "data");
    if (prices.size() < DEGREE) {
      throw new IllegalArgumentException("Price map contained " + prices.size() + " data point(s); need at least " + DEGREE);
    }
    final List<Double> kList = new ArrayList<Double>();
    final List<Double> tList = new ArrayList<Double>();
    final List<Double> sigmaList = new ArrayList<Double>();
    Double k, t, sigma;
    for (final Map.Entry<OptionDefinition, Double> entry : prices.entrySet()) {
      k = entry.getKey().getStrike();
      t = entry.getKey().getTimeToExpiry(data.getDate());
      try {
        sigma = _bsmVolatilityModel.getSurface(Collections.<OptionDefinition, Double>singletonMap(entry.getKey(), entry.getValue()), data).getVolatility(Pair.of(t, k));
        if (k != null && t != null && sigma != null) {
          kList.add(k);
          tList.add(t);
          sigmaList.add(sigma);
        } else {
          s_logger.info("Problem getting BSM volatility for " + entry.getKey() + ", not using this option in regression");
        }
      } catch (final Exception e) {
        s_logger.info("Problem getting BSM volatility for " + entry.getKey() + ", not using this option in regression. Error was: ", e);
      }
    }
    return getVolatilitySurfaceForRegression(getRegressionResult(kList.toArray(EMPTY_ARRAY), tList.toArray(EMPTY_ARRAY), sigmaList.toArray(EMPTY_ARRAY)));
  }

  private LeastSquaresRegressionResult getRegressionResult(final Double[] kArray, final Double[] tArray, final Double[] sigmaArray) {
    final int length = kArray.length;
    final double[][] x = new double[length][DEGREE];
    final double[] y = new double[length];
    Double k;
    Double t;
    Double sigma;
    for (int i = 0; i < kArray.length; i++) {
      k = kArray[i];
      t = tArray[i];
      sigma = sigmaArray[i];
      x[i] = _independentVariableFunction.evaluate(t, k);
      y[i] = sigma;
    }
    return _regression.regress(x, null, y, true);
  }

  private VolatilitySurface getVolatilitySurfaceForRegression(final LeastSquaresRegressionResult result) {
    return new VolatilitySurface() {

      @SuppressWarnings("synthetic-access")
      @Override
      public Double getVolatility(final Pair<Double, Double> tk) {
        return result.getPredictedValue(_independentVariableFunction.evaluate(tk.getFirst(), tk.getSecond()));
      }

      @Override
      public Set<Pair<Double, Double>> getXYData() {
        throw new UnsupportedOperationException();
      }

      @Override
      public VolatilitySurface withMultipleShifts(final Map<Pair<Double, Double>, Double> shifts) {
        throw new UnsupportedOperationException();
      }

      @Override
      public VolatilitySurface withParallelShift(final double shift) {
        throw new UnsupportedOperationException();
      }

      @Override
      public VolatilitySurface withSingleShift(final Pair<Double, Double> xy, final double shift) {
        throw new UnsupportedOperationException();
      }

    };
  }
}
