/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.batch;

import java.util.Date;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Hibernate bean.
 */
public class RiskFailure {
  
  private long _id;
  private int _calculationConfigurationId;
  private int _valueNameId;
  private int _valueConstraintsId;
  private int _functionUniqueId;
  private int _computationTargetId;
  private int _runId;
  private Date _evalInstant;
  private int _computeNodeId;
  
  public long getId() {
    return _id;
  }
  
  public void setId(long id) {
    _id = id;
  }
  
  public int getCalculationConfigurationId() {
    return _calculationConfigurationId;
  }
  
  public void setCalculationConfigurationId(int calculationConfigurationId) {
    _calculationConfigurationId = calculationConfigurationId;
  }
  
  public int getValueNameId() {
    return _valueNameId;
  }
  
  public void setValueNameId(int valueNameId) {
    _valueNameId = valueNameId;
  }
  
  public int getFunctionUniqueId() {
    return _functionUniqueId;
  }

  public void setFunctionUniqueId(int functionUniqueId) {
    _functionUniqueId = functionUniqueId;
  }

  public int getComputationTargetId() {
    return _computationTargetId;
  }
  
  public void setComputationTargetId(int computationTargetId) {
    _computationTargetId = computationTargetId;
  }
  
  public int getRunId() {
    return _runId;
  }
  
  public void setRunId(int runId) {
    _runId = runId;
  }
  
  public Date getEvalInstant() {
    return _evalInstant;
  }
  
  public void setEvalInstant(Date evalInstant) {
    _evalInstant = evalInstant;
  }
  
  public int getComputeNodeId() {
    return _computeNodeId;
  }
  
  public void setComputeNodeId(int computeNodeId) {
    _computeNodeId = computeNodeId;
  }

  public int getValueConstraintsId() {
    return _valueConstraintsId;
  }

  public void setValueConstraintsId(int valueConstraintsId) {
    this._valueConstraintsId = valueConstraintsId;
  }

  public SqlParameterSource toSqlParameterSource() {
    MapSqlParameterSource source = new MapSqlParameterSource();
    source.addValue("id", getId());   
    source.addValue("calculation_configuration_id", getCalculationConfigurationId());
    source.addValue("value_name_id", getValueNameId());
    source.addValue("value_constraints_id", getValueConstraintsId());
    source.addValue("function_unique_id", getFunctionUniqueId());
    source.addValue("computation_target_id", getComputationTargetId());
    source.addValue("run_id", getRunId());
    source.addValue("eval_instant", getEvalInstant());
    source.addValue("compute_node_id", getComputeNodeId());
    return source;
  }
  
  public static String sqlDeleteRiskFailures() {
    return "DELETE FROM " + DbBatchMaster.getDatabaseSchema() + "rsk_failure WHERE run_id = :run_id";
  }
  
  public static String sqlInsertRiskFailure() {
    return "INSERT INTO " + DbBatchMaster.getDatabaseSchema() + "rsk_failure " +
              "(id, calculation_configuration_id, value_name_id, value_constraints_id, function_unique_id, computation_target_id, " +
              "run_id, eval_instant, compute_node_id) " +
            "VALUES " +
              "(:id, :calculation_configuration_id, :value_name_id, :value_constraints_id, :function_unique_id, :computation_target_id, " +
              ":run_id, :eval_instant, :compute_node_id)";
  }
  
  public static String sqlCount() {
    return "SELECT COUNT(*) FROM " + DbBatchMaster.getDatabaseSchema() + "rsk_failure";
  }

}
