package com.sogou.pay.remit.entity;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月12日;
//-------------------------------------------------------
public class App {

  private Integer id;

  private String name;

  private String signKey;

  private Status status;

  public static enum Status {
    INVALID(0), VALID(1);

    private int value;

    private Status(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSignKey() {
    return signKey;
  }

  public void setSignKey(String key) {
    this.signKey = key;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

}
