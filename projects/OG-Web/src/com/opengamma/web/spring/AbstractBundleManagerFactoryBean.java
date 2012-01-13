/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

import com.opengamma.util.SingletonFactoryBean;
import com.opengamma.web.bundle.BundleManager;

/**
 * Abstract class for creating BundleManager for Development or Production bundles
 * from the Bundle XML configuration file.
 */
public abstract class AbstractBundleManagerFactoryBean extends SingletonFactoryBean<BundleManager> implements ServletContextAware {

  
  private static final Logger s_logger = LoggerFactory.getLogger(AbstractBundleManagerFactoryBean.class);
  /**
   * The config resource.
   */
  private Resource _configResource;
  /**
   * The base directory.
   */
  private String _baseDir;
  /**
   * The servlet context.
   */
  private ServletContext _servletContext;

  //-------------------------------------------------------------------------
  /**
   * Gets the config resource.
   * 
   * @return the config resource
   */
  public Resource getConfigResource() {
    return _configResource;
  }

  /**
   * Sets the config resource.
   * @param configResource  the config resource
   */
  public void setConfigResource(Resource configResource) {
    _configResource = configResource;
  }

  /**
   * Gets the base directory.
   * @return the base directory
   */
  public String getBaseDir() {
    return _baseDir;
  }

  /**
   * Sets the base directory.
   * 
   * @param baseDir  the base directory
   */
  public void setBaseDir(String baseDir) {
    _baseDir = baseDir;
  }

  /**
   * Gets the servlet context.
   * @return the servlet context
   */
  public ServletContext getServletContext() {
    return _servletContext;
  }

  /**
   * Sets the servlet context.
   * 
   * @param servletContext  the context, not null
   */
  @Override
  public void setServletContext(ServletContext servletContext) {
    _servletContext = servletContext;
  }

  //-------------------------------------------------------------------------
  /**
   * Resolves the config file.
   * 
   * @return the resolved file
   */
  protected InputStream getXMLStream() {
    InputStream xmlStream = null;
    try {
      if (_configResource instanceof ClassPathResource) {
        ClassPathResource resource = (ClassPathResource) _configResource;
        s_logger.debug("resource.getPath() : {}", resource.getPath());
        s_logger.debug("resource.getClassLoader() : {}", resource.getClassLoader());
        s_logger.debug("resource.getURL().toString() : {}", resource.getURL().toString());
        s_logger.debug("resource.getDescription() : {}", resource.getDescription());
      }
      xmlStream = _configResource.getInputStream();
      
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot find bundle config xml file in the classpath");
    }
    return xmlStream;
  }

  /**
   * Resolves the base directory.
   * 
   * @return the base directory
   */
  protected File resolveBaseDir() {
    ServletContext servletContext = getServletContext();
    if (servletContext == null) {
      throw new IllegalStateException("Bundle Manager needs web application context to work out absolute path for bundle base directory");
    }
    String baseDir = getBaseDir().startsWith("/") ? getBaseDir() : "/" + getBaseDir();
    baseDir = servletContext.getRealPath(baseDir);
    return new File(baseDir);
  }

}
