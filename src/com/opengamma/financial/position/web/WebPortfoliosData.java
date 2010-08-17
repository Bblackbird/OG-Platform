/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.position.web;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.financial.position.master.ManageablePortfolioNode;
import com.opengamma.financial.position.master.PortfolioTreeDocument;
import com.opengamma.financial.position.master.PositionDocument;
import com.opengamma.financial.position.master.PositionMaster;
import com.opengamma.id.UniqueIdentifier;

/**
 * Data class for web-based portfolios.
 */
@BeanDefinition
public class WebPortfoliosData extends DirectBean {

  /**
   * The position master.
   */
  @PropertyDefinition
  private PositionMaster _positionMaster;
  /**
   * The JSR-311 URI information.
   */
  @PropertyDefinition
  private UriInfo _uriInfo;
  /**
   * The portfolio id from the input URI.
   */
  @PropertyDefinition
  private String _uriPortfolioId;
  /**
   * The node id from the input URI.
   */
  @PropertyDefinition
  private String _uriNodeId;
  /**
   * The position id from the input URI.
   */
  @PropertyDefinition
  private String _uriPositionId;
  /**
   * The version id from the URI.
   */
  @PropertyDefinition
  private String _uriVersionId;
  /**
   * The portfolio.
   */
  @PropertyDefinition
  private PortfolioTreeDocument _portfolio;
  /**
   * The parent node.
   */
  @PropertyDefinition
  private ManageablePortfolioNode _parentNode;
  /**
   * The node.
   */
  @PropertyDefinition
  private ManageablePortfolioNode _node;
  /**
   * The portfolio.
   */
  @PropertyDefinition
  private PositionDocument _position;

  //-------------------------------------------------------------------------
  /**
   * Gets the best available portfolio id.
   * @param overrideId  the override id, null derives the result from the data
   * @return the id, may be null
   */
  public String getBestPortfolioUriId(final UniqueIdentifier overrideId) {
    if (overrideId != null) {
      return overrideId.toLatest().toString();
    }
    if (getPosition() != null) {
      return getPosition().getPortfolioId().toLatest().toString();
    }
    return getPortfolio() != null ? getPortfolio().getPortfolioId().toLatest().toString() : getUriPortfolioId();
  }

  /**
   * Gets the best available node id.
   * @param overrideId  the override id, null derives the result from the data
   * @return the id, may be null
   */
  public String getBestNodeUriId(final UniqueIdentifier overrideId) {
    if (overrideId != null) {
      return overrideId.toLatest().toString();
    }
    if (getPosition() != null) {
      return getPosition().getParentNodeId().toLatest().toString();
    }
    return getNode() != null ? getNode().getUniqueIdentifier().toLatest().toString() : getUriNodeId();
  }

  /**
   * Gets the best available position id.
   * @param overrideId  the override id, null derives the result from the data
   * @return the id, may be null
   */
  public String getBestPositionUriId(final UniqueIdentifier overrideId) {
    if (overrideId != null) {
      return overrideId.toLatest().toString();
    }
    return getPosition() != null ? getPosition().getPositionId().toLatest().toString() : getUriPositionId();
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code WebPortfoliosData}.
   * @return the meta-bean, not null
   */
  public static WebPortfoliosData.Meta meta() {
    return WebPortfoliosData.Meta.INSTANCE;
  }

