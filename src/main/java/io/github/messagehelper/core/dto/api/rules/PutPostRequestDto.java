package io.github.messagehelper.core.dto.api.rules;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PutPostRequestDto extends ApiTokenRequestDto {
  private static final String METHOD_MESSAGE =
      "ruleThenMethod: required, string in {\""
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
      max = Constant.RULE_IF_LENGTH,
      message = "ruleIf: required, JSON string with length in [1, " + Constant.RULE_IF_LENGTH + "]")
  @NotNull(
      message = "ruleIf: required, JSON string with length in [1, " + Constant.RULE_IF_LENGTH + "]")
  private String ruleIf;

  @Length(
      min = 1,
      max = Constant.INSTANCE_LENGTH,
      message =
          "ruleThenInstance: required, JSON string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "]")
  @NotNull(
      message =
          "ruleThenInstance: required, JSON string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "]")
  private String ruleThenInstance;

  @NotNull(message = METHOD_MESSAGE)
  private String ruleThenMethod;

  @Length(
      min = 1,
      max = Constant.RULE_THEN_PATH_LENGTH,
      message =
          "ruleThenPath: required, JSON string with length in [1, "
              + Constant.RULE_THEN_PATH_LENGTH
              + "]")
  @NotNull(
      message =
          "ruleThenPath: required, JSON string with length in [1, "
              + Constant.RULE_THEN_PATH_LENGTH
              + "]")
  private String ruleThenPath;

  @Length(
      min = 1,
      max = Constant.RULE_BODY_TEMPLATE_LENGTH,
      message =
          "bodyTemplate: required, JSON string with length in [1, "
              + Constant.RULE_BODY_TEMPLATE_LENGTH
              + "]")
  @NotNull(
      message =
          "bodyTemplate: required, JSON string with length in [1, "
              + Constant.RULE_BODY_TEMPLATE_LENGTH
              + "]")
  private String bodyTemplate;

  @Positive(message = "priority: required, positive integer")
  @NotNull(message = "priority: required, positive integer")
  private Integer priority;

  @NotNull(message = "terminate: required, boolean")
  private Boolean terminate;

  @NotNull(message = "enable: required, boolean")
  private Boolean enable;

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
