/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.rootfinding.newton;

import static com.opengamma.math.matrix.MatrixAlgebraFactory.OG_ALGEBRA;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.matrix.Matrix;

/**
 * 
 */
public class BroydenMatrixUpdateFunction implements NewtonRootFinderMatrixUpdateFunction {

  @Override
  public DoubleMatrix2D getUpdatedMatrix(final Function1D<DoubleMatrix1D, DoubleMatrix1D> f, final DoubleMatrix1D deltaX, final DoubleMatrix1D deltaY,
      final DoubleMatrix2D matrix) {
    Validate.notNull(f);
    Validate.notNull(deltaX);
    Validate.notNull(deltaY);
    Validate.notNull(matrix);
    final double length2 = OG_ALGEBRA.getInnerProduct(deltaX, deltaX);
    Matrix<?> temp = OG_ALGEBRA.subtract(deltaY, OG_ALGEBRA.multiply(matrix, deltaX));
    temp = OG_ALGEBRA.scale(temp, 1.0 / length2);
    return (DoubleMatrix2D) OG_ALGEBRA.add(matrix, OG_ALGEBRA.getOuterProduct(temp, deltaX));
  }

}
