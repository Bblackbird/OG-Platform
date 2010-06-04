/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util;

/**
 * Utility to simplify comparisons.
 */
public class CompareUtils {

  /**
   * Compares two objects, either of which might be null, sorting nulls low.
   * @param <E> type of object we're comparing to.
   * @param a  item that compareTo is called on
   * @param b item that is being compared
   * @return natural ordering as an integer (-ve when a < b, 0 when a == b, +ve when a > b)
   */
  public static <E> int compareWithNull(Comparable<E> a, E b) {
    if (a == null) {
      return b == null ? 0 : -1;
    } else if (b == null) {
      return 1;  // a not null
    } else {
      return a.compareTo((E) b);
    }    
  }

  /**
   * Compares two objects, either of which might be null, sorting nulls high.
   * @param <E> type of object we're comparing to.
   * @param a  item that compareTo is called on
   * @param b  item that is being compared
   * @return natural ordering as an integer (-ve when a < b, 0 when a == b, +ve when a > b)
   */
  public static <E> int compareWithNullHigh(Comparable<E> a, E b) {
    if (a == null) {
      return b == null ? 0 : 1;
    } else if (b == null) {
      return -1;  // a not null
    } else {
      return a.compareTo((E) b);
    }    
  }

  /**
   * Compare two doubles to see if they're 'closely' equal - this is because rounding errors can mean 
   * the results of double precision computations lead to small differences in results.  The definition
   * in this case of 'close' is that the difference is less than 10^-15 (1E-15).  If a different maximum
   * allowed difference is required, use the other version of this method.
   * @param a  the first value
   * @param b  the second value
   * @return true, if a and b are equal to within 10^-15, false otherwise
   */
  public static boolean closeEquals(double a, double b) {
    return (Math.abs(a - b) < 1E-15);
  }
  
  /**
   * Compare two doubles to see if they're 'closely' equal - this is because rounding errors can mean 
   * the results of double precision computations lead to small differences in results.  The definition
   * in this case of 'close' is that the absolute difference is less than the maxDifference parameter.  
   * If a different maximum
   * allowed difference is required, use the other version of this method.
   * @param a  the first value
   * @param b  the second value
   * @param maxDifference  the maximum difference to allow
   * @return true, if a and b are equal to within the tolerance
   */
  public static boolean closeEquals(double a, double b, double maxDifference) {
    return (Math.abs(a - b) < maxDifference);
  }

}
