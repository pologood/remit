package com.sogou.pay.remit.api;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sogou.pay.remit.job.TransferJob;
import com.sogou.pay.remit.model.ApiResult;

@RestController
@RequestMapping("/api")
public class DetailController {

  @Autowired
  private TransferJob job;

  @RequestMapping(value = "/job/detail", method = RequestMethod.GET)
  public ApiResult<?> callback(@RequestParam @DateTimeFormat(iso = ISO.DATE) Optional<LocalDate> date) {
    job.detail(date.orElse(LocalDate.now().minusDays(1)));
    return ApiResult.ok();
  }

}
