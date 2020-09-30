package io.github.messagehelper.core.dto.api.rules;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PutPostRequestDto extends ApiTokenRequestDto {
  public static final String EXCEPTION_MESSAGE_IF_LOG_CONTENT_SATISFY =
      "ifLogContentSatisfy: required, JSON string as array with length in [1, "
          + Constant.RULE_IF_LOG_CONTENT_SATISFY_LENGTH
          + "]";
  public static final String EXCEPTION_MESSAGE_NAME =
      "name: required, string with length in [1, "
          + Constant.RULE_NAME_LENGTH
          + "] which cannot be converted to long";
  private static final String EXCEPTION_MESSAGE_IF_LOG_INSTANCE_EQUAL =
      "ifLogInstanceEqual: required, string with length in [1, " + Constant.INSTANCE_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_IF_LOG_CATEGORY_EQUAL =
      "ifLogCategoryEqual: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_THEN_USE_CONNECTOR_ID =
      "thenUseConnectorId: required, long";
  private static final String EXCEPTION_MESSAGE_THEN_USE_URL_PATH =
      "thenUseUrlPath: required, string with length in [1, "
          + Constant.RULE_THEN_USE_URL_PATH_LENGTH
          + "]";
  public static final String EXCEPTION_MESSAGE_THEN_USE_HEADER_CONTENT_TYPE =
      "thenUseHeaderContentType: required, value of header \"content-type\" as string with length in [0, "
          + Constant.RULE_THEN_USE_HEADER_CONTENT_TYPE_LENGTH
          + "]";
  private static final String EXCEPTION_MESSAGE_THEN_USE_BODY_TEMPLATE =
      "thenUseBodyTemplate: required, JSON string with length in [1, "
          + Constant.RULE_THEN_USE_BODY_TEMPLATE_LENGTH
          + "]";
  private static final String EXCEPTION_MESSAGE_PRIORITY = "priority: required, positive integer";
  private static final String EXCEPTION_MESSAGE_TERMINATE = "terminate: required, boolean";
  private static final String EXCEPTION_MESSAGE_ENABLE = "enable: required, boolean";
  private static final String EXCEPTION_MESSAGE_ANNOTATION =
      "annotation: required, string with length in [0, " + Constant.RULE_ANNOTATION_LENGTH + "]";

  @Length(min = 1, max = Constant.RULE_NAME_LENGTH, message = EXCEPTION_MESSAGE_NAME)
  @NotNull(message = EXCEPTION_MESSAGE_NAME)
  private String name;

  @Length(
      min = 1,
      max = Constant.INSTANCE_LENGTH,
      message = EXCEPTION_MESSAGE_IF_LOG_INSTANCE_EQUAL)
  @NotNull(message = EXCEPTION_MESSAGE_IF_LOG_INSTANCE_EQUAL)
  private String ifLogInstanceEqual;

  @Length(
      min = 1,
      max = Constant.CATEGORY_LENGTH,
      message = EXCEPTION_MESSAGE_IF_LOG_CATEGORY_EQUAL)
  @NotNull(message = EXCEPTION_MESSAGE_IF_LOG_CATEGORY_EQUAL)
  private String ifLogCategoryEqual;

  @Length(
      min = 1,
      max = Constant.RULE_IF_LOG_CONTENT_SATISFY_LENGTH,
      message = EXCEPTION_MESSAGE_IF_LOG_CONTENT_SATISFY)
  @NotNull(message = EXCEPTION_MESSAGE_IF_LOG_CONTENT_SATISFY)
  private String ifLogContentSatisfy;

  @NotNull(message = EXCEPTION_MESSAGE_THEN_USE_CONNECTOR_ID)
  private Long thenUseConnectorId;

  @Length(
      min = 1,
      max = Constant.RULE_THEN_USE_URL_PATH_LENGTH,
      message = EXCEPTION_MESSAGE_THEN_USE_URL_PATH)
  @NotNull(message = EXCEPTION_MESSAGE_THEN_USE_URL_PATH)
  private String thenUseUrlPath;

  @Length(
      max = Constant.RULE_THEN_USE_HEADER_CONTENT_TYPE_LENGTH,
      message = EXCEPTION_MESSAGE_THEN_USE_HEADER_CONTENT_TYPE)
  @NotNull(message = EXCEPTION_MESSAGE_THEN_USE_HEADER_CONTENT_TYPE)
  private String thenUseHeaderContentType;

  @Length(
      min = 1,
      max = Constant.RULE_THEN_USE_BODY_TEMPLATE_LENGTH,
      message = EXCEPTION_MESSAGE_THEN_USE_BODY_TEMPLATE)
  @NotNull(message = EXCEPTION_MESSAGE_THEN_USE_BODY_TEMPLATE)
  private String thenUseBodyTemplate;

  @Positive(message = EXCEPTION_MESSAGE_PRIORITY)
  @NotNull(message = EXCEPTION_MESSAGE_PRIORITY)
  private Integer priority;

  @NotNull(message = EXCEPTION_MESSAGE_TERMINATE)
  private Boolean terminate;

  @NotNull(message = EXCEPTION_MESSAGE_ENABLE)
  private Boolean enable;

  @Length(max = Constant.RULE_ANNOTATION_LENGTH, message = EXCEPTION_MESSAGE_ANNOTATION)
  @NotNull(message = EXCEPTION_MESSAGE_ANNOTATION)
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

  public String getThenUseUrlPath() {
    return thenUseUrlPath;
  }

  public void setThenUseUrlPath(String thenUseUrlPath) {
    this.thenUseUrlPath = thenUseUrlPath;
  }

  public String getThenUseHeaderContentType() {
    return thenUseHeaderContentType;
  }

  public void setThenUseHeaderContentType(String thenUseHeaderContentType) {
    this.thenUseHeaderContentType = thenUseHeaderContentType;
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
