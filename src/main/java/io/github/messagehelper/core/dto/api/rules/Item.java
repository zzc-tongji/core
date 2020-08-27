package io.github.messagehelper.core.dto.api.rules;

public class Item {
  private Long id;
  private String name;
  private String ruleIf;
  private String ruleThenInstance;
  private String ruleThenMethod;
  private String ruleThenPath;
  private String bodyTemplate;
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

  public String getRuleIf() {
    return ruleIf;
  }

  public void setRuleIf(String ruleIf) {
    this.ruleIf = ruleIf;
  }

  public String getRuleThenInstance() {
    return ruleThenInstance;
  }

  public void setRuleThenInstance(String ruleThenInstance) {
    this.ruleThenInstance = ruleThenInstance;
  }

  public String getRuleThenMethod() {
    return ruleThenMethod;
  }

  public void setRuleThenMethod(String ruleThenMethod) {
    this.ruleThenMethod = ruleThenMethod;
  }

  public String getRuleThenPath() {
    return ruleThenPath;
  }

  public void setRuleThenPath(String ruleThenPath) {
    this.ruleThenPath = ruleThenPath;
  }

  public String getBodyTemplate() {
    return bodyTemplate;
  }

  public void setBodyTemplate(String bodyTemplate) {
    this.bodyTemplate = bodyTemplate;
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
