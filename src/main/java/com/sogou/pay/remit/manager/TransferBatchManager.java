/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.entity.User;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.TransferBatchMapper;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.BadRequestException;
import com.sogou.pay.remit.model.ErrorCode;
import com.sogou.pay.remit.model.InternalErrorException;

import commons.utils.LocalDateTimeJsonSerializer;
import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------
@Service
public class TransferBatchManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferBatchManager.class);

  @Autowired
  private BankInfoManager bankInfoManager;

  @Autowired
  private TransferBatchMapper transferBatchMapper;

  @Autowired
  private TransferDetailManager transferDetailManager;

  public ApiResult<?> get(int appId, String batchNo) {
    return get(appId, batchNo, false);
  }

  public ApiResult<?> get(int appId, String batchNo, boolean withDetails) {
    TransferBatch batch = transferBatchMapper.selectByBatchNo(appId, batchNo);
    if (Objects.isNull(batch)) {
      LOGGER.error("[get]{}:appId={},batchNo={}", Exceptions.ENTITY_NOT_FOUND, appId, batchNo);
      return ApiResult.notFound();
    }
    if (withDetails) batch.setDetails(transferDetailManager.selectByBatchNo(appId, batchNo));
    return new ApiResult<>(batch);
  }

  public ApiResult<List<TransferBatch>> list(Channel channel, Status status) {
    return list(channel, status, null);
  }

  public ApiResult<List<TransferBatch>> list(Channel channel, Status status, User user) {
    return list(channel, status, user, null, null);
  }

  public ApiResult<List<TransferBatch>> list(Channel channel, Status status, User user, LocalDateTime beginTime,
      LocalDateTime endTime) {
    List<TransferBatch> batchs = transferBatchMapper.list(channel, status, user, beginTime, endTime);
    if (Objects.equals(channel, Channel.PAY)) batchs.forEach(
        batch -> batch.setDetails(transferDetailManager.selectByBatchNo(batch.getAppId(), batch.getBatchNo())));
    return CollectionUtils.isEmpty(batchs) ? new ApiResult<>(ErrorCode.NOT_FOUND) : new ApiResult<>(batchs);
  }

  public ApiResult<List<TransferBatch>> listApproved() {
    return list(AUDIT_APPROVED_CONDITIONS);
  }

  public ApiResult<List<TransferBatch>> listToNotify() {
    List<TransferBatch> batchs = transferBatchMapper.listNotify();
    return CollectionUtils.isEmpty(batchs) ? new ApiResult<>(ErrorCode.NOT_FOUND) : new ApiResult<>(batchs);
  }

  public ApiResult<List<TransferBatch>> list(List<Tuple2<Status, BigDecimal>> conditions) {
    List<TransferBatch> batchs = transferBatchMapper.listWithStatusAndAmount(conditions);
    return CollectionUtils.isEmpty(batchs) ? new ApiResult<>(ErrorCode.NOT_FOUND) : new ApiResult<>(batchs);
  }

  @Transactional
  public ApiResult<?> add(TransferBatch batch) {
    return add(batch, false);
  }

  public ApiResult<?> add(TransferBatch batch, boolean needReturn) {
    ApiResult<?> busiCheckResult = busiCheck(batch);
    if (ApiResult.isNotOK(busiCheckResult)) {
      LOGGER.error("[busiCheck]{},batch:{}", busiCheckResult.getMessage(), batch);
      return busiCheckResult;
    }
    if (ApiResult.isOK(get(batch.getAppId(), batch.getBatchNo()))) {
      LOGGER.error("[add]{}:appId={} batchNo={}", Exceptions.BATCHNO_INVALID, batch.getAppId(), batch.getBatchNo());
      return ApiResult.badRequest(Exceptions.BATCHNO_INVALID);
    }
    if (isLackOfBankInfo(batch))
      setBankInfo(batch, bankInfoManager.getBankInfo(batch.getOutAccountId(), batch.getChannel()));
    try {
      transferBatchMapper.add(batch);
      transferDetailManager.add(batch.getDetails());
      return needReturn ? new ApiResult<>(batch) : ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error("[add]{}:{}", Exceptions.DATA_PERSISTENCE_FAILED, batch, e);
      throw new InternalErrorException(e);
    }
  }

  private boolean isLackOfBankInfo(TransferBatch batch) {
    return StringUtils.isAnyBlank(batch.getLoginName(), batch.getOutAccountName())
        || Objects.isNull(batch.getBusiCode()) || Objects.isNull(batch.getBusiMode())
        || Objects.isNull(batch.getBranchCode()) || Objects.isNull(batch.getCurrency())
        || (Objects.equals(Channel.PAY, batch.getChannel()) && Objects.isNull(batch.getSettleChannel()));
  }

  private void setBankInfo(TransferBatch batch, BankInfo bankInfo) {
    if (Objects.isNull(bankInfo)) throw new BadRequestException(Exceptions.BANKINFO_NOT_FOUND);
    if (StringUtils.isBlank(batch.getLoginName())) batch.setLoginName(bankInfo.getLoginName());
    if (StringUtils.isBlank(batch.getOutAccountName())) batch.setOutAccountName(bankInfo.getAccountName());
    if (Objects.isNull(batch.getBusiMode())) batch.setBusiMode(bankInfo.getBusiMode());
    if (Objects.isNull(batch.getBusiCode())) batch.setBusiCode(bankInfo.getBusiCode());
    if (Objects.isNull(batch.getTransType())) batch.setTransType(bankInfo.getTransType());
    if (Objects.isNull(batch.getBranchCode())) batch.setBranchCode(bankInfo.getBranchCode());
    if (Objects.isNull(batch.getCurrency())) batch.setCurrency(bankInfo.getCurrency());
    if (Objects.equals(Channel.PAY, batch.getChannel()) && Objects.isNull(batch.getSettleChannel()))
      batch.setSettleChannel(bankInfo.getSettleChannel());
  }

  @Transactional
  public ApiResult<?> audit(int appId, String batchNo, User user, Status status, String opinion) {
    return update(appId, batchNo, user, status, null, opinion, null, null, null);
  }

  @Transactional
  public ApiResult<?> callBack(int appId, String batchNo, Status status, String errMsg, String outTradeNo,
      Integer successCount, BigDecimal successAmount) {
    return update(appId, batchNo, null, status, errMsg, null, outTradeNo, successCount, successAmount);
  }

  private ApiResult<?> update(int appId, String batchNo, User user, Status status, String errMsg, String opinion,
      String outTradeNo, Integer successCount, BigDecimal successAmount) {
    ApiResult<?> result = get(appId, batchNo);
    if (ApiResult.isNotOK(result)) return result;
    TransferBatch batch = (TransferBatch) result.getData();
    Status oldStatus = batch.getStatus();
    if (Objects.equals(oldStatus, status)) return ApiResult.ok();
    if (!TransferBatch.Status.isShiftValid(oldStatus, status)) {
      LOGGER.error("[update]{}:from {} to {}", Exceptions.STATUS_INVALID, oldStatus, status);
      return ApiResult.badRequest(Exceptions.STATUS_INVALID);
    }
    if (transferBatchMapper
        .update(setUpdateItems(batch, errMsg, opinion, status, user, outTradeNo, successCount, successAmount)) < 1) {
      LOGGER.error("[update]{}:appId={},batchNo={},from {} to {},outErrMsg={},opinion={}",
          Exceptions.DATA_PERSISTENCE_FAILED, appId, batchNo, oldStatus, status, errMsg, opinion);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED);
    }
    return new ApiResult<>(batchNo);
  }

  private TransferBatch setUpdateItems(TransferBatch batch, String outErrMsg, String opinion, Status status, User user,
      String outTradeNo, Integer successCount, BigDecimal successAmount) {
    setAuditor(batch, user);
    batch.setOutErrMsg(outErrMsg);
    batch.setOutTradeNo(outTradeNo);
    if (StringUtils.isNotBlank(opinion)) {
      batch.getAuditOpinions().add(opinion);
      batch.getAuditTimes().add(LocalDateTime.now().format(LocalDateTimeJsonSerializer.dtFmt));
    } else {
      batch.setAuditOpinions(null);
      batch.setAuditTimes(null);
    }
    batch.setStatus(status);
    batch.setSuccessCount(successCount);
    batch.setSuccessAmount(successAmount);
    return batch;
  }

  private void setAuditor(TransferBatch batch, User user) {
    if (Objects.isNull(user)) return;
    Integer id = user.getId();
    for (int i = batch.getAuditTimes().size(); i-- > 0; id *= 1000);
    batch.setAuditor(Objects.isNull(batch.getAuditor()) ? id : batch.getAuditor() + id);
  }

  private ApiResult<?> busiCheck(TransferBatch batch) {
    if (!Objects.equals(batch.getTransferCount(), batch.getDetails().size()))
      return ApiResult.badRequest(Exceptions.DETAIL_INVALID);
    BigDecimal sum = BigDecimal.ZERO;
    for (TransferDetail detail : batch.getDetails()) {
      detail.setAppId(batch.getAppId());
      detail.setBatchNo(batch.getBatchNo());
      sum = sum.add(detail.getAmount());
    }
    return 0 == batch.getTransferAmount().compareTo(sum) ? ApiResult.ok()
        : ApiResult.badRequest(Exceptions.AMOUNT_INVALID);
  }

  @Transactional
  public ApiResult<?> batchUpdateTransferBatchStatus(User user, Integer appId, List<String> batchNos, Status status) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (String batchNo : batchNos) {
      try {
        result.add(ImmutableMap.of("batchNo", batchNo, "result",
            ApiResult.isOK(this.audit(appId, batchNo, user, status, null))));
      } catch (Exception e) {
        LOGGER.error("{} appId={} batchNo={} status to {}", Exceptions.DATA_PERSISTENCE_FAILED, appId, batchNo, status,
            e);
        result.add(ImmutableMap.of("batchNo", batchNo, "result", false));
      }
    }
    return new ApiResult<>(result);
  }

  private static final int JUNIOR_AUDIT_LIMIT = 30_0000, SENIOR_AUDIT_LIMIT = 50_0000;

  private static final List<Tuple2<Status, BigDecimal>> AUDIT_APPROVED_CONDITIONS = Arrays.asList(
      new Tuple2<>(Status.JUNIOR_APPROVED, new BigDecimal(JUNIOR_AUDIT_LIMIT)),
      new Tuple2<>(Status.SENIOR_APPROVED, new BigDecimal(SENIOR_AUDIT_LIMIT)),
      new Tuple2<>(Status.FINAL_APPROVED, new BigDecimal(Integer.MAX_VALUE)));

  public void notify(TransferBatch batch) {
    if (transferBatchMapper.logNotify(batch) < 1) LOGGER.error("notify log error:batch={}", batch);
  }

}
