/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.enums.Channel;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.TransferBatchMapper;
import com.sogou.pay.remit.model.ApiResult;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@Component
public class TransferBatchManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchManager.class);

  @Autowired
  private BankInfoManager bankInfoManager;

  @Autowired
  private TransferBatchMapper mapper;

  public ApiResult<?> get(int appId, String batchNo) {
    TransferBatch batch = mapper.selectByBatchNo(appId, batchNo);
    if (Objects.isNull(batch)) {
      LOGGER.error("[get]{}:appId={},batchNo={}", Exceptions.ENTITY_NOT_FOUND.getErrorMsg(), appId, batchNo);
      return ApiResult.notFound();
    }
    return new ApiResult<>(batch);
  }

  public ApiResult<?> list(int status) {
    List<TransferBatch> batchs = mapper.list(status);
    return CollectionUtils.isEmpty(batchs) ? ApiResult.notFound() : new ApiResult<>(batchs);
  }

  public ApiResult<?> add(TransferBatch batch) {
    return add(batch, false);
  }

  public ApiResult<?> add(TransferBatch batch, boolean needReturn) {
    Channel channel = Channel.getChannel(batch);
    if (StringUtils.isAnyBlank(batch.getLoginName(), batch.getOutAccountName(), batch.getBusiCode(),
        batch.getBusiMode(), batch.getBranchCode(), batch.getCurrency())
        || (Objects.equals(Channel.PAY, channel) && StringUtils.isBlank(batch.getSettleChannel())))
      setBankInfo(batch, bankInfoManager.getBankInfo(batch.getOutAccountId(), channel), channel);
    if (ApiResult.isOK(get(batch.getAppId(), batch.getBatchNo()))) {
      LOGGER.error("[add]{}:appId={} batchNo={}", Exceptions.BATCHNO_INVALID.getErrorMsg(), batch.getAppId(),
          batch.getBatchNo());
      return ApiResult.badRequest(Exceptions.BATCHNO_INVALID.getErrorMsg());
    }
    if (mapper.add(batch) < 1) {
      LOGGER.error("[add]{}:{}", Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg(), batch);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg());
    }
    return needReturn ? new ApiResult<>(batch) : ApiResult.ok();
  }

  private void setBankInfo(TransferBatch batch, BankInfo bankInfo, Channel channel) {
    if (StringUtils.isBlank(batch.getLoginName())) batch.setLoginName(bankInfo.getLoginName());
    if (StringUtils.isBlank(batch.getOutAccountName())) batch.setOutAccountName(bankInfo.getAccountName());
    if (StringUtils.isBlank(batch.getBusiMode())) batch.setBusiMode(bankInfo.getBusiMode());
    if (StringUtils.isBlank(batch.getBusiCode())) batch.setLoginName(bankInfo.getBusiCode());
    if (StringUtils.isBlank(batch.getBranchCode())) batch.setBusiCode(bankInfo.getBranchCode());
    if (StringUtils.isBlank(batch.getCurrency())) batch.setCurrency(bankInfo.getCurrency());
    if (Objects.equals(Channel.PAY, channel) && StringUtils.isBlank(batch.getSettleChannel()))
      batch.setSettleChannel(bankInfo.getSettleChannel());
  }

  public ApiResult<?> update(int appId, String batchNo, Integer status, String errMsg, String opinion) {
    ApiResult<?> result = get(appId, batchNo);
    if (ApiResult.isNotOK(result)) return result;
    TransferBatch batch = (TransferBatch) result.getData();
    int oldStatus = batch.getStatus();
    if (Objects.equals(oldStatus, status)) return ApiResult.ok();
    if (!TransferBatch.Status.isShiftValid(oldStatus, status)) {
      LOGGER.error("[update]{}:from{}to{}", Exceptions.STATUS_INVALID.getErrorMsg(), oldStatus, status);
      return ApiResult.badRequest(Exceptions.STATUS_INVALID.getErrorMsg());
    }
    if (mapper.update(setUpdateItems(batch, errMsg, opinion, status)) < 1) {
      LOGGER.error("[update]{}:appId={},batchNo={},from{}to{},outErrMsg={},opinion={}",
          Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg(), appId, batchNo, oldStatus, status, errMsg, opinion);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg());
    }
    return ApiResult.ok();
  }

  private TransferBatch setUpdateItems(TransferBatch batch, String outErrMsg, String opinion, int status) {
    batch.setList();
    if (StringUtils.isNotBlank(outErrMsg)) batch.setOutErrMsg(outErrMsg);
    if (StringUtils.isNotBlank(opinion)) {
      batch.getAuditOpinionList().add(opinion);
      batch.getAuditTimeList().add(LocalDateTime.now());
    }
    batch.setString();
    batch.setStatus(status);
    return batch;
  }

}
