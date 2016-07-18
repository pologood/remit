package com.sogou.pay.remit.model;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ApiObject(name = "ApiResult", description = "ApiResult")
public class ApiResult<Data> {

  @ApiObjectField(description = "error code")
  private int code;

  @ApiObjectField(description = "error message")
  private String message;

  @ApiObjectField(description = "payload")
  Data data;

  public ApiResult(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public ApiResult(ErrorCode code) {
    this(code, code.getMessage());
  }

  public ApiResult(ErrorCode code, String message) {
    this(code.getCode(), message);
    if (code != ErrorCode.OK) setErrorHint();
  }

  public ApiResult(ErrorCode code, Data data) {
    this(code);
    this.data = data;
  }

  public ApiResult(Data data) {
    this(ErrorCode.OK, data);
  }

  void setErrorHint() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    HttpServletResponse response = (HttpServletResponse) request.getAttribute(RESPONSE);
    String error = String.format("%s:%s", code, message.substring(0, Math.min(100, message.length())));
    if (response != null) response.setHeader(API_RESULT_ERROR, error);
    request.setAttribute(API_RESULT_ERROR, error);
  }

  public static ApiResult<?> ok() {
    return new ApiResult<>(ErrorCode.OK);
  }

  public static ApiResult<?> badRequest(String msg) {
    return new ApiResult<>(ErrorCode.BAD_REQUEST, msg);
  }

  public static ApiResult<?> unAuthorized() {
    return new ApiResult<>(ErrorCode.UNAUTHORIZED);
  }

  public static ApiResult<?> forbidden() {
    return new ApiResult<>(ErrorCode.FORBIDDEN);
  }

  public static ApiResult<?> notFound() {
    return new ApiResult<>(ErrorCode.NOT_FOUND);
  }

  public static ApiResult<?> notAcceptable(String msg) {
    return new ApiResult<>(ErrorCode.NOT_ACCEPTABLE, msg);
  }

  public static ApiResult<?> internalError(String msg) {
    return new ApiResult<>(ErrorCode.INTERNAL_ERROR, msg);
  }

  public static ApiResult<?> notImplemented() {
    return new ApiResult<>(ErrorCode.NOT_IMPLEMENTED);
  }

  public static ApiResult<?> serviceUnavailable(String msg) {
    return new ApiResult<>(ErrorCode.SERVICE_UNAVAILABLE, msg);
  }

  public static ApiResult<Map<String, String>> bindingResult(BindingResult bindingResult) {
    return new ApiResult<>(ErrorCode.BAD_REQUEST, bindingResult.getFieldErrors().stream()
        .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage())));
  }

  public static boolean isOK(ApiResult<?> result) {
    return result != null && result.getCode() == ErrorCode.OK.getCode();
  }

  public static boolean isNotOK(ApiResult<?> result) {
    return !isOK(result);
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static final String API_RESULT_ERROR = "ApiResultError", RESPONSE = "response__",
      DATE_TIME_FORMAT = "yyyyMMddHHmmss";

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
