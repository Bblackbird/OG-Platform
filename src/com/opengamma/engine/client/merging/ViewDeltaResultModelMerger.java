/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.client.merging;

import java.util.Map;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.view.ViewCalculationResultModel;
import com.opengamma.engine.view.ViewDeltaResultModel;
import com.opengamma.engine.view.ViewDeltaResultModelImpl;

/**
 * Provides the ability to merge {@link ViewDeltaResultModel} instances.
 */
public class ViewDeltaResultModelMerger implements IncrementalMerger<ViewDeltaResultModel> {

  private ViewDeltaResultModelImpl _currentMergedResult;
  
  @Override
  public void merge(ViewDeltaResultModel newResult) {
    if (_currentMergedResult == null) {
      // Start of a new result
      _currentMergedResult = new ViewDeltaResultModelImpl();
      _currentMergedResult.setPreviousResultTimestamp(newResult.getPreviousResultTimestamp());
      _currentMergedResult.setCalculationConfigurationNames(newResult.getCalculationConfigurationNames());
    }
    _currentMergedResult.setValuationTime(newResult.getValuationTime());
    _currentMergedResult.setResultTimestamp(newResult.getResultTimestamp());
    _currentMergedResult.ensureCalculationConfigurationNames(newResult.getCalculationConfigurationNames());
    
    for (ComputationTargetSpecification targetSpec : newResult.getAllTargets()) {
      for (String calcConfigName : newResult.getCalculationConfigurationNames()) {
        ViewCalculationResultModel resultCalcModel = newResult.getCalculationResult(calcConfigName);
        Map<String, ComputedValue> resultValues = resultCalcModel.getValues(targetSpec);
        if (resultValues == null) {
          continue;
        }
        for (Map.Entry<String, ComputedValue> resultEntry : resultValues.entrySet()) {
          _currentMergedResult.addValue(calcConfigName, resultEntry.getValue());
        }
      }
    }
  }

  @Override
  public ViewDeltaResultModel consume() {
    ViewDeltaResultModel result = _currentMergedResult;
    // This is a delta merger so now that we've consumed the latest deltas, the next delta should start empty
    _currentMergedResult = null;
    return result;
  }

}
