/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.job;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.sogou.pay.remit.api.SignInterceptor;
import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Channel;
import com.sogou.pay.remit.entity.TransferBatch.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.manager.CmbManager;
import com.sogou.pay.remit.manager.CmbManager.BusiResultState;
import com.sogou.pay.remit.mapper.BankInfoMapper;
import com.sogou.pay.remit.manager.TransferBatchManager;
import com.sogou.pay.remit.manager.TransferDetailManager;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.JsonHelper;
import commons.utils.Tuple2;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月19日;
//-------------------------------------------------------
@Service
public class TransferJob implements InitializingBean {

  @Resource(name = "restTemplate")
  private RestTemplate restTemplate;

  @Resource(name = "restTemplateGBK")
  private RestTemplate restTemplateGBK;

  @Autowired
  BankInfoMapper bankInfoMapper;

  public void email() {
    MultiValueMap<String, String> data = getEmail();
    if (MapUtils.isNotEmpty(data)) restTemplateGBK.postForObject(emailUrl, data, Map.class);
  }

  private MultiValueMap<String, String> getEmail() {
    LocalDate today = LocalDate.now(), yesterday = today.minusDays(1);
    List<TransferBatch> list = transferBatchManager.list(
        null, (Status.PROCESSING.getValue() | Status.SUCCESS.getValue() | Status.FAILED.getValue()
            | Status.BACK.getValue() | Status.PART.getValue()),
        null, yesterday.atStartOfDay(), today.atStartOfDay(), null, false).getData();
    if (CollectionUtils.isEmpty(list)) return null;
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("uid", "pay@sogou-inc.com");
    map.add("fr_name", "Sogou Pay");
    map.add("fr_addr", "pay@sogou-inc.com");
    map.add("mode", "text");
    map.add("title", String.format("银企直联统计%s", yesterday));
    map.add("maillist", emailList);
    map.add("attname", String.format("%s.csv", yesterday));
    map.add("attbody", getAttBody(list));
    map.add("body", getBody(list));
    return map;
  }

