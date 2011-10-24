/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.hsqldb.types.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;

import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ExternalIdSearch;
import com.opengamma.id.ObjectId;
import com.opengamma.id.ObjectIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.master.security.RawSecurity;
import com.opengamma.master.security.SecurityDocument;
import com.opengamma.master.security.SecurityHistoryRequest;
import com.opengamma.master.security.SecurityHistoryResult;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.SecurityMetaDataRequest;
import com.opengamma.master.security.SecurityMetaDataResult;
import com.opengamma.master.security.SecuritySearchRequest;
import com.opengamma.master.security.SecuritySearchResult;
import com.opengamma.masterdb.AbstractDocumentDbMaster;
import com.opengamma.masterdb.security.hibernate.HibernateSecurityMasterDetailProvider;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.db.DbDateUtils;
import com.opengamma.util.db.DbMapSqlParameterSource;
import com.opengamma.util.db.DbConnector;
import com.opengamma.util.paging.Paging;

/**
 * A security master implementation using a database for persistence.
 * <p>
 * This is a full implementation of the security master using an SQL database.
 * Full details of the API are in {@link SecurityMaster}.
 * <p>
 * This class uses SQL via JDBC. The SQL may be changed by subclassing the relevant methods.
 * <p>
 * This class is mutable but must be treated as immutable after configuration.
 */
public class DbSecurityMaster extends AbstractDocumentDbMaster<SecurityDocument> implements SecurityMaster {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(DbSecurityMaster.class);

  /**
   * The default scheme for unique identifiers.
   */
  public static final String IDENTIFIER_SCHEME_DEFAULT = "DbSec";
  /**
   * SQL select.
   */
  protected static final String SELECT =
      "SELECT " +
        "main.id AS doc_id, " +
        "main.oid AS doc_oid, " +
        "main.ver_from_instant AS ver_from_instant, " +
        "main.ver_to_instant AS ver_to_instant, " +
        "main.corr_from_instant AS corr_from_instant, " +
        "main.corr_to_instant AS corr_to_instant, " +
        "main.name AS name, " +
        "main.sec_type AS sec_type, " +
        "main.detail_type AS detail_type, " +
        "raw.raw_data AS raw_data, " +
        "i.key_scheme AS key_scheme, " +
        "i.key_value AS key_value, " +
        "sa.key AS security_attr_key, " +
        "sa.value AS security_attr_value ";
  /**
   * SQL from.
   */
  protected static final String FROM =
      "FROM sec_security main " +
        "LEFT JOIN sec_raw raw ON (raw.security_id = main.id) " +
        "LEFT JOIN sec_security2idkey si ON (si.security_id = main.id) " +
        "LEFT JOIN sec_idkey i ON (si.idkey_id = i.id) " +
        "LEFT JOIN sec_security_attribute sa ON (sa.security_id = main.id) ";
  /**
   * SQL select types.
   */
  protected static final String SELECT_TYPES = "SELECT DISTINCT main.sec_type AS sec_type FROM sec_security main";

  /**
   * The detail provider.
   */
  private SecurityMasterDetailProvider _detailProvider;

