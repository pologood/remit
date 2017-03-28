/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.manager;

import static org.apache.commons.lang3.StringUtils.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.function.BiConsumer;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableMap;
import com.sogou.pay.remit.entity.AccountDetail;
import com.sogou.pay.remit.entity.TransferBatch;
import com.sogou.pay.remit.entity.TransferBatch.Currency;
import com.sogou.pay.remit.job.TransferJob.AgencyBatchResultDto;
import com.sogou.pay.remit.job.TransferJob.AgencyDetailResultDto;
import com.sogou.pay.remit.job.TransferJob.AgencyDetailResultDto.Status;
import com.sogou.pay.remit.entity.TransferDetail;
import com.sogou.pay.remit.entity.AccountDetail.CreditOrDebit;
import com.sogou.pay.remit.entity.AccountDetail.TradeType;
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

  private Map<String, String> URL_MAP;

  @Resource(name = "restTemplateGBK")
  private RestTemplate restTemplate;

  public ApiResult<?> queryAccountDetail(String loginName, String branchCode, String accountId, LocalDate beginDate,
      LocalDate endDate) {
    return getQueryAccountDetailResult(restTemplate.postForObject(getUrl(loginName),
        getQueryAccountDetailRequest(loginName, branchCode, accountId, beginDate, endDate), String.class));
  }

  private ApiResult<?> getQueryAccountDetailResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    Vector<Map<String, String>> result = get(packet, QUERY_ACCOUNT_DETAIL_RESULT_MAP);
    if (Objects.isNull(result)) return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    List<AccountDetail> detailResults = new ArrayList<>();
    for (Map<String, String> map : result) {
      AccountDetail detail = new AccountDetail();

      detail.setTradeDate(getDate(map.get(TRADE_DATE)));
      detail.setTradeTime(getTime(map.get(TRADE_TIME)));
      detail.setFlag(CreditOrDebit.get(map.get(CREDIT_OR_DEBIT)));
      detail.setSerialNumber(map.get(SERIAL_NUMBER));

      fieldMap.entrySet().stream().filter(e -> isNotBlank(map.get(e.getKey())))
          .forEach(e -> e.getValue().accept(detail, map.get(e.getKey())));

      detailResults.add(detail);
    }
    return new ApiResult<>(detailResults);
  }

  Map<String, BiConsumer<AccountDetail, String>> fieldMap = new HashMap<String, BiConsumer<AccountDetail, String>>() {
    {
      put(INTEREST_DATE, (detail, data) -> detail.setInterestDate(getDate(data)));
      put(TRADE_TYPE, (detail, data) -> detail.setTradeType(TradeType.get(data)));
      put(ABSTRACT, (detail, data) -> detail.setAbstracts(data));
      put(DEBIT_AMOUNT, (detail, data) -> detail.setDebitAmount(new BigDecimal(data)));
      put(CREDIT_AMOUNT, (detail, data) -> detail.setCreditAmount(new BigDecimal(data)));
      put(BALANCE, (detail, data) -> detail.setBalance(new BigDecimal(data)));
      put(OUT_TRADE_NO, (detail, data) -> detail.setInstanceNo(Integer.parseInt(data)));
      put(BUSINESS_NAME, (detail, data) -> detail.setBusinessName(data));
      put(USAGE, (detail, data) -> detail.setUsage(data));
      put(BATCHNO, (detail, data) -> detail.setBusinessNo(data));
      put(BUSINESS_ABSTRACT, (detail, data) -> detail.setBusinessAbstract(data));
      put(OTHER_ABSTRACT, (detail, data) -> detail.setOtherAbstract(data));
      put(ACCOUNT_REGION, (detail, data) -> detail.setAccountRegion(data));
      put(ACCOUNT_NAME, (detail, data) -> detail.setAccountName(data));
      put(ACCOUNT_ID, (detail, data) -> detail.setAccountId(data));
      put(BANK_CODE, (detail, data) -> detail.setBankCode(data));
      put(BANK_NAME_DETAIL, (detail, data) -> detail.setBankName(data));
      put(BANK_LOCATION, (detail, data) -> detail.setBankLocation(data));
      put(COMPANY_REGION, (detail, data) -> detail.setCompanyRegion(data));
      put(COMPANY_ACCOUNT, (detail, data) -> detail.setCompanyAccount(data));
      put(COMPANY_NAME, (detail, data) -> detail.setCompanyName(data));
      put(INFORMATION_FLAG, (detail, data) -> detail.setInformationFlag(data));
      put(HAS_ATTACHMENT_FLAG, (detail, data) -> detail.setHasAttachmentFlag(data));
      put(INVOICE, (detail, data) -> detail.setInvoice(data));
      put(FIX_FLAG, (detail, data) -> detail.setFixFlag(data));
      put(EXT_ABSTRACT, (detail, data) -> detail.setExtAbstract(data));
      put(TRADE_CODE, (detail, data) -> detail.setTradeCode(data));
      put(MERCHANT_ORDER_ID, (detail, data) -> detail.setMerchantOrderId(data));
      put(COMPANY_CODE, (detail, data) -> detail.setCompanyCode(data));
    }
  };

  private LocalDate getDate(String date) {
    return LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
  }

  private LocalTime getTime(String date) {
    return LocalTime.parse(date, DateTimeFormatter.ofPattern("HHmmss"));
  }

  private Object getQueryAccountDetailRequest(String loginName, String branchCode, String accountId,
      LocalDate beginDate, LocalDate endDate) {
    XmlPacket packet = new XmlPacket(QUERY_ACCOUNT_DETAIL_FUNCTION_NAME, loginName);

    Map<String, String> map = new HashMap<>();

    map.put(BRANCH_CODE, branchCode);
    map.put(IN_ACCOUNT_ID, accountId);
    map.put(BEGIN_DATE, format(beginDate));
    map.put(END_DATE, format(endDate));

    packet.putProperty(ACCOUNT_DETAIL_QUERY_MAP_NAME, map);
    return packet.toXmlString();
  }

  public ApiResult<?> queryDirectPay(TransferBatch batch) {
    return getQueryDirectPayResult(
        restTemplate.postForObject(getUrl(batch.getLoginName()), getQueryDirectPayRequest(batch), String.class));
  }

  private String getQueryDirectPayRequest(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BATCHNO, join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(BEGIN_DATE, getBeginDate(batch));
    map.put(END_DATE, getEndDate(batch));

    packet.putProperty(PAY_QUERY_MAP_NAME, map);
    return packet.toXmlString();
  }

  private String getBeginDate(TransferBatch batch) {
    return format(batch.getUpDateTime().toLocalDate().minusDays(1));
  }

  private String format(LocalDate date) {
    return date.format(DateTimeFormatter.BASIC_ISO_DATE);
  }

  private String getEndDate(TransferBatch batch) {
    return format(batch.getUpDateTime().toLocalDate().plusDays(1));
  }

  private ApiResult<?> getQueryDirectPayResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    Vector<Map<String, String>> result = get(packet, QUERY_PAY_RESULT_MAP);
    if (Objects.isNull(result)) return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    Map<String, String> map = result.stream()
        .max((map1, map2) -> map1.get(OUT_TRADE_NO).compareTo(map2.get(OUT_TRADE_NO))).get();
    if (Objects.equals(BusiRequestState.FINISHED.getValue(), map.get(REQUEST_STATE))) {
      String resultState = map.get(RESULT_STATE);
      if (Objects.equals(BusiResultState.SUCCESS.getValue(), resultState))
        return new ApiResult<>(new Tuple2<>(BusiResultState.SUCCESS, null));
      return new ApiResult<>(new Tuple2<>(
          Objects.equals(BusiResultState.BACK.getValue(), resultState) ? BusiResultState.BACK : BusiResultState.FAILED,
          map.get(PAY_QUERY_ERROR_MESSAGE)));
    } else return new ApiResult<>(BusiRequestState.BANK_PROCESSING);
  }

  private Vector<Map<String, String>> get(XmlPacket packet, String key) {
    return isFailed(packet) || CollectionUtils.isEmpty(packet.getProperty(key)) ? null : packet.getProperty(key);
  }

  private boolean isFailed(XmlPacket packet) {
    return Objects.isNull(packet) || !Objects.equals(packet.getRETCOD(), ResponseCode.SUCCESS.getValue());
  }

  public ApiResult<?> directPay(TransferBatch batch) {
    return getDirectPayResult(
        restTemplate.postForObject(getUrl(batch.getLoginName()), getDirectPayRequest(batch), String.class));
  }

  private String getDirectPayRequest(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(DIRECT_PAY_FUNCTION_NAME, batch.getLoginName());
    getDirectPayBatchMap(batch, packet);
    getDirectPayDetailMap(batch, packet);
    return packet.toXmlString();
  }

  private void getDirectPayBatchMap(TransferBatch batch, XmlPacket packet) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BUSIMODE, batch.getBusiMode().getValue());

    packet.putProperty(PAY_BATCH_MAP_NAME, map);
  }

  private void getDirectPayDetailMap(TransferBatch batch, XmlPacket packet) {
    Map<String, String> map = new HashMap<>();

    map.put(BATCHNO, join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(OUT_ACCOUNT_ID, batch.getOutAccountId());
    map.put("DBTBBK", batch.getBranchCode().getValue());
    map.put(DETAIL_AMOUNT, batch.getTransferAmount().toString());
    map.put(CURRENCY, Currency.RMB.getValue());
    map.put("STLCHN", batch.getSettleChannel().getValue());
    map.put(USAGE, batch.getMemo());

    TransferDetail detail = batch.getDetails().get(0);
    map.put("CRTACC", detail.getInAccountId());
    map.put("CRTNAM", detail.getInAccountName());
    if (isNoneBlank(detail.getBankCity(), detail.getBankName())) {
      map.put(BANK_FLAG, "N");
      map.put("CRTBNK", detail.getBankName());
      map.put("CRTADR", detail.getBankCity());
    } else map.put(BANK_FLAG, "Y");

    packet.putProperty(PAY_DETAIL_MAP_NAME, map);
  }

  private ApiResult<?> getDirectPayResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    Vector<Map<String, String>> result = get(packet, PAY_RESULT_MAP);
    if (Objects.isNull(result)) return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    Map<String, String> map = result.stream()
        .max((map1, map2) -> map1.get(OUT_TRADE_NO).compareTo(map2.get(OUT_TRADE_NO))).get();
    if (Objects.equals(BusiRequestState.FINISHED.getValue(), map.get(REQUEST_STATE))
        && Objects.equals(BusiResultState.FAILED.getValue(), map.get(RESULT_STATE)))
      return new ApiResult<>(map.get(PAY_ERROR_MESSAGE));
    else return ApiResult.ok();
  }

  public ApiResult<?> queryAgencyPayDetail(TransferBatch batch) {
    return getQueryAgencyDetailResult(
        restTemplate.postForObject(getUrl(batch.getLoginName()), getQueryAgencyPayDetailRequest(batch), String.class));
  }

  private String getQueryAgencyPayDetailRequest(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME, batch.getLoginName());
    Map<String, String> map = new HashMap<>();
    map.put(OUT_TRADE_NO, batch.getOutTradeNo());
    packet.putProperty(AGENCY_QUERY_DETAIL_MAP_NAME, map);
    return packet.toXmlString();
  }

  private ApiResult<?> getQueryAgencyDetailResult(String data) {
    XmlPacket packet = XmlPacket.valueOf(data);
    Vector<Map<String, String>> result = get(packet, QUERY_AGENCY_DETAIL_RESULT_MAP);
    if (Objects.isNull(result)) return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
    List<AgencyDetailResultDto> detailResults = new ArrayList<>();
    for (Map<String, String> map : result) {
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

  public ApiResult<?> queryAgencyPayResult(TransferBatch batch) {
    return getQueryAgencyResult(
        restTemplate.postForObject(getUrl(batch.getLoginName()), getQueryAgencyPayResultRequest(batch), String.class),
        batch);
  }

  private String getQueryAgencyPayResultRequest(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME, batch.getLoginName());
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BATCHNO, join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(BEGIN_DATE, getBeginDate(batch));
    map.put(END_DATE, getEndDate(batch));

    packet.putProperty(AGENCY_QUERY_BATCH_MAP_NAME, map);
    return packet.toXmlString();
  }

  private ApiResult<?> getQueryAgencyResult(String data, TransferBatch batch) {
    XmlPacket packet = XmlPacket.valueOf(data);
    Map<String, String> map = get(QUERY_AGENCY_BATCH_RESULT_MAP, packet);
    if (Objects.isNull(map)) return ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name());
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
      if (isNotBlank(successCount)) dto.setSuccessCount(Integer.parseInt(successCount));
      if (isNotBlank(successAmount)) dto.setSuccessAmount(new BigDecimal(successAmount));
      return new ApiResult<>(dto);
    } else return new ApiResult<>(BusiRequestState.BANK_PROCESSING);
  }

  private Map<String, String> get(String key, XmlPacket packet) {
    Vector<Map<String, String>> maps = get(packet, key);
    return Objects.isNull(maps) || MapUtils.isEmpty(maps.get(0)) ? null : maps.get(0);
  }

  public ApiResult<?> agencyPay(TransferBatch batch) {
    return getAgencyPayResult(
        restTemplate.postForObject(getUrl(batch.getLoginName()), getAgencyPayRequest(batch), String.class));
  }

  private ApiResult<?> getAgencyPayResult(String data) {
    return isFailed(XmlPacket.valueOf(data)) ? ApiResult.notAcceptable(ResponseCode.DATA_FORMAT_INVALID.name())
        : ApiResult.ok();
  }

  private String getAgencyPayRequest(TransferBatch batch) {
    XmlPacket packet = new XmlPacket(AGENCY_PAY_FUNCTION_NAME, batch.getLoginName());
    getAgencyPayBatchMap(batch, packet);
    getAgencyPayDetailMap(batch, packet);
    return packet.toXmlString();
  }

  private void getAgencyPayDetailMap(TransferBatch batch, XmlPacket xmlPacket) {
    if (Objects.isNull(batch) || CollectionUtils.isEmpty(batch.getDetails())) return;
    for (TransferDetail detail : batch.getDetails()) {
      Map<String, String> map = new HashMap<>();

      map.put(IN_ACCOUNT_ID, detail.getInAccountId());
      map.put(IN_ACCOUNT_NAME, detail.getInAccountName());
      map.put(DETAIL_AMOUNT, detail.getAmount().toString());
      if (isNoneBlank(detail.getBankCity(), detail.getBankName())) {
        map.put(BANK_FLAG, "N");
        map.put(BANK_NAME, detail.getBankName());
        map.put(BANK_CITY, detail.getBankCity());
      }
      map.put(TRANSFER_ID, detail.getTransferId());

      xmlPacket.putProperty(AGENCY_PAY_DETAIL_MAP_NAME, map);
    }
  }

  private void getAgencyPayBatchMap(TransferBatch batch, XmlPacket xmlPacket) {
    Map<String, String> map = new HashMap<>();

    map.put(BUSICODE, batch.getBusiCode().getValue());
    map.put(BUSIMODE, batch.getBusiMode().getValue());
    map.put(TRANSTYPE, batch.getTransType().getValue());
    map.put(OUT_ACCOUNT_ID, batch.getOutAccountId());
    map.put(BRANCH_CODE, batch.getBranchCode().getValue());
    map.put(TRANSFER_AMOUNT, batch.getTransferAmount().toString());
    map.put(TRANSFER_COUNT, Integer.toString(batch.getTransferCount()));
    map.put(BATCHNO, join(Integer.toString(batch.getAppId()), batch.getBatchNo()));
    map.put(MEMO, batch.getMemo());

    xmlPacket.putProperty(AGENCY_PAY_BATCH_MAP_NAME, map);
  }

  private String getUrl(String loginName) {
    return URL_MAP.getOrDefault(loginName, "");
  }

  private static final String AGENCY_PAY_BATCH_MAP_NAME = "SDKATSRQX", AGENCY_PAY_DETAIL_MAP_NAME = "SDKATDRQX",
      AGENCY_QUERY_BATCH_MAP_NAME = "SDKATSQYX", AGENCY_QUERY_DETAIL_MAP_NAME = "SDKATDQYX",
      PAY_BATCH_MAP_NAME = "SDKPAYRQX", PAY_QUERY_MAP_NAME = "SDKPAYQYX", PAY_DETAIL_MAP_NAME = "DCOPDPAYX",
      PAY_RESULT_MAP = "NTQPAYRQZ", QUERY_PAY_RESULT_MAP = "NTQPAYQYZ", QUERY_AGENCY_BATCH_RESULT_MAP = "NTQATSQYZ",
      QUERY_AGENCY_DETAIL_RESULT_MAP = "NTQATDQYZ", ACCOUNT_DETAIL_QUERY_MAP_NAME = "SDKTSINFX",
      QUERY_ACCOUNT_DETAIL_RESULT_MAP = "NTQTSINFZ";

  private static final String AGENCY_PAY_FUNCTION_NAME = "AgentRequest",
      QUERY_AGENCY_PAY_RESULT_FUNCTION_NAME = "GetAgentInfo", DIRECT_PAY_FUNCTION_NAME = "DCPAYMNT",
      QUERY_AGENCY_PAY_DETAIL_FUNCTION_NAME = "GetAgentDetail", QUERY_DIRECT_PAY_FUNCTION_NAME = "GetPaymentInfo",
      QUERY_ACCOUNT_DETAIL_FUNCTION_NAME = "GetTransInfo", BATCH_REQUEST_STATE = "REQSTA", REQUEST_STATE = "REQSTS",
      RESULT_STATE = "RTNFLG", PAY_QUERY_ERROR_MESSAGE = "RTNNAR", AGENCY_ERROR_MESSAGE = "ERRDSP",
      TRANSFER_ID = "TRSDSP", DETAIL_STATE = "STSCOD", PAY_ERROR_MESSAGE = "ERRTXT";

  private static final String BUSICODE = "BUSCOD", BUSIMODE = "BUSMOD", TRANSTYPE = "TRSTYP", OUT_ACCOUNT_ID = "DBTACC",
      BRANCH_CODE = "BBKNBR", TRANSFER_COUNT = "TOTAL", TRANSFER_AMOUNT = "SUM", BATCHNO = "YURREF", MEMO = "MEMO",
      IN_ACCOUNT_ID = "ACCNBR", IN_ACCOUNT_NAME = "CLTNAM", DETAIL_AMOUNT = "TRSAMT", BANK_FLAG = "BNKFLG",
      BANK_NAME = "EACBNK", BANK_CITY = "EACCTY", BEGIN_DATE = "BGNDAT", END_DATE = "ENDDAT", SUCCESS_AMOUNT = "SUCAMT",
      SUCCESS_COUNT = "SUCNUM", OUT_TRADE_NO = "REQNBR", CURRENCY = "CCYNBR", TRADE_DATE = "ETYDAT",
      TRADE_TIME = "ETYTIM", INTEREST_DATE = "VLTDAT", TRADE_TYPE = "TRSCOD", ABSTRACT = "NARYUR",
      DEBIT_AMOUNT = "TRSAMTD", CREDIT_AMOUNT = "TRSAMTC", CREDIT_OR_DEBIT = "AMTCDR", BALANCE = "TRSBLV",
      SERIAL_NUMBER = "REFNBR", BUSINESS_NAME = "BUSNAM", USAGE = "NUSAGE", BUSINESS_ABSTRACT = "BUSNAR",
      OTHER_ABSTRACT = "OTRNAR", ACCOUNT_REGION = "C_RPYBBK", ACCOUNT_NAME = "RPYNAM", ACCOUNT_ID = "RPYACC",
      BANK_CODE = "RPYBBN", BANK_NAME_DETAIL = "RPYBNK", BANK_LOCATION = "RPYADR", COMPANY_REGION = "C_GSBBBK",
      COMPANY_ACCOUNT = "GSBACC", COMPANY_NAME = "GSBNAM", INFORMATION_FLAG = "INFFLG", HAS_ATTACHMENT_FLAG = "ATHFLG",
      INVOICE = "CHKNBR", FIX_FLAG = "RSVFLG", EXT_ABSTRACT = "NAREXT", TRADE_CODE = "TRSANL",
      MERCHANT_ORDER_ID = "REFSUB", COMPANY_CODE = "FRMCOD";

  @Override
  public void afterPropertiesSet() throws Exception {
    String JIYIFU = env.getRequiredProperty("cmb.url");
    String SOGOUWANGLUO = env.getRequiredProperty("cmb.url1");

    URL_MAP = ImmutableMap.of("吉易付牛荣荣", JIYIFU, "搜狗网络鲁芳702", SOGOUWANGLUO);
  }

  public enum ResponseCode {
    SUCCESS("0", "成功"),

    COMMIT_MASTER_FAIL("-1", "提交主机失败"),

    EXECUTE_FAIL("-2", "执行失败"),

    DATA_FORMAT_INVALID("-3", "数据格式错误"),

    NEED_LOG_IN("-4", "尚未登录系统"),

    TOO_FREQUENT("-5", "请求太频繁"),

    NOT_CERT_USER("-6", "不是证书用户"),

    USER_CANCEL("-7", "用户取消操作"),

    OTHER("-9", "其他错误");

    private String value, description;

    private ResponseCode(String value, String description) {
      this.value = value;
      this.description = description;
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }
  }

  public enum BusiRequestState {
    WAITING_FOR_AUDIT("AUT", "等待审批"),

    AUDIT_COMPLETED("NTE", "终审完毕"),

    ORDER_WAITING_FOR_CONFIRMATION("WCF", "订单待确认(商务支付用)"),

    BANK_PROCESSING("BNK", "银行处理中"),

    FINISHED("FIN", "完成"),

    WAITNG_FOR_ACKNOWLEDGEMENT("ACK", "等待确认(委托贷款用)"),

    WAITING_FOR_BANK_ACKNOWLEDGEMENT("APD", "待银行确认(国际业务用)"),

    DATA_RECEIVING("OPR", "数据接收中(代发)");

    private String value, description;

    private BusiRequestState(String value, String description) {
      this.value = value;
      this.description = description;
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }
  }

  public enum BusiResultState {
    SUCCESS("S", "成功 银行支付成功"),

    FAILED("F", "失败 银行支付失败"),

    BACK("B", "退票 银行支付被退票"),

    REJECTED("R", "否决 企业审批否决"),

    EXPIRED("D", "过期 企业过期不审批"),

    CANCELED("C", "撤消 企业撤销"),

    MERCHANT_CANCELED("M", "商户撤销订单 商务支付"),

    REFUSED("V", "拒绝 委托贷款被借款方拒绝"),

    PART("S", "部分成功");

    private String value, description;

    private BusiResultState(String value, String description) {
      this.value = value;
      this.description = description;
    }

    public String getValue() {
      return value;
    }

    public String getDescription() {
      return description;
    }
  }
}
