/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.historicaldata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.time.calendar.DayOfWeek;
import javax.time.calendar.LocalDate;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.id.DomainSpecificIdentifier;
import com.opengamma.id.DomainSpecificIdentifiers;
import com.opengamma.id.IdentificationDomain;
import com.opengamma.util.timeseries.localdate.ListLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.MutableLocalDateDoubleTimeSeries;
import com.opengamma.util.tuple.Pair;

/**
 * 
 *
 * @author jim
 */
public class HistoricalDataProviderTest {
  private static final Logger s_logger = LoggerFactory.getLogger(HistoricalDataProviderTest.class);
  private static final String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private Set<String> _usedIds = new HashSet<String>();
  private boolean isWeekday(LocalDate day) {
    return (day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY);
  }
  
  public LocalDateDoubleTimeSeries randomTimeSeries() {
    MutableLocalDateDoubleTimeSeries dts = new ListLocalDateDoubleTimeSeries();
    LocalDate start = LocalDate.of(2000, 1, 2);
    LocalDate end = start.plusYears(10);
    LocalDate current = start;
    while (current.isBefore(end)) {
      current = current.plusDays(1);
      if (isWeekday(current)) {
        dts.putDataPoint(current, Math.random());
      }
    }
    return dts;
  }
  
  public int random(int maxBoundExclusive) {
    return (int) (Math.floor(Math.random() * maxBoundExclusive));
  }
  
  private String makeRandomId() {
    StringBuilder sb = new StringBuilder();
    sb.append(ALPHAS.charAt(random(26)));
    sb.append(ALPHAS.charAt(random(26)));
    sb.append(Integer.toString(random(10)));
    sb.append(Integer.toString(random(10)));
    return sb.toString();
  }
  
  // be careful not to call this more than 26^2 * 100 times, or it will loop forever, and it will get progressively slower.
  // now put in a test as it gets near the limit.
  private String makeUniqueRandomId() {
    if (_usedIds.size() > 26*26*90) {
      Assert.fail("tried to create too many ids");
    }
    String id;
    do {
      id = makeRandomId();
      s_logger.info(id);
    } while (_usedIds.contains(id));    
    _usedIds.add(id);
    return id;
  }
  
  private DomainSpecificIdentifiers makeDomainSpecificIdentifiers() {
    return new DomainSpecificIdentifiers(new DomainSpecificIdentifier(IdentificationDomain.BLOOMBERG_TICKER, makeUniqueRandomId()),
                                         new DomainSpecificIdentifier(IdentificationDomain.BLOOMBERG_BUID, makeUniqueRandomId()));
  }
  
  private Pair<HistoricalDataProvider, Set<DomainSpecificIdentifiers>> buildAndTestInMemoryProvider() {
    InMemoryHistoricalDataProvider inMemoryHistoricalDataProvider = new InMemoryHistoricalDataProvider();
    Map<DomainSpecificIdentifiers, Map<String, Map<String, Map<String, LocalDateDoubleTimeSeries>>>> map = new HashMap<DomainSpecificIdentifiers, Map<String, Map<String, Map<String, LocalDateDoubleTimeSeries>>>>();
    for (int i=0; i<100; i++) {
      DomainSpecificIdentifiers ids = makeDomainSpecificIdentifiers();
      Map<String, Map<String, Map<String, LocalDateDoubleTimeSeries>>> dsidsSubMap = map.get(ids);
      if (dsidsSubMap == null) {
        dsidsSubMap = new HashMap<String, Map<String, Map<String, LocalDateDoubleTimeSeries>>>();
        map.put(ids, dsidsSubMap);
      }
      for (String dataSource : new String[] { "BLOOMBERG", "REUTERS", "JPM" }) {
        Map<String, Map<String, LocalDateDoubleTimeSeries>> dataSourceSubMap = dsidsSubMap.get(dataSource);
        if (dataSourceSubMap == null) {
          dataSourceSubMap = new HashMap<String, Map<String, LocalDateDoubleTimeSeries>>();
          dsidsSubMap.put(dataSource, dataSourceSubMap);
        }
        for (String dataProvider : new String[] { "UNKNOWN", "CMPL", "CMPT" }) {
          Map<String, LocalDateDoubleTimeSeries> dataProviderSubMap = dataSourceSubMap.get(dataProvider);
          if (dataProviderSubMap == null) {
            dataProviderSubMap = new HashMap<String, LocalDateDoubleTimeSeries>();
            dataSourceSubMap.put(dataProvider, dataProviderSubMap);
          }
          for (String field : new String[] { "PX_LAST", "VOLUME" }) {
            LocalDateDoubleTimeSeries randomTimeSeries = randomTimeSeries();
            dataProviderSubMap.put(field, randomTimeSeries);
            inMemoryHistoricalDataProvider.storeHistoricalTimeSeries(ids, dataSource, dataProvider, field, randomTimeSeries);
          }
        }
      }
    }
    for (DomainSpecificIdentifiers dsids : map.keySet()) {
      for (String dataSource : new String[] { "BLOOMBERG", "REUTERS", "JPM" }) {
        for (String dataProvider : new String[] { "UNKNOWN", "CMPL", "CMPT" }) {
          for (String field : new String[] { "PX_LAST", "VOLUME" }) {
            Assert.assertEquals(map.get(dsids).get(dataSource).get(dataProvider).get(field), 
                                inMemoryHistoricalDataProvider.getHistoricalTimeSeries(dsids, dataSource, dataProvider, field));
            
          }
        }
      }
    }
    return new Pair<HistoricalDataProvider, Set<DomainSpecificIdentifiers>>(inMemoryHistoricalDataProvider, map.keySet());
  }
  
  @Test
  public void testInMemoryProvider() {
    buildAndTestInMemoryProvider(); 
  }
  

  
  @Test
  public void testEHCachingHistoricalDataProvider() {
    Pair<HistoricalDataProvider, Set<DomainSpecificIdentifiers>> providerAndDsids = buildAndTestInMemoryProvider();
    HistoricalDataProvider inMemoryHistoricalDataProvider = providerAndDsids.getFirst();
    EHCachingHistoricalDataProvider cachedProvider = new EHCachingHistoricalDataProvider(inMemoryHistoricalDataProvider);
    Set<DomainSpecificIdentifiers> identifiers = providerAndDsids.getSecond();
    DomainSpecificIdentifiers[] dsids = identifiers.toArray(new DomainSpecificIdentifiers[] {});
    String[] dataSources = new String[] { "BLOOMBERG", "REUTERS", "JPM" };
    String[] dataProviders = new String[] { "UNKNOWN", "CMPL", "CMPT" };
    String[] fields = new String[] { "PX_LAST", "VOLUME" };
    for (int i=0; i<10000; i++) {
      DomainSpecificIdentifiers ids = dsids[random(dsids.length)];
      String dataSource = dataSources[random(dataSources.length)];
      String dataProvider = dataProviders[random(dataProviders.length)];
      String field = fields[random(fields.length)];
      Assert.assertEquals(inMemoryHistoricalDataProvider.getHistoricalTimeSeries(ids, dataSource, dataProvider, field),
                          cachedProvider.getHistoricalTimeSeries(ids, dataSource, dataProvider, field));
    }    
  }
}
