/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.var.parametric;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.matrix.Matrix;
import com.opengamma.math.matrix.MatrixAlgebra;

/**
 * 
 */
public class DeltaGammaCovarianceMatrixStandardDeviationCalculator extends Function1D<Map<Integer, ParametricVaRDataBundle>, Double> {
  private final MatrixAlgebra _algebra;

  public DeltaGammaCovarianceMatrixStandardDeviationCalculator(final MatrixAlgebra algebra) {
    Validate.notNull(algebra);
    _algebra = algebra;
  }

  @Override
  public Double evaluate(final Map<Integer, ParametricVaRDataBundle> data) {
    Validate.notNull(data);
    final ParametricVaRDataBundle firstOrderData = data.get(1);
    final ParametricVaRDataBundle secondOrderData = data.get(2);
    double deltaStd = 0;
    double gammaStd = 0;
    if (firstOrderData != null) {
      final DoubleMatrix1D delta = (DoubleMatrix1D) firstOrderData.getSensitivities();
      final DoubleMatrix2D deltaCovariance = firstOrderData.getCovarianceMatrix();
      deltaStd = _algebra.getInnerProduct(delta, _algebra.multiply(deltaCovariance, delta));
    }
    if (secondOrderData != null) {
      final Matrix<?> gamma = secondOrderData.getSensitivities();
      final DoubleMatrix2D gammaCovariance = secondOrderData.getCovarianceMatrix();
      gammaStd = 0.5 * _algebra.getTrace(_algebra.getPower(_algebra.multiply(gamma, gammaCovariance), 2));
    }
    return Math.sqrt(deltaStd + gammaStd);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_algebra == null) ? 0 : _algebra.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DeltaGammaCovarianceMatrixStandardDeviationCalculator other = (DeltaGammaCovarianceMatrixStandardDeviationCalculator) obj;
    return ObjectUtils.equals(_algebra, other._algebra);
  }

}
