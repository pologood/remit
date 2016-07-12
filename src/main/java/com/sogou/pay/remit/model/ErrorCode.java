package com.sogou.pay.remit.model;

public enum ErrorCode {

  OK(0, "OK"),

  BAD_REQUEST(400, "bad request"),

  UNAUTHORIZED(401, "unauthorized"),

  FORBIDDEN(403, "forbidden"),

  NOT_FOUND(404, "entity not found"),

  NOT_ACCEPTABLE(406, "not acceptable"),

  INTERNAL_ERROR(500, "internal server error"),

  NOT_IMPLEMENTED(501, "not implemented"),

  SERVICE_UNAVAILABLE(503, "service unavailable");

  private int code;

  private String message;

  private ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

}
