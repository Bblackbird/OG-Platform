/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.opengamma.engine.position.MockPositionSource;
import com.opengamma.engine.position.PortfolioImpl;
import com.opengamma.engine.position.PortfolioNodeImpl;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.position.PositionImpl;
import com.opengamma.id.Identifier;
import com.opengamma.id.UniqueIdentifierSupplier;

/**
 * Tests for the {@link PortfolioStructure} class. 
 */
public class PortfolioStructureTest {

  private FunctionCompilationContext _context;
  private PortfolioNodeImpl _root;
  private PortfolioNodeImpl _child1;
  private PortfolioNodeImpl _child2;
  private PortfolioNodeImpl _badChild;
  private PositionImpl _position1;
  private PositionImpl _position2;
  private PositionImpl _badPosition;
  
  @Before
  public void createPortfolio() {
    final UniqueIdentifierSupplier uid = new UniqueIdentifierSupplier("Test");
    final MockPositionSource positionSource = new MockPositionSource();
    final PortfolioStructure resolver = new PortfolioStructure(positionSource);
    final PortfolioImpl portfolio = new PortfolioImpl(uid.get(), "Test");
    _root = new PortfolioNodeImpl(uid.get(), "root");
    _child1 = new PortfolioNodeImpl(uid.get(), "child 1");
    _child2 = new PortfolioNodeImpl(uid.get(), "child 2");
    _position1 = new PositionImpl(uid.get(), new BigDecimal(10), Identifier.of("Security", "Foo"));
    _position1.setPortfolioNode(_child2.getUniqueIdentifier());
    _child2.addPosition(_position1);
    _position2 = new PositionImpl(uid.get(), new BigDecimal(20), Identifier.of("Security", "Bar"));
    _position2.setPortfolioNode(_child2.getUniqueIdentifier());
    _child2.addPosition(_position2);
    _child2.setParentNode(_child1.getUniqueIdentifier());
    _child1.addChildNode(_child2);
    _child1.setParentNode(_root.getUniqueIdentifier());
    _root.addChildNode(_child1);
    portfolio.setRootNode(_root);
    positionSource.addPortfolio(portfolio);
    _badChild = new PortfolioNodeImpl(uid.get(), "child 3");
    _badChild.setParentNode(uid.get());
    _badPosition = new PositionImpl(uid.get(), new BigDecimal(10), Identifier.of("Security", "Cow"));
    _badPosition.setPortfolioNode(uid.get());
    _context = new FunctionCompilationContext();
    _context.setPortfolioStructure(resolver);
  }

  private PortfolioStructure getPortfolioStructure() {
    return _context.getPortfolioStructure();
  }

  @Test
  public void testGetParentNode_portfolioNode() {
    final PortfolioStructure resolver = getPortfolioStructure();
    assertNotNull(resolver);
    assertEquals(_child1, resolver.getParentNode(_child2));
    assertEquals(_root, resolver.getParentNode(_child1));
    assertEquals(null, resolver.getParentNode(_root));
    assertNull(resolver.getParentNode(_badChild));
  }

  @Test
  public void testGetParentNode_position() {
    final PortfolioStructure resolver = getPortfolioStructure();
    assertNotNull(resolver);
    assertEquals(_child2, resolver.getParentNode(_position1));
    assertEquals(_child2, resolver.getParentNode(_position2));
    assertNull(resolver.getParentNode(_badPosition));
  }

  @Test
  public void testGetRootPortfolioNode_portfolioNode() {
    final PortfolioStructure resolver = getPortfolioStructure();
    assertNotNull(resolver);
    assertEquals(_root, resolver.getRootPortfolioNode(_child1));
    assertEquals(_root, resolver.getRootPortfolioNode(_child2));
    assertEquals(_root, resolver.getRootPortfolioNode(_root));
    assertNull(resolver.getRootPortfolioNode(_badChild));
  }

  @Test
  public void testGetRootPortfolioNode_position() {
    final PortfolioStructure resolver = getPortfolioStructure();
    assertNotNull(resolver);
    assertEquals(_root, resolver.getRootPortfolioNode(_position1));
    assertEquals(_root, resolver.getRootPortfolioNode(_position2));
    assertNull(resolver.getRootPortfolioNode(_badPosition));
  }
  
  @Test
  public void testGetAllPositions () {
    final PortfolioStructure resolver = getPortfolioStructure ();
    assertNotNull(resolver);
    List<Position> positions = resolver.getAllPositions(_child1);
    assertNotNull (positions);
    assertEquals (2, positions.size ());
    assertTrue (positions.contains (_position1));
    assertTrue (positions.contains (_position2));
    positions = resolver.getAllPositions (_root);
    assertNotNull (positions);
    assertEquals (2, positions.size ());
    assertTrue (positions.contains (_position1));
    assertTrue (positions.contains (_position2));
    positions = resolver.getAllPositions (_child2);
    assertNotNull (positions);
    assertEquals (2, positions.size ());
    assertTrue (positions.contains (_position1));
    assertTrue (positions.contains (_position2));
    positions = resolver.getAllPositions(_badChild);
    assertNotNull (positions);
    assertTrue (positions.isEmpty ());
  }

}
