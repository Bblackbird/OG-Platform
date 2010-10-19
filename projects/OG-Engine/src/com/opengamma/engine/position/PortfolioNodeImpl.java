/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;

/**
 * A simple mutable implementation of {@code PortfolioNode}.
 */
public class PortfolioNodeImpl implements PortfolioNode, MutableUniqueIdentifiable, Serializable {

  /**
   * The identifier of the node.
   */
  private UniqueIdentifier _identifier;
  /**
   * Identifier of the parent node
   */
  private UniqueIdentifier _parentNode;
  /**
   * The name.
   */
  private String _name;
  /**
   * The list of child nodes.
   */
  private final List<PortfolioNode> _childNodes = new ArrayList<PortfolioNode>();
  /**
   * The list of child positions.
   */
  private final List<Position> _positions = new ArrayList<Position>();

  /**
   * Creates a portfolio node with an empty name.
   */
  public PortfolioNodeImpl() {
    _name = "";
  }

  /**
   * Creates a portfolio node with a given name.
   * @param name the name of the portfolio node.
   */
  public PortfolioNodeImpl(String name) {
    _name = name;
  }

  /**
   * Creates a portfolio with the specified identifier.
   * @param identifier  the portfolio identifier, not null
   * @param name  the name to use, null treated as empty
   */
  public PortfolioNodeImpl(UniqueIdentifier identifier, String name) {
    ArgumentChecker.notNull(identifier, "identifier");
    _identifier = identifier;
    _name = StringUtils.defaultString(name);
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the unique identifier of the node.
   * @return the identifier, not null
   */
  @Override
  public UniqueIdentifier getUniqueIdentifier() {
    return _identifier;
  }

  /**
   * Sets the unique identifier of the node.
   * @param identifier  the new identifier, not null
   */
  public void setUniqueIdentifier(UniqueIdentifier identifier) {
    ArgumentChecker.notNull(identifier, "identifier");
    _identifier = identifier;
  }

  @Override
  public UniqueIdentifier getParentNode() {
    return _parentNode;
  }

  public void setParentNode(final UniqueIdentifier parentNode) {
    _parentNode = parentNode;
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the name of the node intended for display purposes.
   * @return the name, not null
   */
  @Override
  public String getName() {
    return _name;
  }

  /**
   * Sets the name of the node intended for display purposes.
   * @param name  the name, not empty, not null
   */
  public void setName(String name) {
    ArgumentChecker.notEmpty(name, "name");
    _name = name;
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the total size of the children, including nodes and positions.
   * @return the size of the nodes and position
   */
  @Override
  public int size() {
    return _childNodes.size() + _positions.size();
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the nodes which are immediate children of this node.
   * @return the child nodes, unmodifiable, not null
   */
  @Override
  public List<PortfolioNode> getChildNodes() {
    return Collections.unmodifiableList(_childNodes);
  }

  /**
   * Adds a node to the list of immediate children.
   * @param childNode  the child node to add, not null
   */
  public void addChildNode(PortfolioNode childNode) {
    ArgumentChecker.notNull(childNode, "child node");
    _childNodes.add(childNode);
  }

  /**
   * Adds a collection of nodes to the list of immediate children.
   * @param childNodes the child nodes to add, not null
   */
  public void addChildNodes(Collection<? extends PortfolioNode> childNodes) {
    ArgumentChecker.noNulls(childNodes, "child node");
    _childNodes.addAll(childNodes);
  }

  /**
   * Removes a node from the list of immediate children.
   * @param childNode  the child node to remove, not null
   */
  public void removeChildNode(PortfolioNode childNode) {
    _childNodes.remove(childNode);
  }

  // -------------------------------------------------------------------------
  /**
   * Gets the positions which are immediate children of this node.
   * @return the positions, unmodifiable, not null
   */
  @Override
  public List<Position> getPositions() {
    return Collections.unmodifiableList(_positions);
  }

  /**
   * Adds a node to the list of immediate children.
   * @param position  the position to add, not null
   */
  public void addPosition(Position position) {
    ArgumentChecker.notNull(position, "child node");
    _positions.add(position);
  }

  /**
   * Adds a collection of nodes to the list of immediate children.
   * @param positions the positions to add, not null
   */
  public void addPositions(Collection<? extends Position> positions) {
    ArgumentChecker.noNulls(positions, "position");
    _positions.addAll(positions);
  }

  /**
   * Removes a position from the list.
   * @param position  the position to remove, not null
   */
  public void removePosition(Position position) {
    _positions.remove(position);
  }

  // -------------------------------------------------------------------------
  /**
   * Recursively finds a specific node from this node by identifier.
   * If this node matches it is returned.
   * @param uid  the identifier, null returns null
   * @return the node, null if not found
   */
  public PortfolioNode getNode(UniqueIdentifier uid) {
    return getNode(this, uid);
  }

  /**
   * Recursively finds a specific node from a node by identifier.
   * @param node  the node to process, not null
   * @param uid  the identifier, null returns null
   * @return the node, null if not found
   */
  private static PortfolioNode getNode(PortfolioNode node, UniqueIdentifier uid) {
    if (uid != null) {
      if (uid.equals(node.getUniqueIdentifier())) {
        return node;
      }
      for (PortfolioNode child : node.getChildNodes()) {
        PortfolioNode result = getNode(child, uid);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  /**
   * Recursively finds a specific position from this node by identifier.
   * @param uid  the identifier, null returns null
   * @return the position, null if not found
   */
  public Position getPosition(UniqueIdentifier uid) {
    return getPosition(this, uid);
  }

  /**
   * Recursively finds a specific position from a node by identifier.
   * @param node  the node to process, not null
   * @param uid  the identifier, null returns null
   * @return the position, null if not found
   */
  private static Position getPosition(PortfolioNode node, UniqueIdentifier uid) {
    if (uid != null) {
      for (Position child : node.getPositions()) {
        if (uid.equals(child.getUniqueIdentifier())) {
          return child;
        }
      }
      for (PortfolioNode child : node.getChildNodes()) {
        Position result = getPosition(child, uid);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  public String toLongString() {
    StringBuffer childString = new StringBuffer();
    childString.append("[");
    for (int i = 0; i < _childNodes.size(); i++) {
      PortfolioNode child = _childNodes.get(i);
      if (child instanceof PortfolioNodeImpl) {
        childString.append(((PortfolioNodeImpl) child).toLongString());
      } else {
        childString.append(child.toString());
      }
      if (i != _childNodes.size() - 1) {
        childString.append(",");
      }
    }
    childString.append("]");
    return new StrBuilder().append("PortfolioNode[uniqueIdentifier=").append(getUniqueIdentifier()).append(",childNodes=").append(childString).append(",positions=").append(_positions).append("]")
        .toString();
  }

  // -------------------------------------------------------------------------
  @Override
  public String toString() {
    return new StrBuilder().append("PortfolioNode[").append(getUniqueIdentifier()).append(", ").append(_childNodes.size()).append(" child-nodes, ").append(_positions.size()).append(" positions]")
        .toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof PortfolioNodeImpl)) {
      return false;
    }
    final PortfolioNodeImpl other = (PortfolioNodeImpl) o;
    if (!ObjectUtils.equals(getUniqueIdentifier(), other.getUniqueIdentifier()) || !ObjectUtils.equals(getName(), other.getName())) {
      return false;
    }
    final List<PortfolioNode> otherChildNodes = other.getChildNodes();
    final List<Position> otherPositions = other.getPositions();
    if (getChildNodes().size() != otherChildNodes.size()) {
      return false;
    }
    if (getPositions().size() != otherPositions.size()) {
      return false;
    }
    for (PortfolioNode node : getChildNodes()) {
      if (!otherChildNodes.contains(node)) {
        return false;
      }
    }
    for (Position position : getPositions()) {
      if (!otherPositions.contains(position)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 0;
    int prime = 31;
    if (getUniqueIdentifier() != null) {
      result += getUniqueIdentifier().hashCode();
    }
    result *= prime;
    if (getName() != null) {
      result += getName().hashCode();
    }
    result *= prime;
    if (getParentNode() != null) {
      result += getParentNode().hashCode();
    }
    // Intentionally skip the contained children and positions
    return result;
  }

}
