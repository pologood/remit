/*
 * $Id$
 *
 * Copyright (c) 2015 Sogou.com. All Rights Reserved.
 */
package commons.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//--------------------- Change Logs----------------------
//@author wangwenlong Initial Created at 2016年7月6日;
//-------------------------------------------------------

public class State<T> {

  private T value;

  private String description;

  private Set<State<T>> previous = new HashSet<>();

  private Set<State<T>> nexts = new HashSet<>();

  public State(T value) {
    this.value = value;
  }

  public State(T value, String description) {
    this.value = value;
    this.description = description;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Set<State<T>> getPrevious() {
    return previous;
  }

  public Set<State<T>> getNexts() {
    return nexts;
  }

  public State<T> addNexts(List<State<T>> states) {
    this.nexts.addAll(states);
    return this;
  }

  public State<T> addPrevious(List<State<T>> states) {
    this.previous.addAll(states);
    return this;
  }

}
