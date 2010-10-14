/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view.calc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.opengamma.engine.depgraph.DependencyGraph;
import com.opengamma.engine.test.TestComputationResultListener;
import com.opengamma.engine.view.ViewImpl;
import com.opengamma.engine.view.ViewProcessorImpl;
import com.opengamma.engine.view.ViewProcessorTestEnvironment;
import com.opengamma.engine.view.calc.stats.GraphExecutorStatisticsGatherer;
import com.opengamma.engine.view.calcnode.CalculationJobResult;
import com.opengamma.engine.view.client.ViewClient;
import com.opengamma.util.test.Timeout;

/**
 * Tests SingleComputationCycle
 */
public class SingleComputationCycleTest {

  private static final long TIMEOUT = Timeout.standardTimeoutMillis();
  
  @Test
  public void testInterruptCycle() throws InterruptedException {
    ViewProcessorTestEnvironment env = new ViewProcessorTestEnvironment();
    BlockingDependencyGraphExecutorFactory dgef = new BlockingDependencyGraphExecutorFactory(TIMEOUT);
    env.setDependencyGraphExecutorFactory(dgef);
    env.init();
    
    ViewProcessorImpl vp = env.getViewProcessor();
    vp.start();
    
    ViewImpl view = (ViewImpl) vp.getView(env.getViewDefinition().getName(), ViewProcessorTestEnvironment.TEST_USER);
    view.start();
    view.init();
    
    ViewClient client = view.createClient(ViewProcessorTestEnvironment.TEST_USER);
    TestComputationResultListener resultListener = new TestComputationResultListener();
    client.setResultListener(resultListener);
    client.startLive();  // Performs an initial cycle
    
    BlockingDependencyGraphExecutor executor = dgef.getExecutorInstance();
    assertTrue (executor.awaitFirstRun(TIMEOUT));
    
    // We're now blocked in the execution of the initial cycle
    assertFalse(executor.wasInterrupted());
    
    // Interrupting should cause everything to terminate gracefully
    ViewRecalculationJob recalcJob = env.getCurrentRecalcJob(view);
    Thread recalcThread = env.getCurrentRecalcThread(view);
    recalcJob.terminate();
    recalcThread.interrupt();
    recalcThread.join(TIMEOUT);
    for (int i = 0; (i < TIMEOUT / 10) && !executor.wasInterrupted (); i++) {
      System.out.println ("waiting for executor interrupt");
      Thread.sleep (10);
    }
    assertTrue(executor.wasInterrupted());
  }
  
  private class BlockingDependencyGraphExecutorFactory implements DependencyGraphExecutorFactory<CalculationJobResult> {

    private final BlockingDependencyGraphExecutor _instance;
    
    public BlockingDependencyGraphExecutorFactory(long timeoutMillis) {
      _instance = new BlockingDependencyGraphExecutor(timeoutMillis);
    }
    
    @Override
    public DependencyGraphExecutor<CalculationJobResult> createExecutor(SingleComputationCycle cycle) {
      return _instance;
    }
    
    public BlockingDependencyGraphExecutor getExecutorInstance() {
      return _instance;
    }
    
  }
  
  private class BlockingDependencyGraphExecutor implements DependencyGraphExecutor<CalculationJobResult> {

    private final long _timeout;
    private final CountDownLatch _firstRunLatch = new CountDownLatch(1);
    private final AtomicBoolean _wasInterrupted = new AtomicBoolean ();
    
    public BlockingDependencyGraphExecutor(long timeoutMillis) {
      _timeout = timeoutMillis;
    }
    
    public boolean awaitFirstRun(long timeoutMillis) throws InterruptedException {
      return _firstRunLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
    
    public boolean wasInterrupted() {
      return _wasInterrupted.get ();
    }
    
    @Override
    public Future<CalculationJobResult> execute(DependencyGraph graph, GraphExecutorStatisticsGatherer statistics) {
      FutureTask<CalculationJobResult> future = new FutureTask<CalculationJobResult>(new Runnable() {
        @Override
        public void run() {
          _firstRunLatch.countDown();
          try {
            Thread.sleep(_timeout);
          } catch (InterruptedException e) {
            _wasInterrupted.set (true);
          }
        }
      }, null);
      
      // Cheat a bit - don't give the job to the dispatcher, etc, just run it.
      new Thread(future).start();
      return future;
    }
    
  }
  
}
