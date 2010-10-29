/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.coppclark;

import static com.opengamma.financial.world.region.InMemoryRegionMaster.ISO_COUNTRY_2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.time.calendar.LocalDate;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatters;

import au.com.bytecode.opencsv.CSVReader;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.financial.Currency;
import com.opengamma.financial.world.exchange.ExchangeUtils;
import com.opengamma.financial.world.holiday.HolidayType;
import com.opengamma.financial.world.holiday.master.HolidayDocument;
import com.opengamma.financial.world.holiday.master.HolidayMaster;
import com.opengamma.financial.world.holiday.master.HolidaySearchRequest;
import com.opengamma.financial.world.holiday.master.HolidaySearchResult;
import com.opengamma.financial.world.holiday.master.HolidaySource;
import com.opengamma.financial.world.holiday.master.ManageableHoliday;
import com.opengamma.financial.world.holiday.master.MasterHolidaySource;
import com.opengamma.financial.world.region.RegionFileReader;
import com.opengamma.id.IdentificationScheme;
import com.opengamma.id.Identifier;
import com.opengamma.util.ArgumentChecker;

/**
 * Reads the holiday data from the Copp-Clark data source.
 */
public class CoppClarkHolidayFileReader {

  /**
   * The Copp Clark scheme.
   */
  private static final IdentificationScheme COPP_CLARK_SCHEME = new IdentificationScheme("COPP_CLARK");
  /**
   * The date format.
   */
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatters.pattern("yyyyMMdd");
  /**
   * An empty list of dates.
   */
  private static final List<LocalDate> EMPTY_DATE_LIST = Collections.emptyList();
  /**
   * Path to Copp-Clark holiday files.
   */
  public static final String HOLIDAYS_DIR_PATH = RegionFileReader.WORLD_DATA_DIR_PATH + File.separator + "holiday-calendars" + File.separator + "copp-clark";
  /**
   * Path to currency specific holiday calendars CSV file.
   */
  public static final String CURRENCY_HOLIDAYS_FILE_PATH = HOLIDAYS_DIR_PATH + File.separator + "Currencies_20100610.csv";
  /**
   * Path to financial center holiday calendars CSV file.
   */
  public static final String FINANCIAL_CENTRES_HOLIDAYS_FILE_PATH = HOLIDAYS_DIR_PATH + File.separator + "FinancialCentres_20100610.csv";
  /**
   * Path to exchange settlement holiday calendars CSV file.
   */
  public static final String EXCHANGE_SETTLEMENT_HOLIDAYS_FILE_PATH = HOLIDAYS_DIR_PATH + File.separator + "ExchangeSettlement_20100610.csv";
  /**
   * Path to exchange trading holiday calendars CSV file.
   */
  public static final String EXCHANGE_TRADING_HOLIDAYS_FILE_PATH = HOLIDAYS_DIR_PATH + File.separator + "ExchangeTrading_20100610.csv";
  
//  private static final String EXCHANGE_HOLIDAYS_REPOST_FILE_PATH = RegionFileReader.WORLD_DATA_DIR_PATH + File.separator + "exchanges" + File.separator + "THR_20100630.csv.txt";
  /**
   * The file version.
   */
  private static final String VERSION = "20100610";
  private static final String CURRENCY_HOLIDAYS_RESOURCE = "/com/coppclark/holiday/Currencies_" + VERSION + ".csv";
  private static final String FINANCIAL_CENTERS_RESOURCE = "/com/coppclark/holiday/FinancialCentres_" + VERSION + ".csv";
  private static final String EXCHANGE_SETTLEMENT_RESOURCE = "/com/coppclark/holiday/ExchangeSettlement_" + VERSION + ".csv";;
  private static final String EXCHANGE_TRADING_RESOURCE = "/com/coppclark/holiday/ExchangeTrading_" + VERSION + ".csv";

  /**
   * The holiday master to populate.
   */
  private HolidayMaster _holidayMaster;

