/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.opengamma.engine.position.MockPositionSource;
import com.opengamma.engine.position.Portfolio;
import com.opengamma.engine.position.PortfolioImpl;
import com.opengamma.engine.position.PortfolioNodeImpl;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.position.PositionImpl;
import com.opengamma.engine.position.PositionSource;
import com.opengamma.engine.security.DefaultSecurity;
import com.opengamma.engine.security.MockSecuritySource;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Test DefaultComputationTargetResolver.
 */
public class DefaultComputationTargetResolverTest {

  private static final Portfolio PORTFOLIO = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
  private static final PortfolioNodeImpl NODE = new PortfolioNodeImpl(UniqueIdentifier.of("A", "B"), "Name");
  private static final Position POSITION = new PositionImpl(UniqueIdentifier.of("Test", "1"), new BigDecimal(1), new IdentifierBundle());
  private static final DefaultSecurity SECURITY = new DefaultSecurity(UniqueIdentifier.of("Test", "SEC"), "Test security", "EQUITY", new IdentifierBundle());

  //-------------------------------------------------------------------------
  @Test
  public void test_constructor() {
    SecuritySource secSource = new MockSecuritySource();
    PositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    assertEquals(secSource, test.getSecuritySource());
    assertEquals(posSource, test.getPositionSource());
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_constructor_nullSecuritySource() {
    SecuritySource secSource = new MockSecuritySource();
    new DefaultComputationTargetResolver(secSource, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void test_constructor_nullPositionSource() {
    PositionSource posSource = new MockPositionSource();
    new DefaultComputationTargetResolver(null, posSource);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_resolve_portfolio() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    posSource.addPortfolio(PORTFOLIO);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(PORTFOLIO);
    ComputationTarget expected = new ComputationTarget(PORTFOLIO);
    assertEquals(expected, test.resolve(spec));
  }

  @Test
  public void test_resolve_portfolioNode() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    PortfolioImpl p = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
    p.getRootNode().addChildNode(NODE);
    posSource.addPortfolio(p);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(NODE);
    ComputationTarget expected = new ComputationTarget(NODE);
    assertEquals(expected, test.resolve(spec));
  }

  @Test
  public void test_resolve_position() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    PortfolioImpl p = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
    p.getRootNode().addPosition(POSITION);
    posSource.addPortfolio(p);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(POSITION);
    ComputationTarget expected = new ComputationTarget(POSITION);
    assertEquals(expected, test.resolve(spec));
  }

  @Test
  public void test_resolve_security() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    secSource.addSecurity(SECURITY);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(SECURITY);
    ComputationTarget expected = new ComputationTarget(SECURITY);
    assertEquals(expected, test.resolve(spec));
  }

  @Test
  public void test_resolve_primitive() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(ComputationTargetType.PRIMITIVE, (UniqueIdentifier) null);
    ComputationTarget expected = new ComputationTarget(ComputationTargetType.PRIMITIVE, null);
    assertEquals(expected, test.resolve(spec));
  }

  @Test(expected=NullPointerException.class)
  public void test_resolve_nullSpecification() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    test.resolve(null);
  }

}
