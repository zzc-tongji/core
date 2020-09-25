package io.github.messagehelper.core.dto.api.rules;

public class Item {
  private Long id;
  private String name;
  private String ifLogInstanceEqual;
  private String ifLogCategoryEqual;
  private String ifLogContentSatisfy;
  private Long thenUseConnectorId;
  private String thenUseHttpMethod;
  private String thenUseUrlPath;
  private String thenUseBodyTemplate;
  private Integer priority;
  private Boolean terminate;
  private Boolean enable;
  private String annotation;

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

  public String getIfLogInstanceEqual() {
    return ifLogInstanceEqual;
  }

  public void setIfLogInstanceEqual(String ifLogInstanceEqual) {
    this.ifLogInstanceEqual = ifLogInstanceEqual;
  }

  public String getIfLogCategoryEqual() {
    return ifLogCategoryEqual;
  }

  public void setIfLogCategoryEqual(String ifLogCategoryEqual) {
    this.ifLogCategoryEqual = ifLogCategoryEqual;
  }

  public String getIfLogContentSatisfy() {
    return ifLogContentSatisfy;
  }

  public void setIfLogContentSatisfy(String ifLogContentSatisfy) {
    this.ifLogContentSatisfy = ifLogContentSatisfy;
  }

  public Long getThenUseConnectorId() {
    return thenUseConnectorId;
  }

  public void setThenUseConnectorId(Long thenUseConnectorId) {
    this.thenUseConnectorId = thenUseConnectorId;
  }

  public String getThenUseHttpMethod() {
    return thenUseHttpMethod;
  }

  public void setThenUseHttpMethod(String thenUseHttpMethod) {
    this.thenUseHttpMethod = thenUseHttpMethod;
  }

  public String getThenUseUrlPath() {
    return thenUseUrlPath;
  }

  public void setThenUseUrlPath(String thenUseUrlPath) {
    this.thenUseUrlPath = thenUseUrlPath;
  }

  public String getThenUseBodyTemplate() {
    return thenUseBodyTemplate;
  }

  public void setThenUseBodyTemplate(String thenUseBodyTemplate) {
    this.thenUseBodyTemplate = thenUseBodyTemplate;
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

  public String getAnnotation() {
    return annotation;
  }

  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }
}
