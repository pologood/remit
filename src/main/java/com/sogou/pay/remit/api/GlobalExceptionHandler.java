package com.sogou.pay.remit.api;

import com.google.common.base.Throwables;
import org.springframework.beans.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.dao.DataAccessException;
import com.sogou.pay.remit.model.*;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ApiResult<?> internalServerError(Exception e) {
    e.printStackTrace();
    return new ApiResult<>(ErrorCode.INTERNAL_ERROR, Throwables.getStackTraceAsString(e));
  }

  @ExceptionHandler(DataAccessException.class)
  @ResponseBody
  public ApiResult<?> dataAccessExcption(Exception e) {
    e.printStackTrace();
    return new ApiResult<>(ErrorCode.INTERNAL_ERROR, "interal db error");
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  @ResponseBody
  public ApiResult<?> servletRequestBindingException(Exception e) {
    return new ApiResult<>(ErrorCode.BAD_REQUEST, Throwables.getStackTraceAsString(e));
  }
  
  @ExceptionHandler(BadRequestException.class)
  @ResponseBody
  public ApiResult<?> badRequestException(Exception e) {
    return new ApiResult<>(ErrorCode.BAD_REQUEST, Throwables.getStackTraceAsString(e));
  }

  @ExceptionHandler(InternalErrorException.class)
  @ResponseBody
  public ApiResult<?> internalErrorException(Exception e) {
    return new ApiResult<>(ErrorCode.INTERNAL_ERROR, Throwables.getStackTraceAsString(e));
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseBody
  public ApiResult<?> typeMismatchException(Exception e) {
    return new ApiResult<>(ErrorCode.BAD_REQUEST, e.toString());
  }
}
