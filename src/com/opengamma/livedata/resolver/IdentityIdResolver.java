/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.resolver;

import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;

/**
 * 
 *
 * @author kirk
 */
public class IdentityIdResolver implements IdResolver {

  @Override
  public Identifier resolve(IdentifierBundle ids) {
    if (ids.getIdentifiers().size() != 1) {
      throw new IllegalArgumentException("This resolver only supports singleton bundles");
    }
    return ids.getIdentifiers().iterator().next();
  }
  

}
