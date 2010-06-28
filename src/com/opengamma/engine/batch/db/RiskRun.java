/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.batch.db;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.opengamma.engine.view.ViewCalculationConfiguration;

/**
 * 
 */
public class RiskRun {
  
  private int _id;
  private OpenGammaVersion _openGammaVersion;
  private ComputeHost _masterProcessHost;
  private String _runReason;
  private ObservationDateTime _runTime;
  private Timestamp _valuationTime;
  private int _viewOid;
  private int _viewVersion;
  private LiveDataSnapshot _liveDataSnapshot;
  private Timestamp _createInstant;
  private Timestamp _startInstant;
  private Timestamp _endInstant;
  private Set<CalculationConfiguration> _calculationConfigurations = new HashSet<CalculationConfiguration>();
  private boolean _complete;
  
  public int getId() {
    return _id;
  }
  
  public void setId(int id) {
    _id = id;
  }
  
  public OpenGammaVersion getOpenGammaVersion() {
    return _openGammaVersion;
  }
  
  public void setOpenGammaVersion(OpenGammaVersion openGammaVersion) {
    _openGammaVersion = openGammaVersion;
  }
  
  public ComputeHost getMasterProcessHost() {
    return _masterProcessHost;
  }
  
  public void setMasterProcessHost(ComputeHost masterProcessHost) {
    _masterProcessHost = masterProcessHost;
  }
  
  public String getRunReason() {
    return _runReason;
  }
  
  public void setRunReason(String runReason) {
    _runReason = runReason;
  }
  
  public ObservationDateTime getRunTime() {
    return _runTime;
  }
  
  public void setRunTime(ObservationDateTime runTime) {
    _runTime = runTime;
  }
  
  public Timestamp getValuationTime() {
    return _valuationTime;
  }
  
  public void setValuationTime(Timestamp valuationTime) {
    _valuationTime = valuationTime;
  }
  
  public int getViewOid() {
    return _viewOid;
  }
  
  public void setViewOid(int viewOid) {
    _viewOid = viewOid;
  }
  
  public int getViewVersion() {
    return _viewVersion;
  }
  
  public void setViewVersion(int viewVersion) {
    _viewVersion = viewVersion;
  }
  
  public LiveDataSnapshot getLiveDataSnapshot() {
    return _liveDataSnapshot;
  }
  
  public void setLiveDataSnapshot(LiveDataSnapshot liveDataSnapshot) {
    _liveDataSnapshot = liveDataSnapshot;
  }
  
  public Timestamp getCreateInstant() {
    return _createInstant;
  }
  
  public void setCreateInstant(Timestamp createInstant) {
    _createInstant = createInstant;
  }
  
  public Timestamp getStartInstant() {
    return _startInstant;
  }
  
  public void setStartInstant(Timestamp startInstant) {
    _startInstant = startInstant;
  }
  
  public Timestamp getEndInstant() {
    return _endInstant;
  }
  
  public void setEndInstant(Timestamp endInstant) {
    _endInstant = endInstant;
  }
  
  public Set<CalculationConfiguration> getCalculationConfigurations() {
    return _calculationConfigurations;
  }

  public void setCalculationConfigurations(Set<CalculationConfiguration> calculationConfigurations) {
    _calculationConfigurations = calculationConfigurations;
  }

  public boolean isComplete() {
    return _complete;
  }
  
  public void setComplete(boolean complete) {
    _complete = complete;
  }
  
  // --------------------------------------------------------------------------
  
  public void addCalculationConfiguration(ViewCalculationConfiguration viewCalcConf) {
    CalculationConfiguration calcConf = new CalculationConfiguration();
    calcConf.setName(viewCalcConf.getName());
    calcConf.setRiskRun(this);
    _calculationConfigurations.add(calcConf);
  }
  
  // --------------------------------------------------------------------------
  
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
  @Override
  public String toString() {
    return "RiskRun[id=" + getId() + "]";
  }
  
}