  /**
   * Creates a populated in-memory holiday source.
   * @param holidayMaster  the holiday master to populate, not null
   * @return the holiday source, not null
   */
  public static HolidaySource createPopulated(HolidayMaster holidayMaster) {
    CoppClarkHolidayFileReader fileReader = new CoppClarkHolidayFileReader(holidayMaster);
    InputStream currencyStream = fileReader.getClass().getResourceAsStream(CURRENCY_HOLIDAYS_RESOURCE);
    InputStream financialCentersStream = fileReader.getClass().getResourceAsStream(FINANCIAL_CENTERS_RESOURCE);
    InputStream exchangeSettlementStream = fileReader.getClass().getResourceAsStream(EXCHANGE_SETTLEMENT_RESOURCE);
    InputStream exchangeTradingStream = fileReader.getClass().getResourceAsStream(EXCHANGE_TRADING_RESOURCE);
    fileReader.readStreams(currencyStream, financialCentersStream, exchangeSettlementStream, exchangeTradingStream);
    return new MasterHolidaySource(holidayMaster);
  }

  /**
   * Creates an instance with a master to populate.
   * @param holidayMaster  the holiday master, not null
   */
  public CoppClarkHolidayFileReader(HolidayMaster holidayMaster) {
    ArgumentChecker.notNull(holidayMaster, "holidayMaster");
    _holidayMaster = holidayMaster;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the holiday master.
   * @return the holiday master, not null
   */
  public HolidayMaster getExchangeMaster() {
    return _holidayMaster;
  }

  //-------------------------------------------------------------------------
  /**
   * Reads the streams to create the master.
   * @param currencies  the currency stream, not null
   * @param financialCenters  the centers stream, not null
   * @param exchangeSettlement  the settlement exchanges stream, not null
   * @param exchangeTrading  the trading exchanges stream, not null
   */
  public void readFiles(File currencies, File financialCenters, File exchangeSettlement, File exchangeTrading) {
    try {
      parseCurrencyFile(new FileInputStream(currencies));
      parseFinancialCentersFile(new FileInputStream(financialCenters));
      parseExchangeSettlementFile(new FileInputStream(exchangeSettlement));
      parseExchangeTradingFile(new FileInputStream(exchangeTrading));
    } catch (IOException ioe) {
      throw new OpenGammaRuntimeException("Problem parsing exchange/currency data files", ioe);
    }
  }

  /**
   * Reads the streams to create the master.
   * @param currencyStream  the currency stream, not null
   * @param financialCentersStream  the centers stream, not null
   * @param exchangeSettlementStream  the settlement exchanges stream, not null
   * @param exchangeTradingStream  the trading exchanges stream, not null
   */
  public void readStreams(InputStream currencyStream, InputStream financialCentersStream, InputStream exchangeSettlementStream, InputStream exchangeTradingStream) {
    try {
      parseCurrencyFile(currencyStream);
      parseFinancialCentersFile(financialCentersStream);
      parseExchangeSettlementFile(exchangeSettlementStream);
      parseExchangeTradingFile(exchangeTradingStream);
    } catch (IOException ioe) {
      throw new OpenGammaRuntimeException("Problem parsing exchange/currency data files", ioe);
    }
  }

  //-------------------------------------------------------------------------
  private void parseCurrencyFile(InputStream currencyStream) throws IOException {
    final List<String> columnNames = Arrays.asList(new String[] {
      "CenterID", "ISOCurrencyCode", "ISOCountryCode", "RelatedFinancialCentre", 
      "EventYear", "EventDate", "EventDayOfWeek", "EventName", "FileType" });
    final int ccIdx = columnNames.indexOf("CenterID");
    final int isoCurrencyIdx = columnNames.indexOf("ISOCurrencyCode");
    final int eventDateIdx = columnNames.indexOf("EventDate");
    
    Map<String, HolidayDocument> map = new HashMap<String, HolidayDocument>(512);
    CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(currencyStream)));
    String[] row = reader.readNext(); // throw away the header
    while ((row = reader.readNext()) != null) {
      String ccId = row[ccIdx].trim();
      String currencyISO = row[isoCurrencyIdx].trim();
      Currency currency = Currency.getInstance(currencyISO);  // validates format
      String eventDateStr = row[eventDateIdx];
      LocalDate eventDate =  LocalDate.parse(eventDateStr, DATE_FORMAT);
      HolidayDocument doc = map.get(ccId);
      if (doc == null) {
        doc = new HolidayDocument(new ManageableHoliday(currency, EMPTY_DATE_LIST));
        doc.setProviderId(Identifier.of(COPP_CLARK_SCHEME, ccId));
        map.put(ccId, doc);
      }
      doc.getHoliday().getHolidayDates().add(eventDate);
    }
    addDocuments(map);
  }

  private void parseFinancialCentersFile(InputStream financialCentersStream) throws IOException {
    final List<String> columnNames = Arrays.asList(new String[] {
      "CenterID", "ISOCurrencyCode", "ISOCountryCode", "FinancialCentre", 
      "UN/LOCODE", "EventYear", "EventDate", "EventDayOfWeek", "EventName", "FileType" });
    final int ccIdx = columnNames.indexOf("CenterID");
    final int isoCountryIdx = columnNames.indexOf("ISOCountryCode");
    final int eventDateIdx = columnNames.indexOf("EventDate");
    
    Map<String, HolidayDocument> map = new HashMap<String, HolidayDocument>(512);
    CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(financialCentersStream)));
    String[] row = reader.readNext(); // throw away the header
    while ((row = reader.readNext()) != null) {
      String ccId = row[ccIdx].trim();
      String isoCountry = row[isoCountryIdx];
      String eventDateStr = row[eventDateIdx];
      LocalDate eventDate =  LocalDate.parse(eventDateStr, DATE_FORMAT);
      Identifier regionId = Identifier.of(ISO_COUNTRY_2, isoCountry);
      HolidayDocument doc = map.get(ccId);
      if (doc == null) {
        doc = new HolidayDocument(new ManageableHoliday(HolidayType.BANK, regionId, EMPTY_DATE_LIST));
        doc.setProviderId(Identifier.of(COPP_CLARK_SCHEME, ccId));
        map.put(ccId, doc);
      }
      doc.getHoliday().getHolidayDates().add(eventDate);
    }
    addDocuments(map);
  }

  private void parseExchangeSettlementFile(InputStream exchangeSettlementStream) throws IOException {
    final List<String> columnNames = Arrays.asList(new String[] {
      "CenterID", "ISO MIC Code", "ISOCountryCode", "ExchangeName", "EventYear",
      "EventDate", "EventDayOfWeek", "EventName", "FileType" });
    final int ccIdx = columnNames.indexOf("CenterID");
    final int isoMICCodeIdx = columnNames.indexOf("ISO MIC Code");
    final int eventDateIdx = columnNames.indexOf("EventDate");
    
    Map<String, HolidayDocument> map = new HashMap<String, HolidayDocument>(512);
    CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(exchangeSettlementStream)));
    String[] row = reader.readNext(); // throw away the header
    while ((row = reader.readNext()) != null) {
      String ccId = row[ccIdx].trim();
      String isoMICCode = row[isoMICCodeIdx];
      String eventDateStr = row[eventDateIdx];
      LocalDate eventDate =  LocalDate.parse(eventDateStr, DATE_FORMAT);
      Identifier micId = new Identifier(ExchangeUtils.ISO_MIC, isoMICCode);
      HolidayDocument doc = map.get(ccId);
      if (doc == null) {
        doc = new HolidayDocument(new ManageableHoliday(HolidayType.SETTLEMENT, micId, EMPTY_DATE_LIST));
        doc.setProviderId(Identifier.of(COPP_CLARK_SCHEME, ccId));
        map.put(ccId, doc);
      }
      doc.getHoliday().getHolidayDates().add(eventDate);
    }
    addDocuments(map);
  }

  private void parseExchangeTradingFile(InputStream exchangeTradingStream) throws IOException {
    final List<String> columnNames = Arrays.asList(new String[] {
      "CenterID", "ISO MIC Code", "ISOCountryCode", "ExchangeName", "EventYear",
      "EventDate", "EventDayOfWeek", "EventName", "FileType" });
    final int ccIdx = columnNames.indexOf("CenterID");
    final int isoMICCodeIdx = columnNames.indexOf("ISO MIC Code");
    final int eventDateIdx = columnNames.indexOf("EventDate");
    
    Map<String, HolidayDocument> map = new HashMap<String, HolidayDocument>(512);
    CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(exchangeTradingStream)));
    String[] row = reader.readNext(); // throw away the header
    while ((row = reader.readNext()) != null) {
      String ccId = row[ccIdx].trim();
      String isoMICCode = row[isoMICCodeIdx];
      String eventDateStr = row[eventDateIdx];
      LocalDate eventDate =  LocalDate.parse(eventDateStr, DATE_FORMAT);
      Identifier micId = new Identifier(ExchangeUtils.ISO_MIC, isoMICCode);
      HolidayDocument doc = map.get(ccId);
      if (doc == null) {
        doc = new HolidayDocument(new ManageableHoliday(HolidayType.TRADING, micId, EMPTY_DATE_LIST));
        doc.setProviderId(Identifier.of(COPP_CLARK_SCHEME, ccId));
        map.put(ccId, doc);
      }
      doc.getHoliday().getHolidayDates().add(eventDate);
    }
    addDocuments(map);
  }

  /**
   * Adds the documents to the database.
   * @param map  the map of documents, not null
   */
  private void addDocuments(Map<String, HolidayDocument> map) {
    for (HolidayDocument doc : map.values()) {
      Collections.sort(doc.getHoliday().getHolidayDates());
      HolidaySearchRequest search = new HolidaySearchRequest(doc.getHoliday().getType());
      search.setProviderId(doc.getProviderId());
      HolidaySearchResult result = _holidayMaster.search(search);
      if (result.getDocuments().size() == 0) {
        // add new data
        _holidayMaster.add(doc);
      } else if (result.getDocuments().size() == 1) {
        // update existing data
        HolidayDocument existing = result.getFirstDocument();
        doc.setHolidayId(existing.getHolidayId());
        doc.getHoliday().setUniqueIdentifier(existing.getHolidayId());
        // merge dates
        if (doc.getHoliday().getHolidayDates().size() > 0) {
          LocalDate newFirstDate = doc.getHoliday().getHolidayDates().get(0);
          List<LocalDate> existingDates = existing.getHoliday().getHolidayDates();
          for (int i = 0; i < existingDates.size(); i++) {
            LocalDate existingDate = existingDates.get(i);
            if (existingDate.getYear() < newFirstDate.getYear()) {
              doc.getHoliday().getHolidayDates().add(i, existingDate);
            } else {
              break;
            }
          }
        }
        // only update if changed
        doc.setVersionFromInstant(null);
        doc.setVersionToInstant(null);
        doc.setCorrectionFromInstant(null);
        doc.setCorrectionToInstant(null);
        existing.setVersionFromInstant(null);
        existing.setVersionToInstant(null);
        existing.setCorrectionFromInstant(null);
        existing.setCorrectionToInstant(null);
        if (doc.equals(existing) == false) {  // only update if changed
          _holidayMaster.update(doc);
        }
      } else {
        throw new IllegalStateException("Multiple rows in database for Copp Clark ID: " + doc.getProviderId().getValue() + " " + doc.getHoliday().getType());
      }
    }
  }

}
