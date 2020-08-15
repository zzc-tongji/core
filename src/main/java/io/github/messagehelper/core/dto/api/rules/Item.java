package io.github.messagehelper.core.dto.api.rules;

public class Item {
  private Long id;
  private String name;
  private String ifContent;
  private String thenContent;
  private Integer priority;
  private Boolean terminate;
  private Boolean enable;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIfContent() {
    return ifContent;
  }

  public void setIfContent(String ifContent) {
    this.ifContent = ifContent;
  }

  public String getThenContent() {
    return thenContent;
  }

  public void setThenContent(String thenContent) {
    this.thenContent = thenContent;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Boolean getTerminate() {
    return terminate;
  }

  public void setTerminate(Boolean terminate) {
    this.terminate = terminate;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }
}
