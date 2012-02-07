/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.batch;

import com.opengamma.DataNotFoundException;
import com.opengamma.batch.BatchMaster;
import com.opengamma.batch.RunCreationMode;
import com.opengamma.batch.SnapshotMode;
import com.opengamma.batch.domain.MarketData;
import com.opengamma.batch.domain.MarketDataValue;
import com.opengamma.batch.domain.RiskRun;
import com.opengamma.batch.rest.BatchRunSearchRequest;
import com.opengamma.engine.view.CycleInfo;
import com.opengamma.engine.view.ViewComputationResultModel;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.masterdb.AbstractDbMaster;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.db.DbConnector;
import com.opengamma.util.paging.Paging;
import com.opengamma.util.paging.PagingRequest;
import com.opengamma.util.tuple.Pair;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.opengamma.util.db.DbUtil.eqOrIsNull;

public class DbBatchMaster extends AbstractDbMaster implements BatchMaster {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(DbBatchMaster.class);

  
  final private DbBatchWriter _dbBatchWriter; 

  /**
   * Creates an instance.
   *
   * @param dbConnector  the database connector, not null
   */
  public DbBatchMaster(final DbConnector dbConnector) {
    super(dbConnector, BATCH_IDENTIFIER_SCHEME);
    _dbBatchWriter = new DbBatchWriter(dbConnector);
  }  