  @Override
  public WebPortfoliosData.Meta metaBean() {
    return WebPortfoliosData.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -1840419605:  // positionMaster
        return getPositionMaster();
      case -173275078:  // uriInfo
        return getUriInfo();
      case -72522889:  // uriPortfolioId
        return getUriPortfolioId();
      case 1130377033:  // uriNodeId
        return getUriNodeId();
      case 1240319664:  // uriPositionId
        return getUriPositionId();
      case 666567687:  // uriVersionId
        return getUriVersionId();
      case 1121781064:  // portfolio
        return getPortfolio();
      case -244857396:  // parentNode
        return getParentNode();
      case 3386882:  // node
        return getNode();
      case 747804969:  // position
        return getPosition();
    }
    return super.propertyGet(propertyName);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -1840419605:  // positionMaster
        setPositionMaster((PositionMaster) newValue);
        return;
      case -173275078:  // uriInfo
        setUriInfo((UriInfo) newValue);
        return;
      case -72522889:  // uriPortfolioId
        setUriPortfolioId((String) newValue);
        return;
      case 1130377033:  // uriNodeId
        setUriNodeId((String) newValue);
        return;
      case 1240319664:  // uriPositionId
        setUriPositionId((String) newValue);
        return;
      case 666567687:  // uriVersionId
        setUriVersionId((String) newValue);
        return;
      case 1121781064:  // portfolio
        setPortfolio((PortfolioTreeDocument) newValue);
        return;
      case -244857396:  // parentNode
        setParentNode((ManageablePortfolioNode) newValue);
        return;
      case 3386882:  // node
        setNode((ManageablePortfolioNode) newValue);
        return;
      case 747804969:  // position
        setPosition((PositionDocument) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the position master.
   * @return the value of the property
   */
  public PositionMaster getPositionMaster() {
    return _positionMaster;
  }

  /**
   * Sets the position master.
   * @param positionMaster  the new value of the property
   */
  public void setPositionMaster(PositionMaster positionMaster) {
    this._positionMaster = positionMaster;
  }

  /**
   * Gets the the {@code positionMaster} property.
   * @return the property, not null
   */
  public final Property<PositionMaster> positionMaster() {
    return metaBean().positionMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JSR-311 URI information.
   * @return the value of the property
   */
  public UriInfo getUriInfo() {
    return _uriInfo;
  }

  /**
   * Sets the JSR-311 URI information.
   * @param uriInfo  the new value of the property
   */
  public void setUriInfo(UriInfo uriInfo) {
    this._uriInfo = uriInfo;
  }

  /**
   * Gets the the {@code uriInfo} property.
   * @return the property, not null
   */
  public final Property<UriInfo> uriInfo() {
    return metaBean().uriInfo().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio id from the input URI.
   * @return the value of the property
   */
  public String getUriPortfolioId() {
    return _uriPortfolioId;
  }

  /**
   * Sets the portfolio id from the input URI.
   * @param uriPortfolioId  the new value of the property
   */
  public void setUriPortfolioId(String uriPortfolioId) {
    this._uriPortfolioId = uriPortfolioId;
  }

  /**
   * Gets the the {@code uriPortfolioId} property.
   * @return the property, not null
   */
  public final Property<String> uriPortfolioId() {
    return metaBean().uriPortfolioId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the node id from the input URI.
   * @return the value of the property
   */
  public String getUriNodeId() {
    return _uriNodeId;
  }

  /**
   * Sets the node id from the input URI.
   * @param uriNodeId  the new value of the property
   */
  public void setUriNodeId(String uriNodeId) {
    this._uriNodeId = uriNodeId;
  }

  /**
   * Gets the the {@code uriNodeId} property.
   * @return the property, not null
   */
  public final Property<String> uriNodeId() {
    return metaBean().uriNodeId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the position id from the input URI.
   * @return the value of the property
   */
  public String getUriPositionId() {
    return _uriPositionId;
  }

  /**
   * Sets the position id from the input URI.
   * @param uriPositionId  the new value of the property
   */
  public void setUriPositionId(String uriPositionId) {
    this._uriPositionId = uriPositionId;
  }

  /**
   * Gets the the {@code uriPositionId} property.
   * @return the property, not null
   */
  public final Property<String> uriPositionId() {
    return metaBean().uriPositionId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the version id from the URI.
   * @return the value of the property
   */
  public String getUriVersionId() {
    return _uriVersionId;
  }

  /**
   * Sets the version id from the URI.
   * @param uriVersionId  the new value of the property
   */
  public void setUriVersionId(String uriVersionId) {
    this._uriVersionId = uriVersionId;
  }

  /**
   * Gets the the {@code uriVersionId} property.
   * @return the property, not null
   */
  public final Property<String> uriVersionId() {
    return metaBean().uriVersionId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio.
   * @return the value of the property
   */
  public PortfolioTreeDocument getPortfolio() {
    return _portfolio;
  }

  /**
   * Sets the portfolio.
   * @param portfolio  the new value of the property
   */
  public void setPortfolio(PortfolioTreeDocument portfolio) {
    this._portfolio = portfolio;
  }

  /**
   * Gets the the {@code portfolio} property.
   * @return the property, not null
   */
  public final Property<PortfolioTreeDocument> portfolio() {
    return metaBean().portfolio().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the parent node.
   * @return the value of the property
   */
  public ManageablePortfolioNode getParentNode() {
    return _parentNode;
  }

  /**
   * Sets the parent node.
   * @param parentNode  the new value of the property
   */
  public void setParentNode(ManageablePortfolioNode parentNode) {
    this._parentNode = parentNode;
  }

  /**
   * Gets the the {@code parentNode} property.
   * @return the property, not null
   */
  public final Property<ManageablePortfolioNode> parentNode() {
    return metaBean().parentNode().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the node.
   * @return the value of the property
   */
  public ManageablePortfolioNode getNode() {
    return _node;
  }

  /**
   * Sets the node.
   * @param node  the new value of the property
   */
  public void setNode(ManageablePortfolioNode node) {
    this._node = node;
  }

  /**
   * Gets the the {@code node} property.
   * @return the property, not null
   */
  public final Property<ManageablePortfolioNode> node() {
    return metaBean().node().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio.
   * @return the value of the property
   */
  public PositionDocument getPosition() {
    return _position;
  }

  /**
   * Sets the portfolio.
   * @param position  the new value of the property
   */
  public void setPosition(PositionDocument position) {
    this._position = position;
  }

  /**
   * Gets the the {@code position} property.
   * @return the property, not null
   */
  public final Property<PositionDocument> position() {
    return metaBean().position().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code WebPortfoliosData}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code positionMaster} property.
     */
    private final MetaProperty<PositionMaster> _positionMaster = DirectMetaProperty.ofReadWrite(this, "positionMaster", PositionMaster.class);
    /**
     * The meta-property for the {@code uriInfo} property.
     */
    private final MetaProperty<UriInfo> _uriInfo = DirectMetaProperty.ofReadWrite(this, "uriInfo", UriInfo.class);
    /**
     * The meta-property for the {@code uriPortfolioId} property.
     */
    private final MetaProperty<String> _uriPortfolioId = DirectMetaProperty.ofReadWrite(this, "uriPortfolioId", String.class);
    /**
     * The meta-property for the {@code uriNodeId} property.
     */
    private final MetaProperty<String> _uriNodeId = DirectMetaProperty.ofReadWrite(this, "uriNodeId", String.class);
    /**
     * The meta-property for the {@code uriPositionId} property.
     */
    private final MetaProperty<String> _uriPositionId = DirectMetaProperty.ofReadWrite(this, "uriPositionId", String.class);
    /**
     * The meta-property for the {@code uriVersionId} property.
     */
    private final MetaProperty<String> _uriVersionId = DirectMetaProperty.ofReadWrite(this, "uriVersionId", String.class);
    /**
     * The meta-property for the {@code portfolio} property.
     */
    private final MetaProperty<PortfolioTreeDocument> _portfolio = DirectMetaProperty.ofReadWrite(this, "portfolio", PortfolioTreeDocument.class);
    /**
     * The meta-property for the {@code parentNode} property.
     */
    private final MetaProperty<ManageablePortfolioNode> _parentNode = DirectMetaProperty.ofReadWrite(this, "parentNode", ManageablePortfolioNode.class);
    /**
     * The meta-property for the {@code node} property.
     */
    private final MetaProperty<ManageablePortfolioNode> _node = DirectMetaProperty.ofReadWrite(this, "node", ManageablePortfolioNode.class);
    /**
     * The meta-property for the {@code position} property.
     */
    private final MetaProperty<PositionDocument> _position = DirectMetaProperty.ofReadWrite(this, "position", PositionDocument.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings("unchecked")
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("positionMaster", _positionMaster);
      temp.put("uriInfo", _uriInfo);
      temp.put("uriPortfolioId", _uriPortfolioId);
      temp.put("uriNodeId", _uriNodeId);
      temp.put("uriPositionId", _uriPositionId);
      temp.put("uriVersionId", _uriVersionId);
      temp.put("portfolio", _portfolio);
      temp.put("parentNode", _parentNode);
      temp.put("node", _node);
      temp.put("position", _position);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public WebPortfoliosData createBean() {
      return new WebPortfoliosData();
    }

    @Override
    public Class<? extends WebPortfoliosData> beanType() {
      return WebPortfoliosData.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code positionMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PositionMaster> positionMaster() {
      return _positionMaster;
    }

    /**
     * The meta-property for the {@code uriInfo} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UriInfo> uriInfo() {
      return _uriInfo;
    }

    /**
     * The meta-property for the {@code uriPortfolioId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriPortfolioId() {
      return _uriPortfolioId;
    }

    /**
     * The meta-property for the {@code uriNodeId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriNodeId() {
      return _uriNodeId;
    }

    /**
     * The meta-property for the {@code uriPositionId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriPositionId() {
      return _uriPositionId;
    }

    /**
     * The meta-property for the {@code uriVersionId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriVersionId() {
      return _uriVersionId;
    }

    /**
     * The meta-property for the {@code portfolio} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PortfolioTreeDocument> portfolio() {
      return _portfolio;
    }

    /**
     * The meta-property for the {@code parentNode} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageablePortfolioNode> parentNode() {
      return _parentNode;
    }

    /**
     * The meta-property for the {@code node} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageablePortfolioNode> node() {
      return _node;
    }

    /**
     * The meta-property for the {@code position} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<PositionDocument> position() {
      return _position;
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
