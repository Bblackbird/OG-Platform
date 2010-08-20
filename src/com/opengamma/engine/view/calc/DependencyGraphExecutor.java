/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calc;

import java.util.concurrent.Future;

import com.opengamma.engine.depgraph.DependencyGraph;

/**
 * Evaluates a dependency graph.
 * 
  * @param <T> Type of return information from the executor
 */
public interface DependencyGraphExecutor<T> {
  
  /**
   * Evaluates a dependency graph. 
   * 
   * @param graph This may be a full graph or a subgraph. 
   * A subgraph may have some nodes whose child nodes are 
   * NOT part of that graph. The assumption is 
   * that such nodes have already been evaluated and their
   * values can already be found in the shared computation cache.
   * @return An object you can call get() on to wait for completion
   */
  Future<T> execute(DependencyGraph graph);

}
