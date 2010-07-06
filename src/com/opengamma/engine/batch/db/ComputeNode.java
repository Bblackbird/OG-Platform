/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.batch.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 */
public class ComputeNode {
  
  private int _id;
  private String _configOid;
  private int _configVersion;
  private ComputeHost _computeHost;
  private String _nodeName;
  
  public int getId() {
    return _id;
  }
  
  public void setId(int id) {
    _id = id;
  }
  
  public String getConfigOid() {
    return _configOid;
  }
  
  public void setConfigOid(String configOid) {
    _configOid = configOid;
  }
  
  public int getConfigVersion() {
    return _configVersion;
  }
  
  public void setConfigVersion(int configVersion) {
    _configVersion = configVersion;
  }
  
  public ComputeHost getComputeHost() {
    return _computeHost;
  }
  
  public void setComputeHost(ComputeHost computeHost) {
    _computeHost = computeHost;
  }
  
  public String getNodeName() {
    return _nodeName;
  }
  
  public void setNodeName(String nodeName) {
    _nodeName = nodeName;
  }
  
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
    return ToStringBuilder.reflectionToString(this);
  }

}
