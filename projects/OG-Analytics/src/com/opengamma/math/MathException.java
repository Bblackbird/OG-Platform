/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math;

/**
 * 
 */
public class MathException extends RuntimeException {

  public MathException() {
    super();
  }

  public MathException(final String s) {
    super(s);
  }

  public MathException(final String s, final Throwable cause) {
    super(s, cause);
  }

  public MathException(final Throwable cause) {
    super(cause);
  }
}
