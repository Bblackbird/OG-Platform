/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.linearalgebra;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.matrix.DoubleMatrix2D;

/**
 * Base class for a bunch of matrix decomposition (e.g. SVD, LU etc)
 * @param <S> The type of the decomposition result
 */

public abstract class Decomposition<S extends DecompositionResult> extends Function1D<DoubleMatrix2D, S> {

}
