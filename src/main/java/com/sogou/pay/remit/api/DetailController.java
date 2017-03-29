package com.sogou.pay.remit.api;

import java.time.LocalDate;
import java.util.Objects;

import org.jsondoc.core.annotation.ApiQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.entity.BankInfo;
import com.sogou.pay.remit.enums.Exceptions;
import com.sogou.pay.remit.manager.BankInfoManager;
import com.sogou.pay.remit.manager.CmbManager;

import com.sogou.pay.remit.model.ApiResult;

@RestController
@RequestMapping("/api")
public class DetailController {

  @Autowired
  private CmbManager cmbManager;

  @Autowired
  BankInfoManager bankInfoManager;

  @RequestMapping(value = "/accountDetail", method = RequestMethod.GET)
  public ApiResult<?> get(@ApiQueryParam(name = "accountId", description = "账号") @RequestParam String accountId,
      @ApiQueryParam(name = "begin", description = "起始时间,inclusive", format = "yyyy-MM-dd") @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
      @ApiQueryParam(name = "end", description = "结束时间,inclusive", format = "yyyy-MM-dd") @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
    BankInfo bankInfo = bankInfoManager.getBankInfo(accountId);
    if (Objects.isNull(bankInfo)) return ApiResult.badRequest(Exceptions.BANKINFO_NOT_FOUND);
    return new ApiResult<>(cmbManager.queryAccountDetail(bankInfo.getLoginName(), bankInfo.getBranchCode().getValue(),
        accountId, begin, end));
  }

}
