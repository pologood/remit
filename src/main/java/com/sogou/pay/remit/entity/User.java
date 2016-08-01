/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月20日;
//-------------------------------------------------------
@ApiObject(name = "user", description = "用户")
public class User {

  private Integer id;

  @ApiObjectField(description = "工号")
  @NotNull(message = "uno is required")
  private Integer uno;

  @ApiObjectField(description = "姓名")
  @NotBlank(message = "name is required")
  private String name;

  @ApiObjectField(description = "邮箱")
  @NotBlank(message = "email is required")
  private String email;

  @ApiObjectField(description = "手机")
  @NotBlank(message = "mobile is required")
  @Pattern(regexp = "^[0-9]{11}$")
  private String mobile;

  @ApiObjectField(description = "角色")
  @NotNull(message = "role is required")
  private Role role;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUno() {
    return uno;
  }

  public void setUno(Integer uno) {
    this.uno = uno;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @ApiObject(name = "role", description = "角色")
  public enum Role {
    ADMIN(0), JUNIOR(1), SENIOR(2), FINAL(3);

    private int value;

    private Role(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

}
