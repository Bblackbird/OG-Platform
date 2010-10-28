/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.compilation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.opengamma.engine.depgraph.DependencyGraphBuilder;
import com.opengamma.engine.position.AbstractPortfolioNodeTraversalCallback;
import com.opengamma.engine.position.PortfolioNode;
import com.opengamma.engine.position.PortfolioNodeTraverser;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.view.ResultModelDefinition;
import com.opengamma.engine.view.ResultOutputMode;
import com.opengamma.engine.view.ViewCalculationConfiguration;

/**
 * Compiles dependency graphs for each stage in a portfolio tree.
 */
/*package*/ class PortfolioCompilerTraversalCallback extends AbstractPortfolioNodeTraversalCallback {
  private final DependencyGraphBuilder _dependencyGraphBuilder;
  private final ViewCalculationConfiguration _calculationConfiguration;
  private final ResultModelDefinition _resultModelDefinition;
  
  public PortfolioCompilerTraversalCallback(DependencyGraphBuilder dependencyGraphBuilder, ViewCalculationConfiguration calculationConfiguration) {
    _dependencyGraphBuilder = dependencyGraphBuilder;
    _calculationConfiguration = calculationConfiguration;
    _resultModelDefinition = calculationConfiguration.getViewDefinition().getResultModelDefinition();
  }
  
  /**
   * Gathers all security types. 
   */
  protected static class SubNodeSecurityTypeAccumulator extends AbstractPortfolioNodeTraversalCallback {
    
    private final Set<String> _subNodeSecurityTypes = new TreeSet<String>();

    /**
     * @return the subNodeSecurityTypes
     */
    public Set<String> getSubNodeSecurityTypes() {
      return _subNodeSecurityTypes;
    }

    @Override
    public void preOrderOperation(Position position) {
      _subNodeSecurityTypes.add(position.getSecurity().getSecurityType());
    }
    
  }
  
  /**
   * @param portfolioNode
   * @return
   */
  private static Set<String> getSubNodeSecurityTypes(PortfolioNode portfolioNode) {
    SubNodeSecurityTypeAccumulator accumulator = new SubNodeSecurityTypeAccumulator();
    new PortfolioNodeTraverser(accumulator).traverse(portfolioNode);
    return accumulator.getSubNodeSecurityTypes();
  }

  @Override
  public void preOrderOperation(PortfolioNode portfolioNode) {
    Set<String> subNodeSecurityTypes = getSubNodeSecurityTypes(portfolioNode);
    Map<String, Set<String>> outputsBySecurityType = _calculationConfiguration.getPortfolioRequirementsBySecurityType();
    for (String secType : subNodeSecurityTypes) {
      Set<String> requiredOutputs = outputsBySecurityType.get(secType);
      if ((requiredOutputs == null) || requiredOutputs.isEmpty()) {
        continue;
      }
      Set<ValueRequirement> requirements = new HashSet<ValueRequirement>();
      // If the outputs are not even required in the results then there's no point adding them as terminal outputs
      if (_resultModelDefinition.getAggregatePositionOutputMode() != ResultOutputMode.NONE) {
        for (String requiredOutput : requiredOutputs) {
          requirements.add(new ValueRequirement(requiredOutput, portfolioNode));
        }
        _dependencyGraphBuilder.addTarget(requirements);
      }
      if (_resultModelDefinition.getPositionOutputMode() != ResultOutputMode.NONE) {
        for (Position position : portfolioNode.getPositions()) {
          requirements.clear();
          for (String requiredOutput : requiredOutputs) {
            requirements.add(new ValueRequirement(requiredOutput, position));
          }
          _dependencyGraphBuilder.addTarget(requirements);
        }
      }
    }
  }
      
}
