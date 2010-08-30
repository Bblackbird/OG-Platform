/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */

package com.opengamma.financial.security.db;

import com.opengamma.financial.RegionSource;

/**
 * Context for the bean operations - i.e. access to any resources that are needed
 */
public final class OperationContext {

  private RegionSource _regionRepository;

  public void setRegionRepository(final RegionSource regionRepository) {
    _regionRepository = regionRepository;
  }

  public RegionSource getRegionRepository() {
    return _regionRepository;
  }

}