  /**
   * Creates an instance.
   * 
   * @param dbConnector  the database connector, not null
   */
  public DbSecurityMaster(final DbConnector dbConnector) {
    super(dbConnector, IDENTIFIER_SCHEME_DEFAULT);
    setDetailProvider(new HibernateSecurityMasterDetailProvider());
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the detail provider.
   * 
   * @return the detail provider
   */
  public SecurityMasterDetailProvider getDetailProvider() {
    return _detailProvider;
  }

  /**
   * Sets the detail provider.
   * 
   * @param detailProvider  the detail provider, not null
   */
  public void setDetailProvider(SecurityMasterDetailProvider detailProvider) {
    ArgumentChecker.notNull(detailProvider, "detailProvider");
    detailProvider.init(this);
    _detailProvider = detailProvider;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecurityMetaDataResult metaData(SecurityMetaDataRequest request) {
    ArgumentChecker.notNull(request, "request");
    SecurityMetaDataResult result = new SecurityMetaDataResult();
    if (request.isSecurityTypes()) {
      List<String> securityTypes = getJdbcTemplate().getJdbcOperations().queryForList(SELECT_TYPES, String.class);
      result.getSecurityTypes().addAll(securityTypes);
    }
    return result;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecuritySearchResult search(final SecuritySearchRequest request) {
    ArgumentChecker.notNull(request, "request");
    ArgumentChecker.notNull(request.getPagingRequest(), "request.pagingRequest");
    ArgumentChecker.notNull(request.getVersionCorrection(), "request.versionCorrection");
    s_logger.debug("search {}", request);
    
    final SecuritySearchResult result = new SecuritySearchResult();
    if ((request.getObjectIds() != null && request.getObjectIds().size() == 0) ||
        (ExternalIdSearch.canMatch(request.getExternalIdSearch()) == false)) {
      result.setPaging(Paging.of(request.getPagingRequest(), 0));
      return result;
    }
    final VersionCorrection vc = request.getVersionCorrection().withLatestFixed(now());
    final DbMapSqlParameterSource args = new DbMapSqlParameterSource()
      .addTimestamp("version_as_of_instant", vc.getVersionAsOf())
      .addTimestamp("corrected_to_instant", vc.getCorrectedTo())
      .addValueNullIgnored("name", getDialect().sqlWildcardAdjustValue(request.getName()))
      .addValueNullIgnored("sec_type", request.getSecurityType())
      .addValueNullIgnored("key_value", getDialect().sqlWildcardAdjustValue(request.getExternalIdValue()));
    if (request.getExternalIdSearch() != null) {
      int i = 0;
      for (ExternalId id : request.getExternalIdSearch()) {
        args.addValue("key_scheme" + i, id.getScheme().getName());
        args.addValue("key_value" + i, id.getValue());
        i++;
      }
    }
    
    searchWithPaging(request.getPagingRequest(), sqlSearchSecurities(request, args), args, new SecurityDocumentExtractor(), result);
    if (request.isFullDetail()) {
      loadDetail(result.getDocuments());
    }
    return result;
  }

  /**
   * Gets the SQL to search for documents.
   * 
   * @param request  the request, not null
   * @param args  the arguments to be updated if necessary, not null
   * @return the SQL search and count, not null
   */
  protected String[] sqlSearchSecurities(final SecuritySearchRequest request, final DbMapSqlParameterSource args) {
    String where = "WHERE ver_from_instant <= :version_as_of_instant AND ver_to_instant > :version_as_of_instant " +
                "AND corr_from_instant <= :corrected_to_instant AND corr_to_instant > :corrected_to_instant ";
    if (request.getName() != null) {
      where += getDialect().sqlWildcardQuery("AND UPPER(name) ", "UPPER(:name)", request.getName());
    }
    if (request.getSecurityType() != null) {
      where += "AND UPPER(sec_type) = UPPER(:sec_type) ";
    }
    if (request.getObjectIds() != null) {
      StringBuilder buf = new StringBuilder(request.getObjectIds().size() * 10);
      for (ObjectId objectId : request.getObjectIds()) {
        checkScheme(objectId);
        buf.append(extractOid(objectId)).append(", ");
      }
      buf.setLength(buf.length() - 2);
      where += "AND oid IN (" + buf + ") ";
    }
    if (request.getExternalIdSearch() != null && request.getExternalIdSearch().size() > 0) {
      where += sqlSelectMatchingSecurityKeys(request.getExternalIdSearch());
    }
    if (request.getExternalIdValue() != null) {
      where += sqlSelectExternalIdValue(request.getExternalIdValue());
    }
    where += sqlAdditionalWhere();
    
    String selectFromWhereInner = "SELECT sec_security.id FROM sec_security " +  where;
    SecurityMasterDetailProvider detailProvider = getDetailProvider();  // lock against change
    if (detailProvider != null) {
      selectFromWhereInner = detailProvider.extendSearch(request, args, "SELECT sec_security.id FROM sec_security ", where);
    }
    String inner = getDialect().sqlApplyPaging(selectFromWhereInner, "ORDER BY sec_security.id ", request.getPagingRequest());
    String search = sqlSelectFrom() + "WHERE main.id IN (" + inner + ") ORDER BY main.id" + sqlAdditionalOrderBy(false);
    String count = "SELECT COUNT(*) FROM sec_security " + where;
    return new String[] {search, count};
  }

  /**
   * Gets the SQL to match identifier value
   * 
   * @param identifierValue the identifier value, not null
   * @return the SQL, not null
   */
  protected String sqlSelectExternalIdValue(String identifierValue) {
    String select = "SELECT DISTINCT security_id " +
        "FROM sec_security2idkey, sec_security main " +
        "WHERE security_id = main.id " +
        "AND main.ver_from_instant <= :version_as_of_instant AND main.ver_to_instant > :version_as_of_instant " +
        "AND main.corr_from_instant <= :corrected_to_instant AND main.corr_to_instant > :corrected_to_instant " +
        "AND idkey_id IN ( SELECT id FROM sec_idkey WHERE " + getDialect().sqlWildcardQuery("UPPER(key_value) ", "UPPER(:key_value)", identifierValue) + ") ";
    return "AND id IN (" + select + ") ";
  }

  /**
   * Gets the SQL to match the {@code ExternalIdSearch}.
   * 
   * @param idSearch  the identifier search, not null
   * @return the SQL, not null
   */
  protected String sqlSelectMatchingSecurityKeys(final ExternalIdSearch idSearch) {
    switch (idSearch.getSearchType()) {
      case EXACT:
        return "AND id IN (" + sqlSelectMatchingSecurityKeysExact(idSearch) + ") ";
      case ALL:
        return "AND id IN (" + sqlSelectMatchingSecurityKeysAll(idSearch) + ") ";
      case ANY:
        return "AND id IN (" + sqlSelectMatchingSecurityKeysAny(idSearch) + ") ";
      case NONE:
        return "AND id NOT IN (" + sqlSelectMatchingSecurityKeysAny(idSearch) + ") ";
    }
    throw new UnsupportedOperationException("Search type is not supported: " + idSearch.getSearchType());
  }

  /**
   * Gets the SQL to find all the securities matching.
   * 
   * @param idSearch  the identifier search, not null
   * @return the SQL, not null
   */
  protected String sqlSelectMatchingSecurityKeysExact(final ExternalIdSearch idSearch) {
    // compare size of all matched to size in total
    // filter by dates to reduce search set
    String a = "SELECT security_id AS matched_security_id, COUNT(security_id) AS matched_count " +
      "FROM sec_security2idkey, sec_security main " +
      "WHERE security_id = main.id " +
      "AND main.ver_from_instant <= :version_as_of_instant AND main.ver_to_instant > :version_as_of_instant " +
      "AND main.corr_from_instant <= :corrected_to_instant AND main.corr_to_instant > :corrected_to_instant " +
      "AND idkey_id IN (" + sqlSelectMatchingSecurityKeysOr(idSearch) + ") " +
      "GROUP BY security_id " +
      "HAVING COUNT(security_id) >= " + idSearch.size() + " ";
    String b = "SELECT security_id AS total_security_id, COUNT(security_id) AS total_count " +
      "FROM sec_security2idkey, sec_security main " +
      "WHERE security_id = main.id " +
      "AND main.ver_from_instant <= :version_as_of_instant AND main.ver_to_instant > :version_as_of_instant " +
      "AND main.corr_from_instant <= :corrected_to_instant AND main.corr_to_instant > :corrected_to_instant " +
      "GROUP BY security_id ";
    String select = "SELECT matched_security_id AS security_id " +
      "FROM (" + a + ") AS a, (" + b + ") AS b " +
      "WHERE matched_security_id = total_security_id " +
        "AND matched_count = total_count ";
    return select;
  }

  /**
   * Gets the SQL to find all the securities matching.
   * 
   * @param idSearch  the identifier search, not null
   * @return the SQL, not null
   */
  protected String sqlSelectMatchingSecurityKeysAll(final ExternalIdSearch idSearch) {
    // only return security_id when all requested ids match (having count >= size)
    // filter by dates to reduce search set
    String select = "SELECT security_id " +
      "FROM sec_security2idkey, sec_security main " +
      "WHERE security_id = main.id " +
      "AND main.ver_from_instant <= :version_as_of_instant AND main.ver_to_instant > :version_as_of_instant " +
      "AND main.corr_from_instant <= :corrected_to_instant AND main.corr_to_instant > :corrected_to_instant " +
      "AND idkey_id IN (" + sqlSelectMatchingSecurityKeysOr(idSearch) + ") " +
      "GROUP BY security_id " +
      "HAVING COUNT(security_id) >= " + idSearch.size() + " ";
    return select;
  }

  /**
   * Gets the SQL to find all the securities matching any identifier.
   * 
   * @param idSearch  the identifier search, not null
   * @return the SQL, not null
   */
  protected String sqlSelectMatchingSecurityKeysAny(final ExternalIdSearch idSearch) {
    // optimized search for commons case of individual ORs
    // filter by dates to reduce search set
    String select = "SELECT DISTINCT security_id " +
      "FROM sec_security2idkey, sec_security main " +
      "WHERE security_id = main.id " +
      "AND main.ver_from_instant <= :version_as_of_instant AND main.ver_to_instant > :version_as_of_instant " +
      "AND main.corr_from_instant <= :corrected_to_instant AND main.corr_to_instant > :corrected_to_instant " +
      "AND idkey_id IN (" + sqlSelectMatchingSecurityKeysOr(idSearch) + ") ";
    return select;
  }

  /**
   * Gets the SQL to find all the ids for a single set of identifiers.
   * 
   * @param idSearch  the identifier search, not null
   * @return the SQL, not null
   */
  protected String sqlSelectMatchingSecurityKeysOr(final ExternalIdSearch idSearch) {
    String select = "SELECT id FROM sec_idkey ";
    for (int i = 0; i < idSearch.size(); i++) {
      select += (i == 0 ? "WHERE " : "OR ");
      select += "(key_scheme = :key_scheme" + i + " AND key_value = :key_value" + i + ") ";
    }
    return select;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecurityDocument get(final UniqueId uniqueId) {
    SecurityDocument doc = doGet(uniqueId, new SecurityDocumentExtractor(), "Security");
    loadDetail(Collections.singletonList(doc));
    return doc;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecurityDocument get(final ObjectIdentifiable objectId, final VersionCorrection versionCorrection) {
    SecurityDocument doc = doGetByOidInstants(objectId, versionCorrection, new SecurityDocumentExtractor(), "Holiday");
    loadDetail(Collections.singletonList(doc));
    return doc;
  }

  //-------------------------------------------------------------------------
  @Override
  public SecurityHistoryResult history(final SecurityHistoryRequest request) {
    SecurityHistoryResult result = doHistory(request, new SecurityHistoryResult(), new SecurityDocumentExtractor());
    if (request.isFullDetail()) {
      loadDetail(result.getDocuments());
    }
    return result;
  }

  //-------------------------------------------------------------------------
  /**
   * Loads the detail of the security for the document.
   * 
   * @param docs  the documents to load detail for, not null
   */
  protected void loadDetail(final List<SecurityDocument> docs) {
    SecurityMasterDetailProvider detailProvider = getDetailProvider();  // lock against change
    if (detailProvider != null) {
      for (SecurityDocument doc : docs) {
        if (!(doc.getSecurity() instanceof RawSecurity)) {
          doc.setSecurity(detailProvider.loadSecurityDetail(doc.getSecurity()));
        }
      }
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Inserts a new document.
   * 
   * @param document  the document, not null
   * @return the new document, not null
   */
  @Override
  protected SecurityDocument insert(final SecurityDocument document) {
    ArgumentChecker.notNull(document.getSecurity(), "document.security");
    
    final long docId = nextId("sec_security_seq");
    final long docOid = (document.getUniqueId() != null ? extractOid(document.getUniqueId()) : docId);
    // the arguments for inserting into the security table
    final DbMapSqlParameterSource securityArgs = new DbMapSqlParameterSource()
      .addValue("doc_id", docId)
      .addValue("doc_oid", docOid)
      .addTimestamp("ver_from_instant", document.getVersionFromInstant())
      .addTimestampNullFuture("ver_to_instant", document.getVersionToInstant())
      .addTimestamp("corr_from_instant", document.getCorrectionFromInstant())
      .addTimestampNullFuture("corr_to_instant", document.getCorrectionToInstant())
      .addValue("name", document.getSecurity().getName())
      .addValue("sec_type", document.getSecurity().getSecurityType());
    if (document.getSecurity() instanceof RawSecurity) {
      securityArgs.addValue("detail_type", "R");
    } else if (document.getSecurity().getClass() == ManageableSecurity.class) {
      securityArgs.addValue("detail_type", "M");
    } else {
      securityArgs.addValue("detail_type", "D");
    }
    // the arguments for inserting into the idkey tables
    final List<DbMapSqlParameterSource> assocList = new ArrayList<DbMapSqlParameterSource>();
    final List<DbMapSqlParameterSource> idKeyList = new ArrayList<DbMapSqlParameterSource>();
    for (ExternalId id : document.getSecurity().getExternalIdBundle()) {
      final DbMapSqlParameterSource assocArgs = new DbMapSqlParameterSource()
        .addValue("doc_id", docId)
        .addValue("key_scheme", id.getScheme().getName())
        .addValue("key_value", id.getValue());
      assocList.add(assocArgs);
      if (getJdbcTemplate().queryForList(sqlSelectIdKey(), assocArgs).isEmpty()) {
        // select avoids creating unnecessary id, but id may still not be used
        final long idKeyId = nextId("sec_idkey_seq");
        final DbMapSqlParameterSource idkeyArgs = new DbMapSqlParameterSource()
          .addValue("idkey_id", idKeyId)
          .addValue("key_scheme", id.getScheme().getName())
          .addValue("key_value", id.getValue());
        idKeyList.add(idkeyArgs);
      }
    }
    getJdbcTemplate().update(sqlInsertSecurity(), securityArgs);
    getJdbcTemplate().batchUpdate(sqlInsertIdKey(), idKeyList.toArray(new DbMapSqlParameterSource[idKeyList.size()]));
    getJdbcTemplate().batchUpdate(sqlInsertSecurityIdKey(), assocList.toArray(new DbMapSqlParameterSource[assocList.size()]));
    // set the uniqueId
    final UniqueId uniqueId = createUniqueId(docOid, docId);
    document.getSecurity().setUniqueId(uniqueId);
    document.setUniqueId(uniqueId);
    // store the detail
    if (document.getSecurity() instanceof RawSecurity) {
      storeRawSecurityDetail((RawSecurity) document.getSecurity());
    } else {
      final SecurityMasterDetailProvider detailProvider = getDetailProvider();
      if (detailProvider != null) {
        detailProvider.storeSecurityDetail(document.getSecurity());
      }
    }

    Map<String, String> attributes = new HashMap<String, String>(document.getSecurity().getAttributes());
    final List<DbMapSqlParameterSource> securityAttributeList = Lists.newArrayList();
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      final long securityAttrId = nextId("sec_security_attr_seq");
      final DbMapSqlParameterSource tradeAttributeArgs = new DbMapSqlParameterSource()
              .addValue("attr_id", securityAttrId)
              .addValue("security_id", docId)
              .addValue("security_oid", docOid)
              .addValue("key", entry.getKey())
              .addValue("value", entry.getValue());
      securityAttributeList.add(tradeAttributeArgs);
    }

    getJdbcTemplate().batchUpdate(sqlInsertSecurityAttributes(), securityAttributeList.toArray(new DbMapSqlParameterSource[securityAttributeList.size()]));
    return document;
  }

  private void storeRawSecurityDetail(RawSecurity security) {
    final DbMapSqlParameterSource rawSecurityArgs = new DbMapSqlParameterSource()
      .addValue("security_id", extractRowId(security.getUniqueId()))
      .addValue("raw_data", new SqlLobValue(security.getRawData(), getDialect().getLobHandler()), Types.BLOB);
    getJdbcTemplate().update(sqlInsertRawSecurity(), rawSecurityArgs);
  }

  /**
   * Gets the SQL for inserting a raw security.
   * 
   * @return the SQL, not null
   */
  protected String sqlInsertRawSecurity() {
    return "INSERT INTO sec_raw (security_id, raw_data) VALUES (:security_id, :raw_data)";
  }

  /**
   * Gets the SQL for inserting a document.
   * 
   * @return the SQL, not null
   */
  protected String sqlInsertSecurity() {
    return "INSERT INTO sec_security " +
              "(id, oid, ver_from_instant, ver_to_instant, corr_from_instant, corr_to_instant, name, sec_type, detail_type) " +
            "VALUES " +
              "(:doc_id, :doc_oid, :ver_from_instant, :ver_to_instant, :corr_from_instant, :corr_to_instant, :name, :sec_type, :detail_type)";
  }

  /**
   * Gets the SQL for inserting a security-idkey association.
   * 
   * @return the SQL, not null
   */
  protected String sqlInsertSecurityIdKey() {
    return "INSERT INTO sec_security2idkey " +
              "(security_id, idkey_id) " +
            "VALUES " +
              "(:doc_id, (" + sqlSelectIdKey() + "))";
  }

  /**
   * Gets the SQL for selecting an idkey.
   * 
   * @return the SQL, not null
   */
  protected String sqlSelectIdKey() {
    return "SELECT id FROM sec_idkey WHERE key_scheme = :key_scheme AND key_value = :key_value";
  }

  /**
   * Gets the SQL for inserting an idkey.
   * 
   * @return the SQL, not null
   */
  protected String sqlInsertIdKey() {
    return "INSERT INTO sec_idkey (id, key_scheme, key_value) " +
            "VALUES (:idkey_id, :key_scheme, :key_value)";
  }

    /**
   * Gets the SQL for inserting trade attributes.
   *
   * @return the SQL, not null.
   */
  protected String sqlInsertSecurityAttributes() {
    return "INSERT INTO sec_security_attribute " +
              "(id, security_id, security_oid, key, value) " +
           "VALUES (:attr_id, :security_id, :security_oid, :key, :value)";
  }

  //-------------------------------------------------------------------------
  @Override
  protected String sqlSelectFrom() {
    return SELECT + FROM;
  }

  @Override
  protected String mainTableName() {
    return "sec_security";
  }

  //-------------------------------------------------------------------------
  /**
   * Mapper from SQL rows to a SecurityDocument.
   */
  protected final class SecurityDocumentExtractor implements ResultSetExtractor<List<SecurityDocument>> {
    private long _lastDocId = -1;
    private ManageableSecurity _security;
    private List<SecurityDocument> _documents = new ArrayList<SecurityDocument>();

    @Override
    public List<SecurityDocument> extractData(final ResultSet rs) throws SQLException, DataAccessException {
      while (rs.next()) {
        final long docId = rs.getLong("DOC_ID");
        if (_lastDocId != docId) {
          _lastDocId = docId;
          buildSecurity(rs, docId);
        }
        final String idScheme = rs.getString("KEY_SCHEME");
        final String idValue = rs.getString("KEY_VALUE");
        if (idScheme != null && idValue != null) {
          ExternalId id = ExternalId.of(idScheme, idValue);
          _security.setExternalIdBundle(_security.getExternalIdBundle().withExternalId(id));
        }


        final String securityAttrKey = rs.getString("SECURITY_ATTR_KEY");
        final String securityAttrValue = rs.getString("SECURITY_ATTR_VALUE");
        if (securityAttrKey != null && securityAttrValue != null) {
          _security.addAttribute(securityAttrKey, securityAttrValue);
        }



      }
      return _documents;
    }

    private void buildSecurity(final ResultSet rs, final long docId) throws SQLException {
      final long docOid = rs.getLong("DOC_OID");
      final Timestamp versionFrom = rs.getTimestamp("VER_FROM_INSTANT");
      final Timestamp versionTo = rs.getTimestamp("VER_TO_INSTANT");
      final Timestamp correctionFrom = rs.getTimestamp("CORR_FROM_INSTANT");
      final Timestamp correctionTo = rs.getTimestamp("CORR_TO_INSTANT");
      final String name = rs.getString("NAME");
      final String type = rs.getString("SEC_TYPE");
      UniqueId uniqueId = createUniqueId(docOid, docId);
      String detailType = rs.getString("DETAIL_TYPE");
      if (detailType.equalsIgnoreCase("R")) {
        LobHandler lob = getDialect().getLobHandler();
        byte[] rawData = lob.getBlobAsBytes(rs, "RAW_DATA");
        _security = new RawSecurity(type, rawData);
        _security.setUniqueId(uniqueId);
        _security.setName(name);  
      } else {
        _security = new ManageableSecurity(uniqueId, name, type, ExternalIdBundle.EMPTY);
      }
      SecurityDocument doc = new SecurityDocument(_security);
      doc.setVersionFromInstant(DbDateUtils.fromSqlTimestamp(versionFrom));
      doc.setVersionToInstant(DbDateUtils.fromSqlTimestampNullFarFuture(versionTo));
      doc.setCorrectionFromInstant(DbDateUtils.fromSqlTimestamp(correctionFrom));
      doc.setCorrectionToInstant(DbDateUtils.fromSqlTimestampNullFarFuture(correctionTo));
      doc.setUniqueId(uniqueId);
      _documents.add(doc);
    }
  }

}
