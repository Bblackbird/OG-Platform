/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.position;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.opengamma.util.ArgumentChecker;

/**
 * Recursively loads all positions under a particular {@link PortfolioNode}.
 */
public class PositionAccumulator {

  /**
   * Gets all the positions beneath the starting node.
   * @param startNode  the starting node, not null
   * @return All positions accumulated during execution
   */
  public static Set<Position> getAccumulatedPositions(PortfolioNode startNode) {
    return new PositionAccumulator(startNode).getPositions();
  }

  //-------------------------------------------------------------------------
  /**
   * The set of positions.
   */
  private final Set<Position> _positions = new HashSet<Position>();

  /**
   * Creates an accumulator starting from the specified node.
   * @param startNode  the starting node, not null
   */
  public PositionAccumulator(PortfolioNode startNode) {
    ArgumentChecker.notNull(startNode, "Portfolio Node");
    new PortfolioNodeTraverser(new Callback()).traverse(startNode);
  }

  /**
   * Gets the positions that were found.
   * @return the positions, not null
   */
  public Set<Position> getPositions() {
    return Collections.unmodifiableSet(_positions);
  }

  /**
   * Callback to match the positions.
   */
  private class Callback extends AbstractPortfolioNodeTraversalCallback {
    @Override
    public void preOrderOperation(Position position) {
      _positions.add(position);
    }
  }

}
