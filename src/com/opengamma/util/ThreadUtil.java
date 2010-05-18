/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util;

/**
 * Utility methods for working with threads.
 */
public final class ThreadUtil {

  /**
   * Restrictive constructor.
   */
  private ThreadUtil() {
  }

  /**
   * Attempt to join the thread specified safely.
   *  
   * @param thread  the thread to join, not null
   * @param timeoutMillis  the timeout in milliseconds
   * @return {@code true} if the join succeeded, or {@code false} if a timeout occurred
   */
  public static boolean safeJoin(Thread thread, long timeoutMillis) {
    if (!thread.isAlive()) {
      return true;
    }
    try {
      thread.join(timeoutMillis);
    } catch (InterruptedException e) {
      // clear the interrupted state
      Thread.interrupted();
    }
    return !thread.isAlive();
  }

}
