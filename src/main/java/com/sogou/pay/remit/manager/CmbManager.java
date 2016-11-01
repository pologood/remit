/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Currency;
import com.sogou.pay.remit.job.TransferJob.AgencyBatchResultDto;
import com.sogou.pay.remit.job.TransferJob.AgencyDetailResultDto;
import com.sogou.pay.remit.job.TransferJob.AgencyDetailResultDto.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.model.ApiResult;

import commons.utils.Tuple2;
import commons.utils.cmb.XmlPacket;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月20日;
//-------------------------------------------------------
@Service
public class CmbManager implements InitializingBean {

  @Autowired
  private Environment env;

  private String URL;

  @Resource(name = "restTemplateGBK")
  private RestTemplate restTemplate;

  /**
   * @return Tuple2 BusiResultState, String first for result, second for message
   */
  public ApiResult<?> queryDirectPay(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    String result = restTemplate.postForObject(URL, getQueryDirectPayRequest(batch, packet).toXmlString(),
        String.class);
    return getQueryDirectPayResult(result);
  }

  private ApiResult<?> getQueryDirectPayResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    if (Objects.isNull(packet) || !Objects.equals(packet.getRETCOD(), ResponseCode.SUCCESS.getValue())
        || CollectionUtils.isEmpty(packet.getProperty(QUERY_PAY_RESULT_MAP)))
      return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    Map<String, String> map = packet.getProperty(QUERY_PAY_RESULT_MAP).stream()
        .max((map1, map2) -> map1.get(OUT_TRADE_NO).compareTo(map2.get(OUT_TRADE_NO))).get();
    if (Objects.equals(BusiRequestState.FINISHED.getValue(), map.get(REQUEST_STATE))) {
      if (Objects.equals(BusiResultState.SUCCESS.getValue(), map.get(RESULT_STATE)))
        return new ApiResult<>(new Tuple2<>(BusiResultState.SUCCESS, null));
      if (Objects.equals(BusiResultState.BACK.getValue(), map.get(RESULT_STATE)))
        return new ApiResult<>(new Tuple2<>(BusiResultState.BACK, map.get(PAY_QUERY_ERROR_MESSAGE)));
      else return new ApiResult<>(new Tuple2<>(BusiResultState.FAILED, map.get(PAY_QUERY_ERROR_MESSAGE)));
    } else return new ApiResult<>(BusiRequestState.BANK_PROCESSING);
  }

  private XmlPacket getQueryDirectPayRequest(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BATCHNO, StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(BEGIN_DATE, getBeginDate(batch));
    map.put(END_DATE, getEndDate(batch));

    xmlPacket.putProperty(PAY_QUERY_MAP_NAME, map);
    return xmlPacket;
  }

  private String getEndDate(TransferBatch batch) {
    return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
  }

  private String getBeginDate(TransferBatch batch) {
    return LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
  }

  public ApiResult<?> directPay(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    String result = restTemplate.postForObject(URL, getDirectPayRequest(batch, packet).toXmlString(), String.class);
    return getDirectPayResult(result);
  }

  private ApiResult<?> getDirectPayResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    if (Objects.isNull(packet) || !Objects.equals(ResponseCode.SUCCESS.getValue(), packet.getRETCOD())
        || CollectionUtils.isEmpty(packet.getProperty(PAY_RESULT_MAP)))
      return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    Map<String, String> map = packet.getProperty(PAY_RESULT_MAP).stream()
        .max((map1, map2) -> map1.get(OUT_TRADE_NO).compareTo(map2.get(OUT_TRADE_NO))).get();
    if (Objects.equals(BusiRequestState.FINISHED.getValue(), map.get(REQUEST_STATE))
        && Objects.equals(BusiResultState.FAILED.getValue(), map.get(RESULT_STATE)))
      return new ApiResult<>(map.get(PAY_ERROR_MESSAGE));
    else return ApiResult.ok();
  }

  private XmlPacket getDirectPayRequest(TransferBatch batch, XmlPacket xmlPacket) {
    getDirectPayBatchMap(batch, xmlPacket);
    getDirectPayDetailMap(batch, xmlPacket);
    return xmlPacket;
  }

  private XmlPacket getDirectPayBatchMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BUSIMODE, batch.getBusiMode().getValue());

    xmlPacket.putProperty(PAY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  private XmlPacket getDirectPayDetailMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BATCHNO, StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(OUT_ACCOUNT_ID, batch.getOutAccountId());
    map.put("DBTBBK", batch.getBranchCode().getValue());
    map.put(DETAIL_AMOUNT, batch.getTransferAmount().toString());
    map.put(CURRENCY, Currency.RMB.getValue());
    map.put("STLCHN", batch.getSettleChannel().getValue());
    map.put("NUSAGE", batch.getMemo());

    TransferDetail detail = batch.getDetails().get(0);
    map.put("CRTACC", detail.getInAccountId());
    map.put("CRTNAM", detail.getInAccountName());
    if (StringUtils.isNoneBlank(detail.getBankCity(), detail.getBankName())) {
      map.put(BANK_FLAG, "N");
      map.put("CRTBNK", detail.getBankName());
      map.put("CRTADR", detail.getBankCity());
    } else map.put(BANK_FLAG, "Y");

    xmlPacket.putProperty(PAY_DETAIL_MAP_NAME, map);
    return xmlPacket;
  }

  /**
   * @return List AgencyDetailResultDto
   */
  public ApiResult<?> queryAgencyPayDetail(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME, batch.getLoginName());
    String result = restTemplate.postForObject(URL, getQueryAgencyPayDetailRequest(batch, packet).toXmlString(),
        String.class);
    return getQueryAgencyDetailResult(result);
  }

  private ApiResult<?> getQueryAgencyDetailResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    if (Objects.isNull(packet) || !Objects.equals(ResponseCode.SUCCESS.getValue(), packet.getRETCOD())
        || CollectionUtils.isEmpty(packet.getProperty(QUERY_AGENCY_DETAIL_RESULT_MAP)))
      return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    List<AgencyDetailResultDto> detailResults = new ArrayList<>();
    for (Map<String, String> map : packet.getProperty(QUERY_AGENCY_DETAIL_RESULT_MAP)) {
      AgencyDetailResultDto dto = new AgencyDetailResultDto();

      dto.setAmount(new BigDecimal(map.get(DETAIL_AMOUNT)));
      dto.setAccountId(map.get(IN_ACCOUNT_ID));
      dto.setErrMsg(map.get(AGENCY_ERROR_MESSAGE));
      dto.setTransferId(map.get(TRANSFER_ID));
      dto.setStatus(Status.get(map.get(DETAIL_STATE)));

      detailResults.add(dto);
    }
    return new ApiResult<>(detailResults);
  }

  private XmlPacket getQueryAgencyPayDetailRequest(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();
    map.put(OUT_TRADE_NO, batch.getOutTradeNo());
    xmlPacket.putProperty(AGENCY_QUERY_DETAIL_MAP_NAME, map);
    return xmlPacket;
  }

  public ApiResult<?> queryAgencyPayResult(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME, batch.getLoginName());
    String result = restTemplate.postForObject(URL, getQueryAgencyPayResultRequest(batch, packet).toXmlString(),
        String.class);
    return getQueryAgencyResult(result, batch);
  }

  private ApiResult<?> getQueryAgencyResult(String data, TransferBatch batch) {
    XmlPacket packet = XmlPacket.valueOf(data);
    if (Objects.isNull(packet) || !Objects.equals(ResponseCode.SUCCESS.getValue(), packet.getRETCOD())
        || Objects.isNull(packet.getProperty(QUERY_AGENCY_BATCH_RESULT_MAP, 0)))
      return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    Map<String, String> map = packet.getProperty(QUERY_AGENCY_BATCH_RESULT_MAP, 0);
    AgencyBatchResultDto dto = new AgencyBatchResultDto();
    if (Objects.equals(BusiRequestState.FINISHED.getValue(), map.get(BATCH_REQUEST_STATE))) {
      if (Objects.equals(BusiResultState.FAILED.getValue(), map.get(RESULT_STATE)))
        dto.setState(BusiResultState.FAILED);
      else if (Objects.equals(BusiResultState.SUCCESS.getValue(), map.get(RESULT_STATE))) {
        dto.setState(Objects.equals(String.valueOf(batch.getTransferAmount()), map.get(SUCCESS_AMOUNT))
            && Objects.equals(batch.getTransferCount(), MapUtils.getInteger(map, SUCCESS_COUNT))
                ? BusiResultState.SUCCESS : BusiResultState.PART);
      } else return ApiResult.notAcceptable("agency batch query unknown status:" + map.get(RESULT_STATE));
      dto.setErrMsg(map.get(AGENCY_ERROR_MESSAGE));
      dto.setOutTradeNo(map.get(OUT_TRADE_NO));
      String successAmount = map.get(SUCCESS_AMOUNT), successCount = map.get(SUCCESS_COUNT);
      if (StringUtils.isNotBlank(successCount)) dto.setSuccessCount(Integer.parseInt(successCount));
      if (StringUtils.isNotBlank(successAmount)) dto.setSuccessAmount(new BigDecimal(successAmount));
      return new ApiResult<>(dto);
    } else return new ApiResult<>(BusiRequestState.BANK_PROCESSING);
  }

  private XmlPacket getQueryAgencyPayResultRequest(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BATCHNO, StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(BEGIN_DATE, getBeginDate(batch));
    map.put(END_DATE, getEndDate(batch));

    xmlPacket.putProperty(AGENCY_QUERY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  public ApiResult<?> agencyPay(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(AGENCY_PAY_FUNCTION_NAME, batch.getLoginName());
    String result = restTemplate.postForObject(URL, getAgencyPayRequest(batch, packet).toXmlString(), String.class);
    return getAgencyPayResult(result);
  }

  private ApiResult<?> getAgencyPayResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    if (Objects.isNull(packet) || !Objects.equals(ResponseCode.SUCCESS.getValue(), packet.getRETCOD()))
      return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    return ApiResult.ok();
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

      map.put(IN_ACCOUNT_ID, detail.getInAccountId());
      map.put(IN_ACCOUNT_NAME, detail.getInAccountName());
      map.put(DETAIL_AMOUNT, detail.getAmount().toString());
      if (StringUtils.isNoneBlank(detail.getBankCity(), detail.getBankName())) {
        map.put(BANK_FLAG, "N");
        map.put(BANK_NAME, detail.getBankName());
        map.put(BANK_CITY, detail.getBankCity());
      }
      map.put(TRANSFER_ID, detail.getTransferId());

      xmlPacket.putProperty(AGENCY_PAY_DETAIL_MAP_NAME, map);
    }
    return xmlPacket;
  }

  private XmlPacket getAgencyPayBatchMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BUSIMODE, batch.getBusiMode().getValue());
    map.put(TRANSTYPE, batch.getTransType().getValue());
    map.put(OUT_ACCOUNT_ID, batch.getOutAccountId());
    map.put(BRANCH_CODE, batch.getBranchCode().getValue());
    map.put(TRANSFER_AMOUNT, batch.getTransferAmount().toString());
    map.put(TRANSFER_COUNT, Integer.toString(batch.getTransferCount()));
    map.put(BATCHNO, StringUtils.join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(MEMO, batch.getMemo());

    xmlPacket.putProperty(AGENCY_PAY_BATCH_MAP_NAME, map);
    return xmlPacket;
  }

  private static final String AGENCY_PAY_BATCH_MAP_NAME = "SDKATSRQX", AGENCY_PAY_DETAIL_MAP_NAME = "SDKATDRQX",
      AGENCY_QUERY_BATCH_MAP_NAME = "SDKATSQYX", AGENCY_QUERY_DETAIL_MAP_NAME = "SDKATDQYX",
      PAY_BATCH_MAP_NAME = "SDKPAYRQX", PAY_QUERY_MAP_NAME = "SDKPAYQYX", PAY_DETAIL_MAP_NAME = "DCOPDPAYX",
      PAY_RESULT_MAP = "NTQPAYRQZ", QUERY_PAY_RESULT_MAP = "NTQPAYQYZ", QUERY_AGENCY_BATCH_RESULT_MAP = "NTQATSQYZ",
      QUERY_AGENCY_DETAIL_RESULT_MAP = "NTQATDQYZ";

  private static final String AGENCY_PAY_FUNCTION_NAME = "AgentRequest",
      QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME = "GetAgentInfo", DIRECT_PAY_FUNCTION_NAME = "DCPAYMNT",
      QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME = "GetAgentDetail", QUERY_DIRECT_PAY_FUNCTION_NAME = "GetPaymentInfo",
      BATCH_REQUEST_STATE = "REQSTA", REQUEST_STATE = "REQSTS", RESULT_STATE = "RTNFLG",
      PAY_QUERY_ERROR_MESSAGE = "RTNNAR", AGENCY_ERROR_MESSAGE = "ERRDSP", TRANSFER_ID = "TRSDSP",
      DETAIL_STATE = "STSCOD", PAY_ERROR_MESSAGE = "ERRTXT";

  private static final String BUSICODE = "BUSCOD", BUSIMODE = "BUSMOD", TRANSTYPE = "TRSTYP", OUT_ACCOUNT_ID = "DBTACC",
      BRANCH_CODE = "BBKNBR", TRANSFER_COUNT = "TOTAL", TRANSFER_AMOUNT = "SUM", BATCHNO = "YURREF", MEMO = "MEMO",
      IN_ACCOUNT_ID = "ACCNBR", IN_ACCOUNT_NAME = "CLTNAM", DETAIL_AMOUNT = "TRSAMT", BANK_FLAG = "BNKFLG",
      BANK_NAME = "EACBNK", BANK_CITY = "EACCTY", BEGIN_DATE = "BGNDAT", END_DATE = "ENDDAT", SUCCESS_AMOUNT = "SUCAMT",
      SUCCESS_COUNT = "SUCNUM", OUT_TRADE_NO = "REQNBR", CURRENCY = "CCYNBR";

  //private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public void afterPropertiesSet() throws Exception {
    URL = env.getRequiredProperty("cmb.url");
  }

  public enum ResponseCode {
    SUCCESS("0"), //成功
    COMMIT_MASTER_FAIL("-1"), //提交主机失败
    EXECUTE_FAIL("-2"), //执行失败
    DATA_FORMAT_INVALID("-3"), //数据格式错误
    NEED_LOG_IN("-4"), //尚未登录系统
    TOO_FREQUENT("-5"), //请求太频繁
    NOT_CERT_USER("-6"), //不是证书用户
    USER_CANCEL("-7"), //用户取消操作
    OTHER("-9"); //其他错误

    private String value;

    private ResponseCode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public enum BusiRequestState {
    WAITING_FOR_AUDIT("AUT"), //等待审批
    AUDIT_COMPLETED("NTE"), //终审完毕
    ORDER_WAITING_FOR_CONFIRMATION("WCF"), //订单待确认(商务支付用)
    BANK_PROCESSING("BNK"), //银行处理中
    FINISHED("FIN"), //完成
    WAITNG_FOR_ACKNOWLEDGEMENT("ACK"), //等待确认(委托贷款用)
    WAITING_FOR_BANK_ACKNOWLEDGEMENT("APD"), //待银行确认(国际业务用)
    DATA_RECEIVING("OPR"); //数据接收中(代发)

    private String value;

    private BusiRequestState(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public enum BusiResultState {
    SUCCESS("S"), //成功 银行支付成功
    FAILED("F"), //失败 银行支付失败
    BACK("B"), //退票 银行支付被退票
    REJECTED("R"), //否决 企业审批否决
    EXPIRED("D"), //过期 企业过期不审批
    CANCELED("C"), //撤消 企业撤销
    MERCHANT_CANCELED("M"), //商户撤销订单 商务支付
    REFUSED("V"), PART("S"); //拒绝 委托贷款被借款方拒绝

    private String value;

    private BusiResultState(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}