  private String getAttBody(List<TransferBatch> list) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        "channel,batchNo,transferCount,transferAmount,memo,outAccountId,status,successAmount,successCount,outErrMsg\n");
    TransferBatch batch = list.get(0);
    list.forEach(batch1 -> sb.append(String.join(",",
        Arrays
            .stream(new Object[] { batch.getChannel(), batch.getBatchNo(), batch.getTransferCount(),
                batch.getTransferAmount(), batch.getMemo(), batch.getOutAccountId(), batch.getStatus(),
                batch.getSuccessAmount(), batch.getSuccessCount(), batch.getOutErrMsg() })
            .map(o -> Objects.toString(o, null)).collect(Collectors.toList())))
        .append("\n"));
    return sb.toString();
  }

  private String getBody(List<TransferBatch> list) {
    BigDecimal transferAmountSum = BigDecimal.ZERO, successAmountSum = BigDecimal.ZERO;
    Integer transferCountSum = 0, successCountSum = 0;
    for (TransferBatch batch : list) {
      transferAmountSum = transferAmountSum.add(batch.getTransferAmount());
      successAmountSum = successAmountSum.add(
          Objects.equals(Status.SUCCESS, batch.getStatus()) ? batch.getTransferAmount() : batch.getSuccessAmount());
      transferCountSum += batch.getTransferCount();
      successCountSum += Objects.isNull(batch.getSuccessCount())
          ? Objects.equals(Status.SUCCESS, batch.getStatus()) ? 1 : 0 : batch.getSuccessCount();
    }
    StringBuilder sb = new StringBuilder("付款金额:").append(transferAmountSum).append("\n付款笔数:").append(transferCountSum)
        .append("\n成功金额:").append(successAmountSum).append("\n成功笔数:").append(successCountSum);
    return sb.toString();
  }

  public void detail(LocalDate date) {
    List<BankInfo> banks = bankInfoMapper.list();
    banks.forEach(bank -> LOGGER.info("date is{}, bankinfo is {}, details are {}", date, JsonHelper.toJson(bank),
        JsonHelper.toJson(cmbManager.queryAccountDetail(bank.getLoginName(), bank.getBranchCode().getValue(),
            bank.getAccountId(), date, date))));
  }

  public void pay() {
    ApiResult<List<TransferBatch>> result = transferBatchManager.listApproved();
    if (ApiResult.isNotOK(result)) return;
    List<TransferBatch> list = result.getData(), direct = new ArrayList<>(), agency = new ArrayList<>();
    for (TransferBatch batch : list) {
      if (Objects.equals(Channel.PAY, batch.getChannel())) direct.add(batch);
      else if (Objects.equals(Channel.AGENCY, batch.getChannel())) agency.add(batch);
      else LOGGER.error("unknown channel:" + batch.getChannel());
    }
    if (CollectionUtils.isNotEmpty(direct)) directPay(direct);
    if (CollectionUtils.isNotEmpty(agency)) agencyPay(agency);
  }

  public void directPay(List<TransferBatch> list) {
    LOGGER.info("direct pay start");

    for (TransferBatch batch : list)
      try {
        ApiResult<?> result;
        batch.setDetails(transferDetailManager.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
        if (ApiResult.isOK(result = cmbManager.directPay(batch))) transferBatchManager.callBack(batch.getAppId(),
            batch.getBatchNo(), Objects.isNull(result.getData()) ? Status.PROCESSING : Status.FAILED,
            (String) result.getData(), null, null, null);
        else LOGGER.error("[directPay]appId:{} batchNo:{}", batch.getAppId(), batch.getBatchNo());
      } catch (Exception e) {
        LOGGER.error("[directPay]error:batch={}", batch, e);
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
        LOGGER.error("[agencyPay]error:batch={}", batch, e);
      }

    LOGGER.info("agency pay end");
  }

  public void query() {
    ApiResult<List<TransferBatch>> result = transferBatchManager.list(null, (Status.PROCESSING.getValue()
        | Status.JUNIOR_APPROVED.getValue() | Status.SENIOR_APPROVED.getValue() | Status.FINAL_APPROVED.getValue()));
    if (ApiResult.isNotOK(result)) return;
    List<TransferBatch> list = result.getData(), direct = new ArrayList<>(), agency = new ArrayList<>();
    for (TransferBatch batch : list) {
      if (Objects.equals(Channel.PAY, batch.getChannel())) direct.add(batch);
      else if (Objects.equals(Channel.AGENCY, batch.getChannel())) agency.add(batch);
      else LOGGER.error("unknown channel:" + batch.getChannel());
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
        if (Objects.equals(dto.getState(), BusiResultState.SUCCESS)) continue;
        batch.setOutTradeNo(dto.getOutTradeNo());
        result = cmbManager.queryAgencyPayDetail(batch);
        LOGGER.info("[agencyQuery]batch:{}, details:{}", batch, result);
        if (ApiResult.isNotOK(result) || Objects.isNull(result.getData()) || !(result.getData() instanceof List))
          continue;
        List<?> details = (List<?>) result.getData();
        transferDetailManager.update(details.stream().map(o -> getDetailFromResult((AgencyDetailResultDto) o, batch))
            .collect(Collectors.toList()));
      } catch (Exception e) {
        LOGGER.error("[agencyPay]error:batch={}", batch, e);
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
        if (ApiResult.isNotOK(result) || Objects.isNull(result.getData()) || !(result.getData() instanceof Tuple2))
          return;
        Tuple2<?, ?> tuple = (Tuple2<?, ?>) result.getData();
        transferBatchManager.callBack(batch.getAppId(), batch.getBatchNo(),
            Status.valueOf(((BusiResultState) tuple.f).name()), (String) tuple.s, null, null, null);
      } catch (Exception e) {
        LOGGER.error("[directPay]error:batch={}", batch, e);
      }

    LOGGER.info("direct query end");
  }

  public void callback() {
    ApiResult<?> result = transferBatchManager.listToNotify();
    if (ApiResult.isNotOK(result)) return;

    LOGGER.info("callback start");

    for (Object o : (List<?>) result.getData()) {
      TransferBatch batch = (TransferBatch) o;
      batch.setDetails(Objects.equals(Status.SUCCESS, batch.getStatus()) ? new ArrayList<>()
          : transferDetailManager.selectByBatchNo(batch.getAppId(), batch.getBatchNo()));
      try {
        if (ApiResult.isOK(result = callback(batch))) transferBatchManager.notify(batch);
      } catch (Exception e) {
        LOGGER.error("callback error:batch=%s result={}", batch, result, e);
      }
    }

    LOGGER.info("callback end");
  }

  private ApiResult<?> callback(TransferBatch batch) throws Exception {
    String context = batch.toString();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    headers.add(SignInterceptor.PANDORA_SIGN, SignInterceptor.sign(context));
    HttpEntity<String> entity = new HttpEntity<String>(context, headers);
    @SuppressWarnings("unchecked")
    Map<String, Object> map = restTemplate.postForObject(callBackUrl, entity, Map.class);
    return Objects.equals(0, MapUtils.getInteger(map, "ret_code")) ? ApiResult.ok()
        : ApiResult.notAcceptable(String.valueOf(map));
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferJob.class);

  @Autowired
  private CmbManager cmbManager;

  @Autowired
  private TransferBatchManager transferBatchManager;

  @Autowired
  private TransferDetailManager transferDetailManager;

  @Autowired
  private Environment env;

  private String callBackUrl, emailUrl, emailList;

  @Override
  public void afterPropertiesSet() throws Exception {
    callBackUrl = env.getRequiredProperty("pandora.callback.url");
    emailUrl = env.getRequiredProperty("email.url");
    emailList = env.getRequiredProperty("email.list");
  }

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
