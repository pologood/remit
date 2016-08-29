/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.TransferDetailMapper;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.InternalErrorException;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月22日;
//-------------------------------------------------------
@Service
public class TransferDetailManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchManager.class);

  @Autowired
  private TransferDetailMapper transferDetailMapper;

  @Transactional
  public ApiResult<?> update(TransferDetail detail) {
    TransferDetail detailInDB = transferDetailMapper.selectByTransferId(detail.getAppId(), detail.getBatchNo(),
        detail.getTransferId());
    if (Objects.isNull(detailInDB)) return ApiResult.notFound();
    if (!Objects.equals(detail.getAmount(), detailInDB.getAmount())
        || !Objects.equals(detail.getInAccountId(), detailInDB.getInAccountId())) {
      LOGGER.error("{}:detail:{} detail in db:{}", Exceptions.DETAIL_INVALID, detail, detailInDB);
      return ApiResult.notAcceptable(Exceptions.DETAIL_INVALID);
    }
    if (transferDetailMapper.update(detail) < 1) {
      LOGGER.error("{} detail:{}", Exceptions.DATA_PERSISTENCE_FAILED, detail);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED);
    }
    return ApiResult.ok();
  }

  public List<TransferDetail> selectByBatchNo(int appId, String batchNo) {
    return transferDetailMapper.selectByBatchNo(appId, batchNo);
  }

  public int add(List<TransferDetail> details) {
    return transferDetailMapper.add(details);
  }

  @Transactional
  public void update(List<TransferDetail> details) {
    for (TransferDetail detail : details)
      if (ApiResult.isNotOK(update(detail))) throw new InternalErrorException("callback details error");
  }

}
