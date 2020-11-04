package io.github.r2d2project.core.persistence.po;

import io.github.r2d2project.core.persistence.Constant;

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

  @Column(length = Constant.INSTANCE_LENGTH, name = "if_log_instance_equal", nullable = false)
  private String ifLogInstanceEqual;

  @Column(length = Constant.CATEGORY_LENGTH, name = "if_log_category_equal", nullable = false)
  private String ifLogCategoryEqual;

  @Column(
      name = "if_log_content_satisfy",
      length = Constant.RULE_IF_LOG_CONTENT_SATISFY_LENGTH,
      nullable = false)
  private String ifLogContentSatisfy;

  @Column(name = "then_use_connector_id", nullable = false)
  private Long thenUseConnectorId;

  @Column(
      name = "then_use_url_path",
      length = Constant.RULE_THEN_USE_URL_PATH_LENGTH,
      nullable = false)
  private String thenUseUrlPath;

  @Column(
      name = "then_use_header_content_type",
      length = Constant.RULE_THEN_USE_HEADER_CONTENT_TYPE_LENGTH,
      nullable = false)
  private String thenUseHeaderContentType;

  @Column(name = "then_use_body_json", nullable = false)
  private Boolean thenUseBodyJson;

  @Column(
      name = "then_use_body_template",
      length = Constant.RULE_THEN_USE_BODY_TEMPLATE_LENGTH,
      nullable = false)
  private String thenUseBodyTemplate;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = false)
  private Boolean terminate;

  @Column(nullable = false)
  private Boolean enable;

  @Column(length = Constant.RULE_ANNOTATION_LENGTH, nullable = false)
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

  public void setIfLogInstanceEqual(String ruleIfLogInstance) {
    this.ifLogInstanceEqual = ruleIfLogInstance;
  }

  public String getIfLogCategoryEqual() {
    return ifLogCategoryEqual;
  }

  public void setIfLogCategoryEqual(String ruleIfLogCategory) {
    this.ifLogCategoryEqual = ruleIfLogCategory;
  }

  public String getIfLogContentSatisfy() {
    return ifLogContentSatisfy;
  }

  public void setIfLogContentSatisfy(String ruleIf) {
    this.ifLogContentSatisfy = ruleIf;
  }

  public Long getThenUseConnectorId() {
    return thenUseConnectorId;
  }

  public void setThenUseConnectorId(Long ruleThenConnectorId) {
    this.thenUseConnectorId = ruleThenConnectorId;
  }

  public String getThenUseUrlPath() {
    return thenUseUrlPath;
  }

  public void setThenUseUrlPath(String ruleThenPath) {
    this.thenUseUrlPath = ruleThenPath;
  }

  public String getThenUseHeaderContentType() {
    return thenUseHeaderContentType;
  }

  public void setThenUseHeaderContentType(String ruleThenMethod) {
    this.thenUseHeaderContentType = ruleThenMethod;
  }

  public Boolean getThenUseBodyJson() {
    return thenUseBodyJson;
  }

  public void setThenUseBodyJson(Boolean thenUseBodyJson) {
    this.thenUseBodyJson = thenUseBodyJson;
  }

  public String getThenUseBodyTemplate() {
    return thenUseBodyTemplate;
  }

  public void setThenUseBodyTemplate(String bodyTemplate) {
    this.thenUseBodyTemplate = bodyTemplate;
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
