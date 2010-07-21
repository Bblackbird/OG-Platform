/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.position;

import com.opengamma.id.UniqueIdentifiable;
import com.opengamma.id.UniqueIdentifier;

/**
 * A portfolio of positions, typically having business-level meaning.
 * <p>
 * A portfolio is the primary element of business-level grouping within the source of positions.
 * It consists of a number of positions which are grouped using a flexible tree structure.
 * <p>
 * A portfolio typically has meta-data.
 */
public interface Portfolio extends UniqueIdentifiable {

  /**
   * Gets the unique identifier of the portfolio.
   * @return the identifier, not null
   */
  UniqueIdentifier getUniqueIdentifier();

  /**
   * Gets the name of the portfolio intended for display purposes.
   * @return the name, not null
   */
  String getName();

  /**
   * Gets the root node in the portfolio.
   * <p>
   * The positions stored in a portfolios are held in a tree structure.
   * This method accesses the root of the tree structure.
   * @return the root node of the tree structure, not null
   */
  PortfolioNode getRootNode();

}
