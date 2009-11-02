/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import javax.time.calendar.ZonedDateTime;

import com.opengamma.financial.model.interestrate.curve.DiscountCurve;
import com.opengamma.financial.model.interestrate.curve.DiscountCurveTransformation;
import com.opengamma.financial.model.option.definition.AmericanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.EuropeanVanillaOptionDefinition;
import com.opengamma.financial.model.option.definition.OptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.BivariateNormalDistribution;
import com.opengamma.math.statistics.distribution.NormalProbabilityDistribution;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 * @author emcleod
 * 
 */
public class BjerksundStenslandModel extends AnalyticOptionModel<AmericanVanillaOptionDefinition, StandardOptionDataBundle> {
  private final ProbabilityDistribution<Double[]> _bivariateNormal = new BivariateNormalDistribution();
  private final ProbabilityDistribution<Double> _normal = new NormalProbabilityDistribution(0, 1);
  protected BlackScholesMertonModel _bsm = new BlackScholesMertonModel();

  @Override
  public Function1D<StandardOptionDataBundle, Double> getPricingFunction(final AmericanVanillaOptionDefinition definition) {
    if (definition == null)
      throw new IllegalArgumentException("Option definition was null");
    final Function1D<StandardOptionDataBundle, Double> pricingFunction = new Function1D<StandardOptionDataBundle, Double>() {

      @Override
      public Double evaluate(final StandardOptionDataBundle data) {
        if (data == null)
          throw new IllegalArgumentException("Data bundle was null");
        final ZonedDateTime date = data.getDate();
        double s = data.getSpot();
        double k = definition.getStrike();
        final double t = definition.getTimeToExpiry(date);
        final double sigma = data.getVolatility(t, k);
        double r = data.getInterestRate(t);
        double b = data.getCostOfCarry();
        StandardOptionDataBundle newData = data;
        if (!definition.isCall()) {
          r -= b;
          b *= -1;
          final double temp = s;
          s = k;
          k = temp;
          final DiscountCurve curve = DiscountCurveTransformation.getParallelShiftedCurve(data.getDiscountCurve(), -b);
          newData = data.withDiscountCurve(curve).withSpot(s);
        }
        if (b >= r) {
          final OptionDefinition european = new EuropeanVanillaOptionDefinition(k, definition.getExpiry(), definition.isCall());
          final Function1D<StandardOptionDataBundle, Double> bsm = _bsm.getPricingFunction(european);
          return bsm.evaluate(newData);
        }
        return getCallPrice(s, k, sigma, t, r, b);
      }
    };
    return pricingFunction;
  }

  double getCallPrice(final double s, final double k, final double sigma, final double t2, final double r, final double b) {
    final double sigmaSq = sigma * sigma;
    final double y = 0.5 - b / sigmaSq;
    final double beta = y + Math.sqrt(y * y + 2 * r / sigmaSq);
    final double b0 = Math.max(k, r * k / (r - b));
    final double bInfinity = beta * k / (beta - 1);
    final double t1 = 0.5 * (Math.sqrt(5) - 1) * t2;
    final double h1 = getH(b, t1, sigma, k, b0, bInfinity);
    final double h2 = getH(b, t2, sigma, k, b0, bInfinity);
    final double x1 = getX(b0, bInfinity, h1);
    final double x2 = getX(b0, bInfinity, h2);
    if (s >= x2)
      return s - k;
    final double alpha1 = getAlpha(x1, beta, k);
    final double alpha2 = getAlpha(x2, beta, k);
    return alpha2 * Math.pow(s, beta) - alpha2 * getPhi(s, t1, beta, x2, x2, r, b, sigma) + getPhi(s, t1, 1, x2, x2, r, b, sigma) - getPhi(s, t1, 1, x1, x2, r, b, sigma) - k
        * getPhi(s, t1, 0, x2, x2, r, b, sigma) + k * getPhi(s, t1, 0, x1, x2, r, b, sigma) + alpha1 * getPhi(s, t1, beta, x1, x2, r, b, sigma) - alpha1
        * getPsi(s, t1, t2, beta, x1, x2, x1, r, b, sigma) + getPsi(s, t1, t2, 1, x1, x2, x1, r, b, sigma) - getPsi(s, t1, t2, 1, k, x2, x1, r, b, sigma) - k
        * getPsi(s, t1, t2, 0, x1, x2, x1, r, b, sigma) + k * getPsi(s, t1, t2, 0, k, x2, x1, r, b, sigma);
  }

