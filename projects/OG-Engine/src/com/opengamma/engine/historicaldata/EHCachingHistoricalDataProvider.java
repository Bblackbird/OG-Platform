/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.historicaldata;

import java.io.Serializable;

import javax.time.calendar.LocalDate;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ehcache.EHCacheUtils;
import com.opengamma.util.timeseries.localdate.ArrayLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;
import com.opengamma.util.tuple.ObjectsPair;
import com.opengamma.util.tuple.Pair;

/**
 * 
 */
public class EHCachingHistoricalDataProvider implements HistoricalDataSource {
  private static final Logger s_logger = LoggerFactory.getLogger(EHCachingHistoricalDataProvider.class);

  private static final LocalDateDoubleTimeSeries EMPTY_TIMESERIES = new ArrayLocalDateDoubleTimeSeries();
  private static final String CACHE_NAME = "HistoricalDataCache";
  private final HistoricalDataSource _underlying;
  private final CacheManager _manager;
  private final Cache _cache;

  public EHCachingHistoricalDataProvider(HistoricalDataSource underlying, CacheManager cacheManager, int maxElementsInMemory, MemoryStoreEvictionPolicy memoryStoreEvictionPolicy,
      boolean overflowToDisk, String diskStorePath, boolean eternal, long timeToLiveSeconds, long timeToIdleSeconds, boolean diskPersistent, long diskExpiryThreadIntervalSeconds,
      RegisteredEventListeners registeredEventListeners) {
    ArgumentChecker.notNull(underlying, "Underlying Historical Data Provider");
    ArgumentChecker.notNull(cacheManager, "cacheManager");
    _underlying = underlying;
    _manager = cacheManager;
    EHCacheUtils.addCache(_manager, CACHE_NAME, maxElementsInMemory, memoryStoreEvictionPolicy, overflowToDisk, diskStorePath, eternal, timeToLiveSeconds, timeToIdleSeconds, diskPersistent,
        diskExpiryThreadIntervalSeconds, registeredEventListeners);
    _cache = EHCacheUtils.getCacheFromManager(_manager, CACHE_NAME);
  }

  public EHCachingHistoricalDataProvider(HistoricalDataSource underlying, CacheManager manager) {
    ArgumentChecker.notNull(underlying, "Underlying Historical Data Provider");
    ArgumentChecker.notNull(manager, "Cache Manager");
    _underlying = underlying;
    EHCacheUtils.addCache(manager, CACHE_NAME);
    _cache = EHCacheUtils.getCacheFromManager(manager, CACHE_NAME);
    _manager = manager;
  }

  /**
   * @return the underlying
   */
  public HistoricalDataSource getUnderlying() {
    return _underlying;
  }

