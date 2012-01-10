/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve.rest.old;

import javax.ws.rs.Path;

import org.fudgemsg.FudgeContext;

import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveSpecificationBuilder;
import com.opengamma.util.rest.AbstractResourceService;

/**
 * RESTful backend for {@link RemoteInterpolatedYieldCurveSpecificationBuilder}.
 */
@Path("interpolatedYieldCurveSpecificationBuilder")
public class InterpolatedYieldCurveSpecificationBuilderService extends AbstractResourceService<InterpolatedYieldCurveSpecificationBuilder, InterpolatedYieldCurveSpecificationBuilderResource> {

  public InterpolatedYieldCurveSpecificationBuilderService(final FudgeContext fudgeContext) {
    super(fudgeContext);
  }

  @Override
  protected InterpolatedYieldCurveSpecificationBuilderResource createResource(InterpolatedYieldCurveSpecificationBuilder underlying) {
    return new InterpolatedYieldCurveSpecificationBuilderResource(underlying, getFudgeContext());
  }

}