  private double getH(final double b, final double t, final double sigma, final double k, final double b0, final double bInfinity) {
    return -(b * t + 2 * sigma * Math.sqrt(t)) * k * k / (b0 * (bInfinity - b0));
  }

  private double getX(final double b0, final double bInfinity, final double h) {
    return b0 + (bInfinity - b0) * (1 - Math.exp(h));
  }

  private double getAlpha(final double i, final double beta, final double k) {
    return Math.pow(i, -beta) * (i - k);
  }

  private double getPhi(final double s, final double t, final double gamma, final double h, final double x, final double r, final double b, final double sigma) {
    final double sigmaSq = sigma * sigma;
    final double denom = getDenom(t, sigma);
    final double lambda = getLambda(r, gamma, b, sigmaSq);
    final double kappa = getKappa(b, gamma, sigmaSq);
    final double y = getY(t, b, sigmaSq, gamma, denom);
    final double d1 = getD(s / h, denom, y);
    final double d2 = getD(x * x / (s * h), denom, y);
    return Math.exp(lambda * t) * Math.pow(s, gamma) * (_normal.getCDF(d1) - Math.pow(x / s, kappa) * _normal.getCDF(d2));
  }

  private double getPsi(final double s, final double t1, final double t2, final double gamma, final double h, final double x2, final double x1, final double r, final double b,
      final double sigma) {
    final double sigmaSq = sigma * sigma;
    final double denom1 = getDenom(t1, sigma);
    final double denom2 = getDenom(t2, sigma);
    final double y1 = getY(t1, b, sigmaSq, gamma, denom1);
    final double y2 = getY(t2, b, sigmaSq, gamma, denom2);
    final double d1 = getD(s / x1, denom1, y1);
    final double d2 = getD(x2 * x2 / (s * x1), denom1, y1);
    final double d3 = d1 + 2 * y1;
    final double d4 = d2 + 2 * y1;
    final double e1 = getD(s / h, denom2, y2);
    final double e2 = getD(x2 * x2 / (s * h), denom2, y2);
    final double e3 = getD(x1 * x1 / (s * h), denom2, y2);
    final double e4 = getD(s * x1 * x1 / (h * x2 * x2), denom2, y2);
    final double lambda = getLambda(r, gamma, b, sigmaSq);
    final double kappa = getKappa(b, gamma, sigmaSq);
    final double rho = Math.sqrt(t1 / t2);
    return Math.exp(lambda * t2)
        * Math.pow(s, gamma)
        * (_bivariateNormal.getCDF(new Double[] { d1, e1, rho }) - Math.pow(x2 / s, kappa) * _bivariateNormal.getCDF(new Double[] { d2, e2, rho }) - Math.pow(x1 / s, kappa)
            * _bivariateNormal.getCDF(new Double[] { d3, e3, -rho }) + Math.pow(x1 / x2, kappa) * _bivariateNormal.getCDF(new Double[] { d4, e4, -rho }));
  }

  private double getLambda(final double r, final double gamma, final double b, final double sigmaSq) {
    return -r + gamma * b + 0.5 * gamma * (gamma - 1) * sigmaSq;
  }

  private double getKappa(final double b, final double gamma, final double sigmaSq) {
    return 2 * b / sigmaSq + 2 * gamma - 1;
  }

  private double getY(final double t, final double b, final double sigmaSq, final double gamma, final double denom) {
    return t * (b + sigmaSq * (gamma - 0.5)) / denom;
  }

  private double getDenom(final double t, final double sigma) {
    return sigma * Math.sqrt(t);
  }

  private double getD(final double x, final double denom, final double y) {
    return -(Math.log(x) / denom + y);
  }
}
