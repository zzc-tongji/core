package io.github.messagehelper.core.dto.api.logs;

import java.util.ArrayList;
import java.util.List;

public class Data {
  private Long page;
  private Long size;
  private Long total;
  private Long totalPage;
  private String previous;
  private String next;
  private List<Item> logList;

  public Long getPage() {
    return page;
  }

  public void setPage(Long page) {
    this.page = page;
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

  public Long getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(Long totalPage) {
    this.totalPage = totalPage;
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

  public List<Item> getLogList() {
    return logList;
  }

  public Data() {
    size = 0L;
    total = 0L;
    totalPage = 0L;
    previous = "";
    next = "";
    logList = new ArrayList<>();
  }
}
