/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.swap;

import java.util.HashSet;
import java.util.Set;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Doubles;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.financial.analytics.schedule.ScheduleCalculator;
import com.opengamma.financial.convention.ConventionBundle;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.convention.InMemoryConventionBundleMaster;
import com.opengamma.financial.convention.businessday.HolidaySourceCalendarAdapter;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.interestrate.annuity.definition.ForwardLiborAnnuity;
import com.opengamma.financial.interestrate.payments.ForwardLiborPayment;
import com.opengamma.financial.interestrate.swap.definition.TenorSwap;
import com.opengamma.financial.security.swap.FloatingInterestRateLeg;
import com.opengamma.financial.security.swap.InterestRateNotional;
import com.opengamma.financial.security.swap.SwapLeg;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.financial.world.holiday.HolidaySource;
import com.opengamma.financial.world.region.InMemoryRegionMaster;
import com.opengamma.financial.world.region.Region;
import com.opengamma.financial.world.region.RegionSource;
import com.opengamma.id.Identifier;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 
 */
public class TenorSwapSecurityToTenorSwapConverter {
  
  private static final Logger s_logger = LoggerFactory.getLogger(TenorSwapSecurityToTenorSwapConverter.class);
  private final HolidaySource _holidaySource;
  private final RegionSource _regionSource;
  private final ConventionBundleSource _conventionSource;

  public TenorSwapSecurityToTenorSwapConverter(final HolidaySource holidaySource, final RegionSource regionSource, final ConventionBundleSource conventionSource) {
    _holidaySource = holidaySource;
    _regionSource = regionSource;
    _conventionSource = conventionSource;

  }
  
  // REVIEW: jim 8-Oct-2010 -- we might want to move this logic inside the RegionMaster.
  protected Calendar getCalendar(Identifier regionId) {
    if (regionId.getScheme().equals(InMemoryRegionMaster.REGION_FILE_SCHEME_ISO2) && regionId.getValue().contains("+")) {
      String[] regions = regionId.getValue().split("\\+");
      Set<Region> resultRegions = new HashSet<Region>();
      for (String region : regions) {
        resultRegions.add(_regionSource.getHighestLevelRegion(Identifier.of(InMemoryRegionMaster.REGION_FILE_SCHEME_ISO2, region)));
      }
      return new HolidaySourceCalendarAdapter(_holidaySource, resultRegions);
    } else {
      final Region payRegion = _regionSource.getHighestLevelRegion(regionId); // we've checked that they are the same.
      return new HolidaySourceCalendarAdapter(_holidaySource, payRegion);
    }
  }

  public TenorSwap getSwap(final SwapSecurity swapSecurity, final String fundingCurveName, final String payLegCurveName, final String recieveLegCurveName, final double marketRate,  
       final ZonedDateTime now) {

    Validate.notNull(swapSecurity, "swap security");
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate().toZonedDateTime();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate().toZonedDateTime();
  
    SwapLeg payLeg = swapSecurity.getPayLeg();
    SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    
    FloatingInterestRateLeg floatPayLeg;
    FloatingInterestRateLeg floatReceiveLeg;
    if (payLeg instanceof FloatingInterestRateLeg && receiveLeg instanceof FloatingInterestRateLeg) {
      floatPayLeg = (FloatingInterestRateLeg) payLeg;
      floatReceiveLeg = (FloatingInterestRateLeg) receiveLeg;
    } else {
      throw new OpenGammaRuntimeException("can only handle float-float legs");
    }
    
    if (!payLeg.getRegionIdentifier().equals(receiveLeg.getRegionIdentifier())) {
      throw new OpenGammaRuntimeException("Pay and receive legs must be from same region");
    }
    
    final Calendar calendar = getCalendar(payLeg.getRegionIdentifier());

    final String currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency().getISOCode();
    final ConventionBundle conventions = _conventionSource.getConventionBundle(Identifier.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, currency + "_TENOR_SWAP"));
    
