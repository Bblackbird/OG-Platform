/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.test;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Objects;
import com.opengamma.core.security.Security;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueId;

/**
 * Mock Security.
 */
public class MockSecurity implements Security, MutableUniqueIdentifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private UniqueId _uniqueId;
  private String _securityType;
  private String _name;
  private ExternalIdBundle _identifiers;
  private Map<String, String> _attributes;

  /**
   * Creates an instance.
   * @param securityType  the security type
   */
  public MockSecurity(String securityType) {
    _securityType = securityType;
    _identifiers = ExternalIdBundle.EMPTY;
  }

  public MockSecurity(UniqueId uniqueId, String name, String securityType, ExternalIdBundle identifiers) {
    _uniqueId = uniqueId;
    _name = name;
    _securityType = securityType;
    _identifiers = identifiers;
  }

  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  public void setUniqueId(UniqueId uniqueId) {
    _uniqueId = uniqueId;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getSecurityType() {
    return _securityType;
  }

  public void setSecurityType(String securityType) {
    _securityType = securityType;
  }

  public ExternalIdBundle getExternalIdBundle() {
    return _identifiers;
  }

  public void setIdentifiers(ExternalIdBundle identifiers) {
    _identifiers = identifiers;
  }

  public void addIdentifier(final ExternalId identifier) {
    setIdentifiers(getExternalIdBundle().withExternalId(identifier));
  }

  @Override
  public Map<String, String> getAttributes() {
    return _attributes;
  }

  @Override
  public void setAttributes(Map<String, String> attributes) {
    this._attributes = attributes;
  }

  @Override
  public void addAttribute(String key, String value) {
    this._attributes.put(key, value);
  }

  //-------------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof MockSecurity) {
      MockSecurity other = (MockSecurity) obj;
      return Objects.equal(_uniqueId, other._uniqueId) &&
        Objects.equal(_securityType, other._securityType) &&
        Objects.equal(_name, other._name) &&
        Objects.equal(_identifiers, other._identifiers);
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_identifiers == null) ? 0 : _identifiers.hashCode());
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    result = prime * result + ((_securityType == null) ? 0 : _securityType.hashCode());
    result = prime * result + ((_uniqueId == null) ? 0 : _uniqueId.hashCode());
    return result;
  }

}
