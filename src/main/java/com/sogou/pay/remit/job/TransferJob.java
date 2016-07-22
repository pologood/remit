/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.manager.CmbManager;
import com.sogou.pay.remit.manager.CmbManager.BusiResultState;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.manager.TransferDetailManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月19日;
//-------------------------------------------------------
@Component
public class TransferJob {

  public void pay() {
    ApiResult<List<TransferBatch>> result = transferBatchManager.listApproved();
    if (ApiResult.isNotOK(result)) return;
    List<TransferBatch> list = result.getData(), direct = new ArrayList<>(), agency = new ArrayList<>();
    for (TransferBatch batch : list) {
      if (Objects.equals(Channel.PAY, batch.getChannel())) direct.add(batch);
      else if (Objects.equals(Channel.AGENCY, batch.getChannel())) agency.add(batch);
      else throw new RuntimeException("unknown channel:" + batch.getChannel());
    }
    if (CollectionUtils.isNotEmpty(direct)) directPay(direct);
    if (CollectionUtils.isNotEmpty(agency)) agencyPay(agency);
  }

  public void directPay(List<TransferBatch> list) {
    LOGGER.info("direct pay start");

    for (TransferBatch batch : list)
      try {
        batch.setDetails(transferDetailManager.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
        if (ApiResult.isOK(cmbManager.directPay(batch))) transferBatchManager.callBack(batch.getAppId(),
            batch.getBatchNo(), Status.PROCESSING, null, null, null, null);
        else LOGGER.error("[directPay]appId:{} batchNo:{}", batch.getAppId(), batch.getBatchNo());
      } catch (Exception e) {
        LOGGER.error(String.format("[directPay]error:batch=%s", batch), e);
      }

    LOGGER.info("direct pay end");
  }

  public void agencyPay(List<TransferBatch> list) {
    LOGGER.info("agency pay start");

    for (TransferBatch batch : list)
      try {
        batch.setDetails(transferDetailManager.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
        if (ApiResult.isOK(cmbManager.agencyPay(batch))) transferBatchManager.callBack(batch.getAppId(),
            batch.getBatchNo(), Status.PROCESSING, null, null, null, null);
      } catch (Exception e) {
        LOGGER.error(String.format("[agencyPay]error:batch=%s", batch), e);
      }

    LOGGER.info("agency pay end");
  }

  public void query() {
    ApiResult<List<TransferBatch>> result = transferBatchManager.list(Status.PROCESSING);
    if (ApiResult.isNotOK(result)) return;
    List<TransferBatch> list = result.getData(), direct = new ArrayList<>(), agency = new ArrayList<>();
    for (TransferBatch batch : list) {
      if (Objects.equals(Channel.PAY, batch.getChannel())) direct.add(batch);
      else if (Objects.equals(Channel.AGENCY, batch.getChannel())) agency.add(batch);
      else throw new RuntimeException("unknown channel:" + batch.getChannel());
    }
    if (CollectionUtils.isNotEmpty(direct)) directQuery(direct);
    if (CollectionUtils.isNotEmpty(agency)) agencyQuery(agency);
  }

  private void agencyQuery(List<TransferBatch> list) {
    LOGGER.info("agency query start");

    for (TransferBatch batch : list)
      try {
        ApiResult<?> result = cmbManager.queryAgencyPayResult(batch);
        LOGGER.info("[agencyQuery]batch:{}, result:{}", batch, result);
        if (ApiResult.isNotOK(result) || Objects.isNull(result.getData())
            || !(result.getData() instanceof AgencyBatchResultDto))
          continue;
        AgencyBatchResultDto dto = (AgencyBatchResultDto) result.getData();
        transferBatchManager.callBack(batch.getAppId(), batch.getBatchNo(), Status.valueOf(dto.getState().name()),
            dto.getErrMsg(), dto.getOutTradeNo(), dto.getSuccessCount(), dto.getSuccessAmount());
        if (!Objects.equals(dto.getState(), BusiResultState.PART)) continue;
        batch.setOutTradeNo(dto.getOutTradeNo());;
        result = cmbManager.queryAgencyPayDetail(batch);
        LOGGER.info("[agencyQuery]batch:{}, details:{}", batch, result);
        if (ApiResult.isNotOK(result) || Objects.isNull(result.getData()) || !(result.getData() instanceof List))
          continue;
        List<?> details = (List<?>) result.getData();
        transferDetailManager.update(details.stream().map(o -> getDetailFromResult((AgencyDetailResultDto) o, batch))
            .collect(Collectors.toList()));
      } catch (Exception e) {
        LOGGER.error(String.format("[agencyPay]error:batch=%s", batch), e);
      }

    LOGGER.info("agency query end");

  }

  private TransferDetail getDetailFromResult(AgencyDetailResultDto dto, TransferBatch batch) {
    TransferDetail detail = new TransferDetail();

    detail.setAppId(batch.getAppId());
    detail.setBatchNo(batch.getBatchNo());
    detail.setTransferId(dto.getTransferId());
    detail.setStatus(com.sogou.pay.remit.entity.TransferDetail.Status.valueOf(dto.getStatus().name()));
    detail.setOutErrMsg(dto.getErrMsg());
    detail.setAmount(dto.getAmount());
    detail.setInAccountId(dto.getAccountId());

    return detail;
  }

  private void directQuery(List<TransferBatch> list) {
    LOGGER.info("direct query start");

    for (TransferBatch batch : list)
      try {
        ApiResult<?> result = cmbManager.queryDirectPay(batch);
        LOGGER.info("[directQuery]batch:{}, result:{}", batch, result);
        if (ApiResult.isOK(result) && Objects.nonNull(result.getData()) && result.getData() instanceof Tuple2) {
          Tuple2<?, ?> tuple = (Tuple2<?, ?>) result.getData();
          transferBatchManager.callBack(batch.getAppId(), batch.getBatchNo(),
              Status.valueOf(((BusiResultState) tuple.f).name()), (String) tuple.s, null, null, null);
        }
      } catch (Exception e) {
        LOGGER.error(String.format("[directPay]error:batch=%s", batch), e);
      }

    LOGGER.info("direct query end");
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferJob.class);

  @Autowired
  private CmbManager cmbManager;

  @Autowired
  private TransferBatchManager transferBatchManager;

  @Autowired
  private TransferDetailManager transferDetailManager;

  public static class AgencyDetailResultDto {

    private String accountId;

    private BigDecimal amount;

    private Status status;

    private String errMsg;

    private String transferId;

    public String getAccountId() {
      return accountId;
    }

    public void setAccountId(String accountId) {
      this.accountId = accountId;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }

    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }

    public String getErrMsg() {
      return errMsg;
    }

    public void setErrMsg(String errMsg) {
      this.errMsg = errMsg;
    }

    public String getTransferId() {
      return transferId;
    }

    public void setTransferId(String transferId) {
      this.transferId = transferId;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public enum Status {
      SUCCESS("S"), //成功 
      FAILED("F"), //失败
      CANCELED("C"), //撤消 
      INIT("I");//数据录入

      private static final Map<String, Status> MAP = new HashMap<>();

      static {
        Arrays.asList(Status.values()).forEach(state -> MAP.put(state.getValue(), state));
      }

      public static Status get(String value) {
        return MAP.get(value);
      }

      private String value;

      private Status(String value) {
        this.value = value;
      }

      public String getValue() {
        return value;
      }
    }
  }

  public static class AgencyBatchResultDto {

    private String outTradeNo;

    private BusiResultState state;

    private String errMsg;

    private Integer successCount;

    private BigDecimal successAmount;

    public String getOutTradeNo() {
      return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
      this.outTradeNo = outTradeNo;
    }

    public BusiResultState getState() {
      return state;
    }

    public void setState(BusiResultState state) {
      this.state = state;
    }

    public String getErrMsg() {
      return errMsg;
    }

    public void setErrMsg(String errMsg) {
      this.errMsg = errMsg;
    }

    public Integer getSuccessCount() {
      return successCount;
    }

    public void setSuccessCount(Integer successCount) {
      this.successCount = successCount;
    }

    public BigDecimal getSuccessAmount() {
      return successAmount;
    }

    public void setSuccessAmount(BigDecimal successAmount) {
      this.successAmount = successAmount;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
  }
}