    ForwardLiborAnnuity pay = getFloatLeg(floatPayLeg, now, effectiveDate, maturityDate,
        fundingCurveName, payLegCurveName, calendar, 0.0 /*spread is paid on receive leg*/, conventions.getBasisSwapPayFloatingLegSettlementDays());
   
    ForwardLiborAnnuity receive = getFloatLeg(floatReceiveLeg, now, effectiveDate, maturityDate,
       fundingCurveName, recieveLegCurveName, calendar, marketRate, conventions.getBasisSwapReceiveFloatingLegSettlementDays());
   
    return new TenorSwap(pay, receive);
  }

  
  public ForwardLiborAnnuity getFloatLeg(final FloatingInterestRateLeg floatLeg, final ZonedDateTime now, final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate,
      final String fundingCurveName, final String liborCurveName, final Calendar calendar, final double marketRate, final int settlementDays) {
    s_logger.debug("getFloatLeg(floatLeg=" + floatLeg + ", now=" + now + ", effectiveDate=" + effectiveDate + ", maturityDate=" + maturityDate + ", fundingCurveName=" + fundingCurveName
        + ", liborCurveName" + liborCurveName + ", calendar=" + calendar + ", settlementDays=" + settlementDays);
    final ZonedDateTime[] unadjustedDates = ScheduleCalculator.getUnadjustedDateSchedule(effectiveDate, maturityDate, floatLeg.getFrequency());
    s_logger.debug("unadjustedDates=" + Arrays.asList(unadjustedDates));
    final ZonedDateTime[] adjustedDates = ScheduleCalculator.getAdjustedDateSchedule(unadjustedDates, floatLeg.getBusinessDayConvention(), calendar);
    s_logger.debug("adjustedDates=" + Arrays.asList(adjustedDates));
    final ZonedDateTime[] resetDates = ScheduleCalculator.getAdjustedResetDateSchedule(effectiveDate, unadjustedDates, floatLeg.getBusinessDayConvention(), calendar, settlementDays);
    s_logger.debug("resetDates=" + Arrays.asList(resetDates));
    final ZonedDateTime[] maturityDates = ScheduleCalculator.getAdjustedMaturityDateSchedule(effectiveDate, unadjustedDates, floatLeg.getBusinessDayConvention(), calendar, floatLeg.getFrequency());
    s_logger.debug("maturityDates=" + Arrays.asList(maturityDates));

    double[] paymentTimes = ScheduleCalculator.getTimes(adjustedDates, DayCountFactory.INSTANCE.getDayCount("Actual/Actual"), now);
    s_logger.debug("paymentTimes=" + Doubles.asList(paymentTimes));
    double[] resetTimes = ScheduleCalculator.getTimes(resetDates, DayCountFactory.INSTANCE.getDayCount("Actual/Actual"), now);
    s_logger.debug("resetTimes=" + Doubles.asList(resetTimes));
    double[] maturityTimes = ScheduleCalculator.getTimes(maturityDates, DayCountFactory.INSTANCE.getDayCount("Actual/Actual"), now);
    s_logger.debug("maturityTimes=" + Doubles.asList(maturityTimes));
    double[] yearFractions = ScheduleCalculator.getYearFractions(adjustedDates, floatLeg.getDayCount(), effectiveDate);
    s_logger.debug("yearFractions=" + Doubles.asList(yearFractions));
    final double notional = ((InterestRateNotional) floatLeg.getNotional()).getAmount();    

   
    final double[] spreads = new double[paymentTimes.length];
    Arrays.fill(spreads, marketRate);

    final ForwardLiborPayment[] payments = new ForwardLiborPayment[paymentTimes.length];
    for (int i = 0; i < payments.length; i++) {
        payments[i] = new ForwardLiborPayment(paymentTimes[i], notional, resetTimes[i], maturityTimes[i], yearFractions[i], yearFractions[i], spreads[i], fundingCurveName, liborCurveName);
    }

    //TODO need to handle paymentYearFraction differently from forwardYearFraction 
    return new ForwardLiborAnnuity(payments);
  }

}
