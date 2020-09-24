package io.github.messagehelper.core.mysql.po;

import io.github.messagehelper.core.mysql.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "rule")
public class RulePo implements Serializable {
  @Id private Long id;

  @Column(length = Constant.RULE_NAME_LENGTH, nullable = false, unique = true)
  private String name;

  @Column(name = "rule_if", length = Constant.RULE_IF_LENGTH, nullable = false)
  private String ruleIf;

  @Column(name = "rule_then_instance", length = Constant.INSTANCE_LENGTH, nullable = false)
  private String ruleThenInstance;

  @Column(name = "rule_then_method", length = Constant.RULE_THEN_METHOD_LENGTH, nullable = false)
  private String ruleThenMethod;

  @Column(name = "rule_then_path", length = Constant.RULE_THEN_PATH_LENGTH, nullable = false)
  private String ruleThenPath;

  @Column(name = "body_template", length = Constant.RULE_BODY_TEMPLATE_LENGTH, nullable = false)
  private String bodyTemplate;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = false)
  private Boolean terminate;

  @Column(nullable = false)
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
