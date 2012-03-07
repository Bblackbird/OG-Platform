/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import javax.time.calendar.Period;

import org.apache.commons.lang.Validate;

import com.opengamma.core.region.RegionUtils;
import com.opengamma.core.security.SecurityUtils;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.financial.convention.frequency.SimpleFrequencyFactory;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;

/**
 * 
 */
public class JPConventions {
  private static final char[] MONTH_NAMES = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K' };

  public static synchronized void addFixedIncomeInstrumentConventions(final ConventionBundleMaster conventionMaster) {
    Validate.notNull(conventionMaster, "convention master");
    final BusinessDayConvention modified = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
    final BusinessDayConvention following = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Following");
    final DayCount act360 = DayCountFactory.INSTANCE.getDayCount("Actual/360");
    final DayCount act365 = DayCountFactory.INSTANCE.getDayCount("Actual/365");
    final Frequency annual = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.ANNUAL_NAME);
    final Frequency semiAnnual = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.SEMI_ANNUAL_NAME);
    final Frequency quarterly = SimpleFrequencyFactory.INSTANCE.getFrequency(Frequency.QUARTERLY_NAME);
    //TODO looked at BSYM and the codes seem right but need to check
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY00O/N Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR O/N")), "JPY LIBOR O/N", act360,
        following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY00S/N Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR S/N")), "JPY LIBOR S/N", act360,
        following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY00T/N Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR T/N")), "JPY LIBOR T/N", act360,
        following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0001W Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 1w")), "JPY LIBOR 1w", act360,
        following, Period.ofDays(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0002W Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 2w")), "JPY LIBOR 2w", act360,
        following, Period.ofDays(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0001M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 1m")), "JPY LIBOR 1m", act360,
        following, Period.ofMonths(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0002M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 2m")), "JPY LIBOR 2m", act360,
        following, Period.ofMonths(2), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0003M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 3m"),
            ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYLIBORP3M")), "JPY LIBOR 3m", act360, following, Period.ofMonths(3), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0004M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 4m")), "JPY LIBOR 4m", act360,
        following, Period.ofMonths(4), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0005M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 5m")), "JPY LIBOR 5m", act360,
        following, Period.ofMonths(5), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0006M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 6m"),
            ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYLIBORP6M")), "JPY LIBOR 6m", act360, following, Period.ofMonths(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0007M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 7m")), "JPY LIBOR 7m", act360,
        following, Period.ofMonths(7), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0008M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 8m")), "JPY LIBOR 8m", act360,
        following, Period.ofMonths(8), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0009M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 9m")), "JPY LIBOR 9m", act360,
        following, Period.ofMonths(9), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0010M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 10m")), "JPY LIBOR 10m", act360,
        following, Period.ofMonths(10), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0011M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 11m")), "JPY LIBOR 11m", act360,
        following, Period.ofMonths(11), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JY0012M Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 12m"),
            ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYLIBORP12M")), "JPY LIBOR 12m", act360, following, Period.ofMonths(12), 2, false, null);
    conventionMaster.addConventionBundle(ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("MUTSCALM Index"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY TONAR")),
        "JPY TONAR", act365, following, Period.ofDays(1), 2, false, null);

    //TODO need to check that these are right for deposit rates
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR1T Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 1d")), "JPY DEPOSIT 1d", act360,
        following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR2T Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 2d")), "JPY DEPOSIT 2d", act360,
        following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR3T Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 3d")), "JPY DEPOSIT 3d", act360,
        following, Period.ofDays(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR1Z Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 1w")), "JPY DEPOSIT 1w", act360,
        following, Period.ofDays(7), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR2Z Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 2w")), "JPY DEPOSIT 2w", act360,
        following, Period.ofDays(14), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR3Z Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 3w")), "JPY DEPOSIT 3w", act360,
        following, Period.ofDays(21), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRA Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 1m")), "JPY DEPOSIT 1m", act360,
        following, Period.ofMonths(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRB Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 2m")), "JPY DEPOSIT 2m", act360,
        following, Period.ofMonths(2), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRC Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 3m")), "JPY DEPOSIT 3m", act360,
        following, Period.ofMonths(3), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRD Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 4m")), "JPY DEPOSIT 4m", act360,
        following, Period.ofMonths(4), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRE Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 5m")), "JPY DEPOSIT 5m", act360,
        following, Period.ofMonths(5), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRF Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 6m")), "JPY DEPOSIT 6m", act360,
        following, Period.ofMonths(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRG Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 7m")), "JPY DEPOSIT 7m", act360,
        following, Period.ofMonths(7), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRH Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 8m")), "JPY DEPOSIT 8m", act360,
        following, Period.ofMonths(8), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRI Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 9m")), "JPY DEPOSIT 9m", act360,
        following, Period.ofMonths(9), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRJ Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 10m")), "JPY DEPOSIT 10m", act360,
        following, Period.ofMonths(10), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDRK Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 11m")), "JPY DEPOSIT 11m", act360,
        following, Period.ofMonths(11), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR1 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 1y")), "JPY DEPOSIT 1y", act360,
        following, Period.ofYears(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR2 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 2y")), "JPY DEPOSIT 2y", act360,
        following, Period.ofYears(2), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR3 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 3y")), "JPY DEPOSIT 3y", act360,
        following, Period.ofYears(3), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR4 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 4y")), "JPY DEPOSIT 4y", act360,
        following, Period.ofYears(4), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYDR5 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY DEPOSIT 5y")), "JPY DEPOSIT 5y", act360,
        following, Period.ofYears(5), 2, false, null);

    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYFR3/9 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY FRA 3x9")), "JPY FRA 3x9", act360,
        following, Period.ofMonths(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYFR6/12 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY FRA 6x12")), "JPY FRA 6x12", act360,
        following, Period.ofMonths(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId("JYFR9/15 Curncy"), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY FRA 9x15")), "JPY FRA 9x15", act360,
        following, Period.ofMonths(6), 2, false, null);

    //TODO holiday associated with JPY swaps is Tokyo
    final ExternalId jp = RegionUtils.financialRegionId("JP");
    final DayCount swapFixedDayCount = act365;
    final BusinessDayConvention swapFixedBusinessDay = modified;
    final Frequency swapFixedPaymentFrequency = semiAnnual;
    final DayCount swapFloatDayCount = act360;
    final BusinessDayConvention swapFloatBusinessDay = modified;
    final Frequency swapFloatPaymentFrequency = semiAnnual;
    for (final int i : new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }) {
      final String bbgOISId = "JYSO" + MONTH_NAMES[i - 1] + " Curncy";
      final String ogOISName = "JPY OIS " + i + "m";
      final Frequency frequency = PeriodFrequency.of(Period.ofMonths(i));
      conventionMaster.addConventionBundle(
          ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId(bbgOISId), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, ogOISName)), ogOISName,
          act365, swapFixedBusinessDay, frequency, 2, jp, act365, swapFloatBusinessDay, frequency,
          2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY TONAR"), jp, true);
    }
    for (int i = 1; i <= 30; i++) {
      final String bbgSwapId = "JYSW" + i + " Curncy";
      final String ogSwapName = "JPY SWAP " + i + "y";
      final String bbgOISId = "JYSO" + i + " Curncy";
      final String ogOISName = "JPY OIS " + i + "y";
      conventionMaster.addConventionBundle(
          ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId(bbgSwapId), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, ogSwapName)), ogSwapName,
          swapFixedDayCount, swapFixedBusinessDay, swapFixedPaymentFrequency, 2, jp, swapFloatDayCount, swapFloatBusinessDay, swapFloatPaymentFrequency,
          2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 6m"), jp, true);
      conventionMaster.addConventionBundle(
          ExternalIdBundle.of(SecurityUtils.bloombergTickerSecurityId(bbgOISId), ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, ogOISName)), ogOISName,
          act365, swapFixedBusinessDay, annual, 2, jp, act365, swapFloatBusinessDay, annual,
          2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY TONAR"), jp, true);
    }

    conventionMaster.addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_SWAP")), "JPY_SWAP", act365, modified, semiAnnual, 2, jp, act360,
        modified, semiAnnual, 2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 6m"), jp, true);
    conventionMaster.addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_3M_SWAP")), "JPY_3M_SWAP", act365, modified, semiAnnual, 2, jp,
        act360, modified, quarterly, 2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 3m"), jp, true);
    conventionMaster.addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_6M_SWAP")), "JPY_6M_SWAP", act365, modified, semiAnnual, 2, jp,
        act360, modified, semiAnnual, 2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY LIBOR 6m"), jp, true);
    // Overnight Index Swap Convention have additional flag, publicationLag
    final Integer publicationLag = 0; // TODO CASE PublicationLag JPY - Confirm 0
    conventionMaster.addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_OIS_SWAP")), "JPY_OIS_SWAP", act365, modified, annual, 2, jp,
        act365, modified, annual, 2, ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY TONAR"), jp, true, publicationLag);
    conventionMaster
        .addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_OIS_CASH")), "JPY_OIS_CASH", act365, following, null, 2, false, null);

    //TODO check this
    conventionMaster.addConventionBundle(ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPY_IBOR_INDEX")), "JPY_IBOR_INDEX", act360, following, 2, false);

    //Identifiers for external data 
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP1D"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP1D")),
        "JPYCASHP1D", act360, following, Period.ofDays(1), 0, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP1M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP1M")),
        "JPYCASHP1M", act360, modified, Period.ofMonths(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP2M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP2M")),
        "JPYCASHP2M", act360, modified, Period.ofMonths(2), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP3M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP3M")),
        "JPYCASHP3M", act360, modified, Period.ofMonths(3), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP4M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP4M")),
        "JPYCASHP4M", act360, modified, Period.ofMonths(4), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP5M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP5M")),
        "JPYCASHP5M", act360, modified, Period.ofMonths(5), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP6M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP6M")),
        "JPYCASHP6M", act360, modified, Period.ofMonths(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP7M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP7M")),
        "JPYCASHP7M", act360, modified, Period.ofMonths(7), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP8M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP8M")),
        "JPYCASHP8M", act360, modified, Period.ofMonths(8), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP9M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP9M")),
        "JPYCASHP9M", act360, modified, Period.ofMonths(9), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP10M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP10M")),
        "JPYCASHP10M", act360, modified, Period.ofMonths(10), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP11M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP11M")),
        "JPYCASHP11M", act360, modified, Period.ofMonths(1), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYCASHP12M"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYCASHP12M")),
        "JPYCASHP12M", act360, modified, Period.ofMonths(12), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP2Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP2Y")),
        "JPYSWAPP2Y", act365, modified, Period.ofYears(2), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP3Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP3Y")),
        "JPYSWAPP3Y", act365, modified, Period.ofYears(3), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP4Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP4Y")),
        "JPYSWAPP4Y", act365, modified, Period.ofYears(4), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP5Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP5Y")),
        "JPYSWAPP5Y", act365, modified, Period.ofYears(5), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP6Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP6Y")),
        "JPYSWAPP6Y", act365, modified, Period.ofYears(6), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP7Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP7Y")),
        "JPYSWAPP7Y", act365, modified, Period.ofYears(7), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP8Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP8Y")),
        "JPYSWAPP8Y", act365, modified, Period.ofYears(8), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP9Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP9Y")),
        "JPYSWAPP9Y", act365, modified, Period.ofYears(9), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP10Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP10Y")),
        "JPYSWAPP10Y", act365, modified, Period.ofYears(10), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP12Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP12Y")),
        "JPYSWAPP12Y", act365, modified, Period.ofYears(12), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP15Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP15Y")),
        "JPYSWAPP15Y", act365, modified, Period.ofYears(15), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP20Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP20Y")),
        "JPYSWAPP20Y", act365, modified, Period.ofYears(20), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP25Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP25Y")),
        "JPYSWAPP25Y", act365, modified, Period.ofYears(25), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP30Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP30Y")),
        "JPYSWAPP30Y", act365, modified, Period.ofYears(30), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP40Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP40Y")),
        "JPYSWAPP40Y", act365, modified, Period.ofYears(40), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP50Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP50Y")),
        "JPYSWAPP50Y", act365, modified, Period.ofYears(50), 2, false, null);
    conventionMaster.addConventionBundle(
        ExternalIdBundle.of(ExternalId.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, "JPYSWAPP80Y"), ExternalId.of(InMemoryConventionBundleMaster.OG_SYNTHETIC_TICKER, "JPYSWAPP80Y")),
        "JPYSWAPP80Y", act365, modified, Period.ofYears(80), 2, false, null);
  }
}
