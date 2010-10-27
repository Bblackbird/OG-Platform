/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.master;

import javax.time.calendar.LocalDate;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.ehcache.EHCacheUtils;

/**
 * 
 */
public class EHCachingHolidaySource extends MasterHolidaySource {
  
  /**
   * Cache key for holidays.
   */
  private static final String HOLIDAY_CACHE = "holiday";
  
  /**
   * The cache manager.
   */
  private final CacheManager _cacheManager;
  
  /**
   * The result cache.
   */
  private final Cache _holiday;
  
  /**
   * Creates the cache around an underlying holiday source.
   * @param underlying  the underlying data, not null
   * @param cacheManager  the cache manager, not null
   */
  public EHCachingHolidaySource(final HolidayMaster underlying, final CacheManager cacheManager) {
    super(underlying);

    ArgumentChecker.notNull(cacheManager, "cacheManager");
    _cacheManager = cacheManager;
    EHCacheUtils.addCache(cacheManager, HOLIDAY_CACHE);
    _holiday = EHCacheUtils.getCacheFromManager(cacheManager, HOLIDAY_CACHE);
  }

  // -------------------------------------------------------------------------
  
  public CacheManager getCacheManager() {
    return _cacheManager;
  }
  
  // -------------------------------------------------------------------------
  
  @Override
  protected boolean isHoliday(final HolidaySearchRequest request, final LocalDate dateToCheck) {
    if (isWeekend(dateToCheck)) {
      return true;
    }
    
    Element e = _holiday.get(request);
    if (e != null) {
      HolidayDocument doc = (HolidayDocument) e.getValue();
      return isHoliday(doc, dateToCheck);
    } else {
      HolidayDocument doc = getHolidayMaster().search(request).getFirstDocument();
      _holiday.put(new Element(request, doc));
      return isHoliday(doc, dateToCheck);
    }
  }
  
}
