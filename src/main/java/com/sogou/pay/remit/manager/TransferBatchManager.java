/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.sogou.pay.remit.entity.TransferBatch.TransferBatchUpdateStatusDto;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.mapper.TransferBatchMapper;
import com.sogou.pay.remit.mapper.TransferDetailMapper;
import com.sogou.pay.remit.model.ApiResult;
import com.sogou.pay.remit.model.BadRequestException;
import com.sogou.pay.remit.model.InternalErrorException;

import commons.utils.LocalDateTimeJsonSerializer;
import commons.utils.MapHelper;
import commons.utils.Tuple2;
import commons.utils.cmb.XmlPacket;

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
      LOGGER.error("[get]{}:appId={},batchNo={}", Exceptions.ENTITY_NOT_FOUND.getErrMsg(), appId, batchNo);
      return ApiResult.notFound();
    }
    if (withDetails) batch.setDetails(transferDetailMapper.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
    return new ApiResult<>(batch);
  }

  public ApiResult<?> list(Status status) {
    List<TransferBatch> batchs = transferBatchMapper.list(status.getValue());
    return CollectionUtils.isEmpty(batchs) ? ApiResult.notFound() : new ApiResult<>(batchs);
  }

  public ApiResult<?> list() {
    return list(AUDIT_APPROVED_CONDITIONS);
  }

  public ApiResult<?> list(List<Tuple2<Status, BigDecimal>> conditions) {
    List<TransferBatch> batchs = transferBatchMapper.listWithStatusAndAmount(conditions);
    return CollectionUtils.isEmpty(batchs) ? ApiResult.notFound() : new ApiResult<>(batchs);
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
      LOGGER.error("[add]{}:appId={} batchNo={}", Exceptions.BATCHNO_INVALID.getErrMsg(), batch.getAppId(),
          batch.getBatchNo());
      return ApiResult.badRequest(Exceptions.BATCHNO_INVALID.getErrMsg());
    }
    if (isLackOfBankInfo(batch))
      setBankInfo(batch, bankInfoManager.getBankInfo(batch.getOutAccountId(), batch.getChannel()));
    try {
      transferBatchMapper.add(batch);
      transferDetailMapper.add(batch.getDetails());
      return needReturn ? new ApiResult<>(batch) : ApiResult.ok();
    } catch (Exception e) {
      LOGGER.error(String.format("[add]%s:%s", Exceptions.DATA_PERSISTENCE_FAILED.getErrMsg(), batch), e);
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
    if (Objects.isNull(bankInfo)) throw new BadRequestException(Exceptions.BANKINFO_NOT_FOUND.getErrMsg());
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

  public ApiResult<?> update(int appId, String batchNo, Integer userId, Status status, Optional<String> errMsg,
      String opinion) {
    ApiResult<?> result = get(appId, batchNo);
    if (ApiResult.isNotOK(result)) return result;
    TransferBatch batch = (TransferBatch) result.getData();
    Status oldStatus = batch.getStatus();
    if (Objects.equals(oldStatus, status)) return ApiResult.ok();
    if (!TransferBatch.Status.isShiftValid(oldStatus, status)) {
      LOGGER.error("[update]{}:from{}to{}", Exceptions.STATUS_INVALID.getErrMsg(), oldStatus, status);
      return ApiResult.badRequest(Exceptions.STATUS_INVALID.getErrMsg());
    }
    if (transferBatchMapper.update(setUpdateItems(batch, errMsg, opinion, status, userId)) < 1) {
      LOGGER.error("[update]{}:appId={},batchNo={},from{}to{},outErrMsg={},opinion={}",
          Exceptions.DATA_PERSISTENCE_FAILED.getErrMsg(), appId, batchNo, oldStatus, status, errMsg, opinion);
      return ApiResult.internalError(Exceptions.DATA_PERSISTENCE_FAILED.getErrMsg());
    }
    return ApiResult.ok();
  }

  private TransferBatch setUpdateItems(TransferBatch batch, Optional<String> outErrMsg, String opinion, Status status,
      Integer userId) {
    batch.setOutErrMsg(outErrMsg.orElse(null));
    if (StringUtils.isNotBlank(opinion)) {
      batch.getAuditOpinions().add(opinion);
      batch.getAuditTimes().add(LocalDateTime.now().format(LocalDateTimeJsonSerializer.dtFmt));
    } else {
      batch.setAuditOpinions(null);
      batch.setAuditTimes(null);
    }
    batch.setStatus(status);
    if (Objects.nonNull(userId)) batch.setAuditor((long) userId);
    return batch;
  }

  private ApiResult<?> busiCheck(TransferBatch batch) {
    ApiResult<?> signCheckResult = checkSign(batch);
    if (ApiResult.isNotOK(signCheckResult)) return signCheckResult;
    return checkDetailsAmountSum(batch);
  }

  private ApiResult<?> checkSign(TransferBatch batch) {
    return Objects.nonNull(batch) && appManager.checkSign(MapHelper.convertToMap(batch)) ? ApiResult.ok()
        : ApiResult.badRequest(Exceptions.SIGN_INVALID.getErrMsg());
  }

  private ApiResult<?> checkDetailsAmountSum(TransferBatch batch) {
    BigDecimal sum = BigDecimal.ZERO;
    if (CollectionUtils.isNotEmpty(batch.getDetails())) for (TransferDetail detail : batch.getDetails()) {
      detail.setAppId(batch.getAppId());
      detail.setBatchNo(batch.getBatchNo());
      sum = sum.add(detail.getAmount());
    }
    return 0 == batch.getTransferAmount().compareTo(sum) ? ApiResult.ok()
        : ApiResult.badRequest(Exceptions.AMOUNT_INVALID.getErrMsg());
  }

  public ApiResult<?> batchUpdateTransferBatchStatus(List<TransferBatchUpdateStatusDto> dtos) {
    for (TransferBatchUpdateStatusDto dto : dtos) {
      try {
        dto.setIsUpdateSuccess(
            ApiResult.isOK(update(dto.getAppId(), dto.getBatchNo(), null, dto.getToStatus(), null, null)));
      } catch (Exception e) {
        LOGGER.error(String.format("%s appId=%s batchNo=%s status to %s",
            Exceptions.DATA_PERSISTENCE_FAILED.getErrMsg(), dto.getAppId(), dto.getBatchNo(), dto.getToStatus()), e);
        dto.setIsUpdateSuccess(false);
      }
    }
    return new ApiResult<>(dtos);
  }

  public ApiResult<?> queryDirectPay(TransferBatch batch) {
    XmlPacket xmlPacket = new XmlPacket(DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    getDirectPayRequest(batch, xmlPacket);
    String request = xmlPacket.toXmlString();
    return null;
  }

  public ApiResult<?> directPay(TransferBatch batch) {
    XmlPacket xmlPacket = new XmlPacket(DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    getDirectPayRequest(batch, xmlPacket);
    String request = xmlPacket.toXmlString();
    return null;
  }

  private XmlPacket getDirectPayRequest(TransferBatch batch, XmlPacket xmlPacket) {
    getDirectPayBatchMap(batch, xmlPacket);
    getDirectPayDetailMap(batch, xmlPacket);
    return xmlPacket;
  }

  private XmlPacket getDirectPayDetailMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put("BUSCOD", batch.getBusiCode().getValue());
    map.put("BUSMOD", batch.getBusiMode().getValue());

    xmlPacket.putProperty(PAY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  private XmlPacket getDirectPayBatchMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put("YURREF", StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put("DBTACC", batch.getOutAccountId());
    map.put("DBTBBK", batch.getBranchCode().getValue());
    map.put("TRSAMT", batch.getTransferAmount().toString());
    map.put("CCYNBR", "10");//for rmb
    map.put("STLCHN", batch.getSettleChannel().getValue());
    map.put("NUSAGE", batch.getMemo());

    TransferDetail detail = batch.getDetails().get(0);
    map.put("CRTACC", detail.getInAccountId());
    map.put("CRTNAM", detail.getInAccountName());
    map.put("TRSAMT", detail.getAmount().toString());
    if (StringUtils.isNoneBlank(detail.getBankCity(), detail.getBankName())) {
      map.put("BNKFLG", "N");
      map.put("CRTBNK", detail.getBankName());
      map.put("CRTADR", detail.getBankCity());
    }

    xmlPacket.putProperty(PAY_DETAIL_MAP_NAME, map);
    return xmlPacket;
  }

  public ApiResult<?> queryAgencyPayDetail(TransferBatch batch) {
    XmlPacket xmlPacket = new XmlPacket(QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME, batch.getLoginName());
    getQueryAgencyPayDetailRequest(batch, xmlPacket);
    String request = xmlPacket.toXmlString();
    return null;
  }

  private XmlPacket getQueryAgencyPayDetailRequest(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();
    map.put("REQNBR", batch.getOutTradeNo());
    xmlPacket.putProperty(AGENCY_QUERY_DETAIL_MAP_NAME, map);
    return xmlPacket;
  }

  public ApiResult<?> queryAgencyPayResult(TransferBatch batch) {
    XmlPacket xmlPacket = new XmlPacket(QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME, batch.getLoginName());
    getQueryAgencyPayResultRequest(batch, xmlPacket);
    String request = xmlPacket.toXmlString();
    return null;
  }

  private XmlPacket getQueryAgencyPayResultRequest(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put("BUSCOD", batch.getBusiCode().getValue());
    map.put("YURREF", StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put("BGNDAT", batch.getUpDateTime().format(FORMATTER));
    map.put("ENDDAT", batch.getUpDateTime().format(FORMATTER));

    xmlPacket.putProperty(AGENCY_QUERY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  public ApiResult<?> agencyPay(TransferBatch batch) {
    XmlPacket xmlPacket = new XmlPacket(AGENCY_PAY_FUNCTION_NAME, batch.getLoginName());
    getAgencyPayRequest(batch, xmlPacket);
    String request = xmlPacket.toXmlString();
    return null;
  }

  private XmlPacket getAgencyPayRequest(TransferBatch batch, XmlPacket xmlPacket) {
    getAgencyPayBatchMap(batch, xmlPacket);
    getAgencyPayDetailMap(batch, xmlPacket);
    return xmlPacket;
  }

  private XmlPacket getAgencyPayDetailMap(TransferBatch batch, XmlPacket xmlPacket) {
    if (Objects.nonNull(batch) && CollectionUtils.isNotEmpty(batch.getDetails()))
      for (TransferDetail detail : batch.getDetails()) {
      Map<String, String> map = new HashMap<>();

      map.put("ACCNBR", detail.getInAccountId());
      map.put("CLTNAM", detail.getInAccountName());
      map.put("TRSAMT", detail.getAmount().toString());
      if (StringUtils.isNoneBlank(detail.getBankCity(), detail.getBankName())) {
        map.put("BNKFLG", "N");
        map.put("EACBNK", detail.getBankName());
        map.put("EACCTY", detail.getBankCity());
      }
      map.put("TRSDSP", detail.getTransferId());

      xmlPacket.putProperty(AGENCY_PAY_DETAIL_MAP_NAME, map);
    }
    return xmlPacket;
  }

  private XmlPacket getAgencyPayBatchMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put("BUSCOD", batch.getBusiCode().getValue());
    map.put("BUSMOD", batch.getBusiMode().getValue());
    map.put("TRSTYP", batch.getTransType().getValue());
    map.put("DBTACC", batch.getOutAccountId());
    map.put("BBKNBR", batch.getBranchCode().getValue());
    map.put("SUM", batch.getTransferAmount().toString());
    map.put("TOTAL", Integer.toString(batch.getTransferCount()));
    map.put("YURREF", StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put("MEMO", batch.getMemo());

    xmlPacket.putProperty(AGENCY_PAY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  private static final int JUNIOR_AUDIT_LIMIT = 30_0000, SENIOR_AUDIT_LIMIT = 50_0000;

  private static final List<Tuple2<Status, BigDecimal>> AUDIT_APPROVED_CONDITIONS = Arrays.asList(
      new Tuple2<>(Status.JUNIOR_APPROVED, new BigDecimal(JUNIOR_AUDIT_LIMIT)),
      new Tuple2<>(Status.SENIOR_APPROVED, new BigDecimal(SENIOR_AUDIT_LIMIT)),
      new Tuple2<>(Status.FINAL_APPROVED, new BigDecimal(Integer.MAX_VALUE)));

  private static final String AGENCY_PAY_BATCH_MAP_NAME = "SDKATSRQX", AGENCY_PAY_DETAIL_MAP_NAME = "SDKATDRQX",
      AGENCY_QUERY_BATCH_MAP_NAME = "SDKATSQYX", AGENCY_QUERY_DETAIL_MAP_NAME = "SDKATDQYX",
      PAY_BATCH_MAP_NAME = "SDKPAYRQX", PAY_DETAIL_MAP_NAME = "DCOPDPAYX";

  private static final String AGENCY_PAY_FUNCTION_NAME = "AgentRequest",
      QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME = "GetAgentInfo", DIRECT_PAY_FUNCTION_NAME = "DCPAYMNT",
      QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME = "GetAgentDetail", DATE_FORMAT = "yyyyMMdd";

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

}
