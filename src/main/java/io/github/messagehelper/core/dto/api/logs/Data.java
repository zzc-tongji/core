package io.github.messagehelper.core.dto.api.logs;

import java.util.ArrayList;
import java.util.List;

public class Data {
  private List<Item> logList;
  private Long size;
  private Long total;
  private Long pageTotal;
  private String previous;
  private String next;

  public List<Item> getLogList() {
    return logList;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public Long getPageTotal() {
    return pageTotal;
  }

  public void setPageTotal(Long pageTotal) {
    this.pageTotal = pageTotal;
  }

  public String getPrevious() {
    return previous;
  }

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public Data() {
    logList = new ArrayList<>();
    size = 0L;
    total = 0L;
    pageTotal = 0L;
    previous = "";
    next = "";
  }
}
