/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.master.memory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.Instant;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.opengamma.DataNotFoundException;
import com.opengamma.financial.world.holiday.master.HolidayDocument;
import com.opengamma.financial.world.holiday.master.HolidayMaster;
import com.opengamma.financial.world.holiday.master.HolidaySearchHistoricRequest;
import com.opengamma.financial.world.holiday.master.HolidaySearchHistoricResult;
import com.opengamma.financial.world.holiday.master.HolidaySearchRequest;
import com.opengamma.financial.world.holiday.master.HolidaySearchResult;
import com.opengamma.financial.world.holiday.master.ManageableHoliday;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.id.UniqueIdentifierSupplier;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.RegexUtils;
import com.opengamma.util.db.Paging;

/**
 * A simple, in-memory implementation of {@code HolidayMaster}.
 * <p>
 * This master does not support versioning of holidays.
 * <p>
 * This implementation does not copy stored elements, making it thread-hostile.
 * As such, this implementation is currently most useful for testing scenarios.
 */
public class InMemoryHolidayMaster implements HolidayMaster {

  /**
   * The default scheme used for each {@link UniqueIdentifier}.
   */
  public static final String DEFAULT_UID_SCHEME = "Memory";

  /**
   * A cache of holidays by identifier.
   */
  private final ConcurrentMap<UniqueIdentifier, HolidayDocument> _holidays = new ConcurrentHashMap<UniqueIdentifier, HolidayDocument>();
  /**
   * The supplied of identifiers.
   */
  private final Supplier<UniqueIdentifier> _uidSupplier;

  /**
   * Creates an empty holiday master using the default scheme for any {@link UniqueIdentifier}s created.
   */
  public InMemoryHolidayMaster() {
    this(new UniqueIdentifierSupplier(DEFAULT_UID_SCHEME));
  }

  /**
   * Creates an instance specifying the supplier of unique identifiers.
   * 
   * @param uidSupplier  the supplier of unique identifiers, not null
   */
  public InMemoryHolidayMaster(final Supplier<UniqueIdentifier> uidSupplier) {
    ArgumentChecker.notNull(uidSupplier, "uidSupplier");
    _uidSupplier = uidSupplier;
  }

  //-------------------------------------------------------------------------
  @Override
  public HolidaySearchResult search(final HolidaySearchRequest request) {
    ArgumentChecker.notNull(request, "request");
    final HolidaySearchResult result = new HolidaySearchResult();
    Collection<HolidayDocument> docs = _holidays.values();
    if (request.getCurrency() != null) {
      docs = Collections2.filter(docs, new Predicate<HolidayDocument>() {
        @Override
        public boolean apply(final HolidayDocument doc) {
          return doc.getHoliday().getCurrencyISO() != null &&
            doc.getHoliday().getCurrencyISO().equals(request.getCurrency().getISOCode());
        }
      });
    }
    if (request.getRegionIdentifiers() != null) {
      docs = Collections2.filter(docs, new Predicate<HolidayDocument>() {
        @Override
        public boolean apply(final HolidayDocument doc) {
          return doc.getHoliday().getRegionId() != null &&
            request.getRegionIdentifiers().contains(doc.getHoliday().getRegionId());
        }
      });
    }
    if (request.getExchangeIdentifiers() != null) {
      docs = Collections2.filter(docs, new Predicate<HolidayDocument>() {
        @Override
        public boolean apply(final HolidayDocument doc) {
          return doc.getHoliday().getExchangeId() != null &&
            request.getExchangeIdentifiers().contains(doc.getHoliday().getExchangeId());
        }
      });
    }
    final String name = request.getName();
    if (name != null) {
      docs = Collections2.filter(docs, new Predicate<HolidayDocument>() {
        @Override
        public boolean apply(final HolidayDocument doc) {
          return RegexUtils.wildcardsToPattern(name).matcher(doc.getName()).matches();
        }
      });
    }
    if (request.getType() != null) {
      docs = Collections2.filter(docs, new Predicate<HolidayDocument>() {
        @Override
        public boolean apply(final HolidayDocument doc) {
          return doc.getHoliday().getType() == request.getType();
        }
      });
    }
    result.setPaging(Paging.of(docs, request.getPagingRequest()));
    result.getDocuments().addAll(request.getPagingRequest().select(docs));
    return result;
  }

  //-------------------------------------------------------------------------
  @Override
  public HolidayDocument get(final UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "uid");
    final HolidayDocument document = _holidays.get(uid);
    if (document == null) {
      throw new DataNotFoundException("Holiday not found: " + uid);
    }
    return document;
  }

  //-------------------------------------------------------------------------
  @Override
  public HolidayDocument add(final HolidayDocument document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getName(), "document.name");
    ArgumentChecker.notNull(document.getHoliday(), "document.holiday");
    
    final UniqueIdentifier uid = _uidSupplier.get();
    final ManageableHoliday holiday = new ManageableHoliday(document.getHoliday());
    holiday.setUniqueIdentifier(uid);
    final Instant now = Instant.nowSystemClock();
    final HolidayDocument doc = new HolidayDocument(holiday);
    doc.setVersionFromInstant(now);
    doc.setCorrectionFromInstant(now);
    _holidays.put(uid, doc);  // unique identifier should be unique
    return doc;
  }

  //-------------------------------------------------------------------------
  @Override
  public HolidayDocument update(final HolidayDocument document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getName(), "document.name");
    ArgumentChecker.notNull(document.getHoliday(), "document.holiday");
    ArgumentChecker.notNull(document.getHolidayId(), "document.holidayId");
    
    final UniqueIdentifier uid = document.getHolidayId();
    final Instant now = Instant.nowSystemClock();
    final HolidayDocument storedDocument = _holidays.get(uid);
    if (storedDocument == null) {
      throw new DataNotFoundException("Holiday not found: " + uid);
    }
    final ManageableHoliday holiday = new ManageableHoliday(document.getHoliday());
    final HolidayDocument doc = new HolidayDocument(holiday);
    doc.setVersionFromInstant(now);
    doc.setCorrectionFromInstant(now);
    if (_holidays.replace(uid, storedDocument, document) == false) {
      throw new IllegalArgumentException("Concurrent modification");
    }
    return document;
  }

  //-------------------------------------------------------------------------
  @Override
  public void remove(final UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "uid");
    
    if (_holidays.remove(uid) == null) {
      throw new DataNotFoundException("Holiday not found: " + uid);
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public HolidaySearchHistoricResult searchHistoric(final HolidaySearchHistoricRequest request) {
    ArgumentChecker.notNull(request, "request");
    ArgumentChecker.notNull(request.getHolidayId(), "request.holidayId");
    
    final HolidaySearchHistoricResult result = new HolidaySearchHistoricResult();
    final HolidayDocument doc = get(request.getHolidayId());
    if (doc != null) {
      result.getDocuments().add(doc);
    }
    result.setPaging(Paging.of(result.getDocuments()));
    return result;
  }

  @Override
  public HolidayDocument correct(final HolidayDocument document) {
    return update(document);
  }

}