  /**
   * @return the CacheManager
   */
  public CacheManager getCacheManager() {
    return _manager;
  }
  
  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, LocalDate currentDate, String dataSource, String dataProvider, String dataField) {
    MetaDataKey key = new MetaDataKey(null, currentDate, identifiers, dataSource, dataProvider, dataField);
    Element element = _cache.get(key);
    if (element != null) {
      Serializable value = element.getValue();
      if (value instanceof UniqueIdentifier) {
        UniqueIdentifier uid = (UniqueIdentifier) value;
        s_logger.debug("retrieved UID: {} from cache", uid);
        LocalDateDoubleTimeSeries timeSeries = getHistoricalData(uid);
        return new ObjectsPair<UniqueIdentifier, LocalDateDoubleTimeSeries>(uid, timeSeries);
      } else if (value == null) {
        s_logger.debug("cached miss on {}", identifiers);
        return Pair.of(null, EMPTY_TIMESERIES);
      } else {
        s_logger.warn("returned object {} from cache, not a UniqueIdentifier", value);
        return Pair.of(null, EMPTY_TIMESERIES);
      }
    } else {
      Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = _underlying.getHistoricalData(identifiers, currentDate, dataSource, dataProvider, dataField);
      _cache.put(new Element(key, tsPair.getFirst()));
      if (tsPair.getFirst() != null) {
        s_logger.debug("Retrieved {} for {}", tsPair.getFirst(), identifiers);
        _cache.put(new Element(tsPair.getFirst(), tsPair.getSecond()));
      } else {
        s_logger.debug("No data returned from underlying for {}", identifiers);
      }
      return tsPair;
    }
  }

  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, String dataSource, String dataProvider, String dataField) {
    return getHistoricalData(identifiers, (LocalDate) null, dataSource, dataProvider, dataField);
  }
  
  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, LocalDate currentDate, String dataSource, String dataProvider, String field,
      LocalDate start, boolean inclusiveStart, LocalDate end, boolean exclusiveEnd) {
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = getHistoricalData(identifiers, currentDate, dataSource, dataProvider, field);
    return getSubseries(start, inclusiveStart, end, exclusiveEnd, tsPair);
  }
  
  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, String dataSource, String dataProvider, String field, LocalDate start,
      boolean inclusiveStart, LocalDate end, boolean exclusiveEnd) {
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = getHistoricalData(identifiers, dataSource, dataProvider, field);
    return getSubseries(start, inclusiveStart, end, exclusiveEnd, tsPair);
  }

  private Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getSubseries(LocalDate start, boolean inclusiveStart, LocalDate end, boolean exclusiveEnd,
      Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair) {
    if (tsPair != null) {
      LocalDateDoubleTimeSeries timeSeries = (LocalDateDoubleTimeSeries) tsPair.getSecond().subSeries(start, inclusiveStart, end, !exclusiveEnd);
      return Pair.of(tsPair.getKey(), timeSeries);
    } else {
      return Pair.of(null, EMPTY_TIMESERIES);
    }
  }
  
 
  @Override
  public LocalDateDoubleTimeSeries getHistoricalData(UniqueIdentifier uid) {
    Element element = _cache.get(uid);
    if (element != null) {
      Serializable value = element.getValue();
      if (value instanceof LocalDateDoubleTimeSeries) {
        LocalDateDoubleTimeSeries ts = (LocalDateDoubleTimeSeries) value;
        s_logger.debug("retrieved time series: {} from cache", ts);
        return ts;
      } else {
        s_logger.error("returned object {} from cache, not a LocalDateDoubleTimeSeries", value);
        return EMPTY_TIMESERIES;
      }
    } else {
      LocalDateDoubleTimeSeries ts = _underlying.getHistoricalData(uid);
      _cache.put(new Element(uid, ts));
      return ts;
    }
  }

  @Override
  public LocalDateDoubleTimeSeries getHistoricalData(UniqueIdentifier uid, LocalDate start, boolean inclusiveStart, LocalDate end, boolean exclusiveEnd) {
    LocalDateDoubleTimeSeries timeseries = getHistoricalData(uid);
    if (!timeseries.isEmpty()) {
      return (LocalDateDoubleTimeSeries) timeseries.subSeries(start, inclusiveStart, end, !exclusiveEnd);
    } else {
      return timeseries;
    }
  }

  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, LocalDate currentDate, String configDocName) {
    MetaDataKey key = new MetaDataKey(configDocName, currentDate, identifiers, null, null, null);
    Element element = _cache.get(key);
    if (element != null) {
      Serializable value = element.getValue();
      if (value instanceof UniqueIdentifier) {
        UniqueIdentifier uid = (UniqueIdentifier) value;
        s_logger.debug("retrieved UID: {} from cache", uid);
        LocalDateDoubleTimeSeries timeSeries = getHistoricalData(uid);
        return new ObjectsPair<UniqueIdentifier, LocalDateDoubleTimeSeries>(uid, timeSeries);
      } else if (value == null) {
        s_logger.debug("cached miss on {}", identifiers);
        return Pair.of(null, EMPTY_TIMESERIES);
      } else {
        s_logger.warn("returned object {} from cache, not a UniqueIdentifier", value);
        return Pair.of(null, EMPTY_TIMESERIES);
      }
    } else {
      Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = _underlying.getHistoricalData(identifiers, currentDate, configDocName);
      _cache.put(new Element(key, tsPair.getFirst()));
      if (tsPair.getFirst() != null) {
        s_logger.debug("caching {} for {}", tsPair.getFirst(), identifiers);
        _cache.put(new Element(tsPair.getFirst(), tsPair.getSecond()));
      } else {
        s_logger.debug("no data returned from underlying for {}", identifiers);
      }
      return tsPair;
    }
  }
  
  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, String configDocName) {
    return getHistoricalData(identifiers, null, configDocName);
  }

  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, String configDocName, 
      LocalDate start, boolean inclusiveStart, LocalDate end, boolean exclusiveEnd) {
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = getHistoricalData(identifiers, configDocName);
    return getSubseries(start, inclusiveStart, end, exclusiveEnd, tsPair);
  }

  @Override
  public Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> getHistoricalData(IdentifierBundle identifiers, LocalDate currentDate, String configDocName, 
      LocalDate start, boolean inclusiveStart, LocalDate end, boolean exclusiveEnd) {
    Pair<UniqueIdentifier, LocalDateDoubleTimeSeries> tsPair = getHistoricalData(identifiers, currentDate, configDocName);
    return getSubseries(start, inclusiveStart, end, exclusiveEnd, tsPair);
  }

}
