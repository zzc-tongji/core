package io.github.messagehelper.core.dto.api.rules;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PutPostRequestDto extends ApiTokenRequestDto {
  private static final String METHOD_MESSAGE =
      "thenUseHttpMethod: required, string in {\""
          + Constant.RULE_THEN_METHOD_GET
          + "\", \""
          + Constant.RULE_THEN_METHOD_POST
          + "\"} with length in [1, "
          + Constant.RULE_THEN_PATH_LENGTH
          + "]";

  @Length(
      min = 1,
      max = Constant.RULE_NAME_LENGTH,
      message =
          "name: required, string with length in [1, "
              + Constant.RULE_NAME_LENGTH
              + "] which cannot be converted to long")
  @NotNull(
      message =
          "name: required, string with length in [1, "
              + Constant.RULE_NAME_LENGTH
              + "] which cannot be converted to long")
  private String name;

  @Length(
      min = 1,
      max = Constant.INSTANCE_LENGTH,
      message =
          "ifLogInstanceEqual: required, string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "]")
  @NotNull(
      message =
          "ifLogInstanceEqual: required, string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "]")
  private String ifLogInstanceEqual;

  @Length(
      min = 1,
      max = Constant.CATEGORY_LENGTH,
      message =
          "ifLogCategoryEqual: required, string with length in [1, "
              + Constant.CATEGORY_LENGTH
              + "]")
  @NotNull(
      message =
          "ifLogCategoryEqual: required, string with length in [1, "
              + Constant.CATEGORY_LENGTH
              + "]")
  private String ifLogCategoryEqual;

  @Length(
      min = 1,
      max = Constant.RULE_IF_LENGTH,
      message =
          "ifLogContentSatisfy: required, JSON string with length in [1, "
              + Constant.RULE_IF_LENGTH
              + "]")
  @NotNull(
      message =
          "ifLogContentSatisfy: required, JSON string with length in [1, "
              + Constant.RULE_IF_LENGTH
              + "]")
  private String ifLogContentSatisfy;

  @NotNull(message = "thenUseConnectorId: required, long")
  private Long thenUseConnectorId;

  @NotNull(message = METHOD_MESSAGE)
  private String thenUseHttpMethod;

  @Length(
      min = 1,
      max = Constant.RULE_THEN_PATH_LENGTH,
      message =
          "thenUseUrlPath: required, string with length in [1, "
              + Constant.RULE_THEN_PATH_LENGTH
              + "]")
  @NotNull(
      message =
          "thenUseUrlPath: required, string with length in [1, "
              + Constant.RULE_THEN_PATH_LENGTH
              + "]")
  private String thenUseUrlPath;

  @Length(
      min = 1,
      max = Constant.RULE_BODY_TEMPLATE_LENGTH,
      message =
          "ThenUseBodyTemplate: required, JSON string with length in [1, "
              + Constant.RULE_BODY_TEMPLATE_LENGTH
              + "]")
  @NotNull(
      message =
          "ThenUseBodyTemplate: required, JSON string with length in [1, "
              + Constant.RULE_BODY_TEMPLATE_LENGTH
              + "]")
  private String ThenUseBodyTemplate;

  @Positive(message = "priority: required, positive integer")
  @NotNull(message = "priority: required, positive integer")
  private Integer priority;

  @NotNull(message = "terminate: required, boolean")
  private Boolean terminate;

  @NotNull(message = "enable: required, boolean")
  private Boolean enable;

  @Length(
      max = Constant.RULE_ANNOTATION_LENGTH,
      message =
          "annotation: required, string with length in [0, "
              + Constant.RULE_ANNOTATION_LENGTH
              + "]")
  @NotNull(
      message =
          "annotation: required, string with length in [0, "
              + Constant.RULE_ANNOTATION_LENGTH
              + "]")
  private String annotation;

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
    return ThenUseBodyTemplate;
  }

  public void setThenUseBodyTemplate(String thenUseBodyTemplate) {
    this.ThenUseBodyTemplate = thenUseBodyTemplate;
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
