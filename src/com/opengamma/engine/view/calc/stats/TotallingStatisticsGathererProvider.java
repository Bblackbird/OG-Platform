/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */

package com.opengamma.engine.view.calc.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.opengamma.engine.view.View;

/**
 * Maintains ever increasing tallies.
 */
public class TotallingStatisticsGathererProvider extends PerViewStatisticsGathererProvider {

  /**
   * 
   */
  public static final class Statistics implements GraphExecutorStatisticsGatherer {

    private final String _viewName;
    private final ConcurrentMap<String, GraphExecutionStatistics> _statistics = new ConcurrentHashMap<String, GraphExecutionStatistics>();

    private Statistics(final String viewName) {
      _viewName = viewName;
    }

    protected GraphExecutionStatistics getOrCreateConfiguration(final String calcConfig) {
      GraphExecutionStatistics stats = _statistics.get(calcConfig);
      if (stats == null) {
        stats = new GraphExecutionStatistics(_viewName, calcConfig);
        final GraphExecutionStatistics newStats = _statistics.putIfAbsent(calcConfig, stats);
        if (newStats != null) {
          stats = newStats;
        }
      }
      return stats;
    }

    @Override
    public void graphExecuted(String calcConfig, int nodeCount, long executionTime, long duration) {
      getOrCreateConfiguration(calcConfig).recordExecution(nodeCount, executionTime, duration);
    }

    @Override
    public void graphProcessed(String calcConfig, int totalJobs, double meanJobSize, double meanJobCycleCost) {
      getOrCreateConfiguration(calcConfig).recordProcessing(totalJobs, meanJobSize, meanJobCycleCost);
    }

    public List<GraphExecutionStatistics> getExecutionStatistics() {
      return new ArrayList<GraphExecutionStatistics>(_statistics.values());
    }

  }

  @Override
  protected GraphExecutorStatisticsGatherer createStatisticsGatherer(final View view) {
    return new Statistics(view.getName());
  }

}
