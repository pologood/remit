/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.TransferBatchMapper;
import com.sogou.pay.remit.mapper.TransferDetailMapper;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.InternalErrorException;

import commons.utils.MapHelper;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@Component
public class TransferBatchManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchManager.class);

  @Autowired
  private AppManager appManager;

  @Autowired
  private BankInfoManager bankInfoManager;

  @Autowired
  private TransferBatchMapper transferBatchMapper;

  @Autowired
  private TransferDetailMapper transferDetailMapper;

  public ApiResult<?> get(int appId, String batchNo) {
    return get(appId, batchNo, false);
  }

  public ApiResult<?> get(int appId, String batchNo, boolean withDetails) {
    TransferBatch batch = transferBatchMapper.selectByBatchNo(appId, batchNo);
    if (Objects.isNull(batch)) {
      LOGGER.error("[get]{}:appId={},batchNo={}", Exceptions.ENTITY_NOT_FOUND.getErrorMsg(), appId, batchNo);
      return ApiResult.notFound();
    }
    if (withDetails) batch.setDetails(transferDetailMapper.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
    return new ApiResult<>(batch);
  }

  public ApiResult<?> list(Status status) {
    List<TransferBatch> batchs = transferBatchMapper.list(status.getValue());
    return CollectionUtils.isEmpty(batchs) ? ApiResult.notFound() : new ApiResult<>(batchs);
  }

  public ApiResult<?> add(TransferBatch batch) {
    return add(batch, false);
  }

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public ApiResult<?> add(TransferBatch batch, boolean needReturn) {
    ApiResult<?> busiCheckResult = busiCheck(batch);
    if (ApiResult.isNotOK(busiCheckResult)) {
      LOGGER.error("[busiCheck]{},batch:{}", busiCheckResult.getMessage(), batch);
      return busiCheckResult;
    }
    if (StringUtils.isAnyBlank(batch.getLoginName(), batch.getOutAccountName(), batch.getCurrency())
        || Objects.isNull(batch.getBusiCode()) || Objects.isNull(batch.getBusiMode())
        || Objects.isNull(batch.getBranchCode())
        || (Objects.equals(Channel.PAY, batch.getChannel()) && Objects.isNull(batch.getSettleChannel())))
      setBankInfo(batch, bankInfoManager.getBankInfo(batch.getOutAccountId(), batch.getChannel()));
    if (ApiResult.isOK(get(batch.getAppId(), batch.getBatchNo()))) {
      LOGGER.error("[add]{}:appId={} batchNo={}", Exceptions.BATCHNO_INVALID.getErrorMsg(), batch.getAppId(),
          batch.getBatchNo());
      return ApiResult.badRequest(Exceptions.BATCHNO_INVALID.getErrorMsg());
    }
    try {
      transferBatchMapper.add(batch);
      batch.getDetails().forEach(detail -> {
        detail.setAppId(batch.getAppId());
        detail.setBatchNo(batch.getBatchNo());
      });
      transferDetailMapper.add(batch.getDetails());
      return needReturn ? new ApiResult<>(batch) : ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error(String.format("[add]%s:%s", Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg(), batch), e);
      throw new InternalErrorException(e);
    }
  }

  private void setBankInfo(TransferBatch batch, BankInfo bankInfo) {
    if (StringUtils.isBlank(batch.getLoginName())) batch.setLoginName(bankInfo.getLoginName());
    if (StringUtils.isBlank(batch.getOutAccountName())) batch.setOutAccountName(bankInfo.getAccountName());
    if (Objects.isNull(batch.getBusiMode())) batch.setBusiMode(bankInfo.getBusiMode());
    if (Objects.isNull(batch.getBusiCode())) batch.setBusiCode(bankInfo.getBusiCode());
    if (Objects.isNull(batch.getTransType())) batch.setTransType(bankInfo.getTransType());
    if (Objects.isNull(batch.getBranchCode())) batch.setBranchCode(bankInfo.getBranchCode());
    if (StringUtils.isBlank(batch.getCurrency())) batch.setCurrency(bankInfo.getCurrency());
    if (Objects.equals(Channel.PAY, batch.getChannel()) && Objects.isNull(batch.getSettleChannel()))
      batch.setSettleChannel(bankInfo.getSettleChannel());
  }

  public ApiResult<?> update(int appId, String batchNo, Status status, Optional<String> errMsg, String opinion) {
    ApiResult<?> result = get(appId, batchNo);
    if (ApiResult.isNotOK(result)) return result;
    TransferBatch batch = (TransferBatch) result.getData();
    Status oldStatus = batch.getStatus();
    if (Objects.equals(oldStatus, status)) return ApiResult.ok();
    if (!TransferBatch.Status.isShiftValid(oldStatus, status)) {
      LOGGER.error("[update]{}:from{}to{}", Exceptions.STATUS_INVALID.getErrorMsg(), oldStatus, status);
      return ApiResult.badRequest(Exceptions.STATUS_INVALID.getErrorMsg());
    }
    if (transferBatchMapper.update(setUpdateItems(batch, errMsg, opinion, status)) < 1) {
      LOGGER.error("[update]{}:appId={},batchNo={},from{}to{},outErrMsg={},opinion={}",
          Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg(), appId, batchNo, oldStatus, status, errMsg, opinion);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED.getErrorMsg());
    }
    return ApiResult.ok();
  }

  private TransferBatch setUpdateItems(TransferBatch batch, Optional<String> outErrMsg, String opinion, Status status) {
    batch.setOutErrMsg(outErrMsg.orElse(null));
    if (StringUtils.isNotBlank(opinion)) {
      batch.setList();
      batch.getAuditOpinionList().add(opinion);
      batch.getAuditTimeList().add(LocalDateTime.now());
      batch.setString();
    } else {
      batch.setAuditOpinions(null);
      batch.setAuditTimes(null);
    }
    batch.setStatus(status);
    return batch;
  }

  private ApiResult<?> busiCheck(TransferBatch batch) {
    ApiResult<?> signCheckResult = checkSign(batch);
    if (ApiResult.isNotOK(signCheckResult)) return signCheckResult;
    BigDecimal detailsAmountSum = getDetailsAmountSum(batch.getDetails());
    return 0 == batch.getTransferAmount().compareTo(detailsAmountSum) ? ApiResult.ok()
        : ApiResult.badRequest(Exceptions.AMOUNT_INVALID.getErrorMsg());
  }

  private ApiResult<?> checkSign(TransferBatch batch) {
    return Objects.nonNull(batch) && appManager.checkSign(MapHelper.convertToMap(batch)) ? ApiResult.ok()
        : ApiResult.badRequest(Exceptions.SIGN_INVALID.getErrorMsg());
  }

  private BigDecimal getDetailsAmountSum(List<TransferDetail> details) {
    BigDecimal sum = BigDecimal.ZERO;
    if (CollectionUtils.isNotEmpty(details)) for (TransferDetail detail : details)
      sum = sum.add(detail.getAmount());
    return sum;
  }

}