  @Override
  public RiskRun getRiskRun(final ObjectId uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    s_logger.info("Getting BatchDocument by unique id: ", uniqueId);
    final Long id = extractOid(uniqueId);
    return getHibernateTransactionTemplate().execute(new HibernateCallback<RiskRun>() {
      @Override
      public RiskRun doInHibernate(Session session) throws HibernateException, SQLException {
        RiskRun run = _dbBatchWriter.getRiskRunById(id);
        if (run != null) {
          return run;
        } else {
          throw new DataNotFoundException("Batch run not found: " + id);
        }
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Pair<List<MarketData>, Paging> getMarketData(final PagingRequest pagingRequest) {
    s_logger.info("Getting markte datas: ", pagingRequest);

    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Pair<List<MarketData>, Paging>>() {
      @Override
      public Pair<List<MarketData>, Paging> doInTransaction(final TransactionStatus status) {
        final DetachedCriteria criteria = DetachedCriteria.forClass(MarketData.class);

        List<MarketData> results = Collections.emptyList();
        if (!pagingRequest.equals(PagingRequest.NONE)) {
          results = getHibernateTemplate().findByCriteria(
            criteria,
            pagingRequest.getFirstItem(),
            pagingRequest.getPagingSize());
        }
        //
        Paging paging;
        if (pagingRequest.equals(PagingRequest.ALL)) {
          paging = Paging.of(pagingRequest, results);
        } else {
          criteria.setProjection(Projections.rowCount());
          Long totalCount = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
          paging = Paging.of(pagingRequest, totalCount.intValue());
        }
        //     
        return Pair.of(results, paging);
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public MarketData getMarketDataById(final ObjectId batchSnapshotId) {
    s_logger.info("Getting the batch data snapshot: {}", batchSnapshotId);

    final Long marketDataPK = extractOid(batchSnapshotId);

    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<MarketData>() {
      @Override
      public MarketData doInTransaction(final TransactionStatus status) {
        return getHibernateTemplate().get(MarketData.class, marketDataPK);
      }
    });
  }

  @Override
  @SuppressWarnings("unchecked")
  public Pair<List<MarketDataValue>, Paging> getMarketDataValues(final ObjectId marketDataId, final PagingRequest pagingRequest) {
    s_logger.info("Getting the batch data snapshot: {}", marketDataId);

    final Long marketDataPK = extractOid(marketDataId);

    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Pair<List<MarketDataValue>, Paging>>() {
      @Override
      public Pair<List<MarketDataValue>, Paging> doInTransaction(final TransactionStatus status) {

        final DetachedCriteria criteria = DetachedCriteria.forClass(MarketDataValue.class);
        criteria.add(Restrictions.eq("marketDataId", marketDataPK));
        //
        List<MarketDataValue> results = Collections.emptyList();
        if (!pagingRequest.equals(PagingRequest.NONE)) {
          results = getHibernateTemplate().findByCriteria(
            criteria,
            pagingRequest.getFirstItem(),
            pagingRequest.getPagingSize());
        }
        //
        Paging paging;
        if (pagingRequest.equals(PagingRequest.ALL)) {
          paging = Paging.of(pagingRequest, results);
        } else {
          criteria.setProjection(Projections.rowCount());
          Long totalCount = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
          paging = Paging.of(pagingRequest, totalCount.intValue());
        }
        //
        return Pair.of(results, paging);
      }
    });

  }

  @Override
  public void deleteMarketData(final ObjectId batchSnapshotId) {
    s_logger.info("Deleting market data snapshot: ", batchSnapshotId);
    getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Void>() {
      @Override
      public Void doInTransaction(final TransactionStatus status) {
        _dbBatchWriter.deleteSnapshotInTransaction(batchSnapshotId);
        return null;
      }
    });
  }

  //--------------------------------------------------------------------------------------------------------------------


  @Override
  @SuppressWarnings("unchecked")
  public Pair<List<RiskRun>, Paging> searchRiskRun(final BatchRunSearchRequest requestRun) {
    s_logger.info("Searching BatchDocuments: ", requestRun);

    final DetachedCriteria criteria = DetachedCriteria.forClass(RiskRun.class);


    if (requestRun.getValuationTime() != null) {
      criteria.add(
        Restrictions.eq("valuationTime", requestRun.getValuationTime()));
    }

    if (requestRun.getVersionCorrection() != null) {
      criteria.add(
        Restrictions.eq("versionCorrection", requestRun.getVersionCorrection()));
    }


    if (requestRun.getMarketDataUid() != null) {
      criteria.createCriteria("marketData")
        .add(Restrictions.eq("baseUidScheme", requestRun.getMarketDataUid().getScheme()))
        .add(Restrictions.eq("baseUidValue", requestRun.getMarketDataUid().getValue()))
        .add(eqOrIsNull("baseUidVersion", requestRun.getMarketDataUid().getVersion()));
      //.addOrder(Order.asc("baseUid"));
    }

    if (requestRun.getViewDefinitionUid() != null) {
      criteria.add(Restrictions.eq("viewDefinitionUidScheme", requestRun.getViewDefinitionUid().getScheme()))
        .add(Restrictions.eq("viewDefinitionUidValue", requestRun.getViewDefinitionUid().getValue()))
        .add(eqOrIsNull("viewDefinitionUidVersion", requestRun.getViewDefinitionUid().getVersion()));
      //.addOrder(Order.asc("viewDefinitionUid"));
    }

    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Pair<List<RiskRun>, Paging>>() {
      @Override
      public Pair<List<RiskRun>, Paging> doInTransaction(final TransactionStatus status) {
        //
        final PagingRequest pagingRequest = requestRun.getPagingRequest();
        List<RiskRun> results = Collections.emptyList();
        Paging paging;
        if (!pagingRequest.equals(PagingRequest.NONE)) {
          if (pagingRequest.equals(PagingRequest.ALL)) {
            criteria.addOrder(Order.asc("valuationTime"));
            results = getHibernateTemplate().findByCriteria(
              criteria,
              pagingRequest.getFirstItem(),
              pagingRequest.getPagingSize());
        //
            paging = Paging.of(pagingRequest, results);
        } else {
          criteria.setProjection(Projections.rowCount());
          Long totalCount = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
            paging = Paging.of(pagingRequest, totalCount.intValue());
            //
          criteria.setProjection(null);
          criteria.setResultTransformer(Criteria.ROOT_ENTITY);
        criteria.addOrder(Order.asc("valuationTime"));
            results = getHibernateTemplate().findByCriteria(
            criteria,
              pagingRequest.getFirstItem(),
              pagingRequest.getPagingSize());
      }
        } else {
          paging = Paging.of(PagingRequest.NONE, 0);
  }
        return Pair.of(results, paging);
          }
        });
      }

  //--------------------------------------------------------------------------------------------------------------------

  @Override
  public void addValuesToMarketData(final ObjectId marketDataId, final Set<MarketDataValue> values) {
      getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Void>() {
        @Override
        public Void doInTransaction(final TransactionStatus status) {
        _dbBatchWriter.addValuesToMarketDataInTransaction(marketDataId, values);
          return null;
        }
      });
    }
  
    @Override
  public RiskRun startRiskRun(final CycleInfo cycleInfo,
                              final Map<String, String> batchParameters,
                              final RunCreationMode runCreationMode,
                              final SnapshotMode snapshotMode) {
    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<RiskRun>() {
        @Override
      public RiskRun doInTransaction(final TransactionStatus status) {
        return _dbBatchWriter.startBatchInTransaction(cycleInfo, batchParameters, runCreationMode, snapshotMode);
        }
      });
    }
  

    @Override
  public void deleteRiskRun(final ObjectId batchUniqueId) {
      getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Void>() {
        @Override
        public Void doInTransaction(final TransactionStatus status) {
          _dbBatchWriter.deleteBatchInTransaction(batchUniqueId);
          return null;
        }
      });
    }
  
    @Override
  public void endRiskRun(final ObjectId batchUniqueId) {
      getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Void>() {
        @Override
        public Void doInTransaction(final TransactionStatus status) {
          _dbBatchWriter.endBatchInTransaction(batchUniqueId);
          return null;
        }
      });
    }
  
    @Override
  public MarketData createMarketData(final UniqueId marketDataSnapshotUniqueId) {
    return getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<MarketData>() {
        @Override
      public MarketData doInTransaction(final TransactionStatus status) {
        return _dbBatchWriter.createOrGetMarketDataInTransaction(marketDataSnapshotUniqueId);
        }
      });
    }
  
    // -------------------------------------------------------------------------------------------------------------------
  
    @Override
  public void addJobResults(final ObjectId riskRunId, final ViewComputationResultModel result) {
      getTransactionTemplateRetrying(getMaxRetries()).execute(new TransactionCallback<Void>() {
        @Override
        public Void doInTransaction(final TransactionStatus status) {
        _dbBatchWriter.addJobResultsInTransaction(riskRunId, result);
          return null;
        }
      });
    }
}
