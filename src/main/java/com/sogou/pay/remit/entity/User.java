/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package com.sogou.pay.remit.entity;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月20日;
//-------------------------------------------------------
public class User {

  private Integer id;

  private Integer uno;

  private String userName;

  private String userEmail;

  private String mobile;

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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
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
