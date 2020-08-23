package io.github.messagehelper.core.dto.api.rules;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PutPostRequestDto extends ApiTokenRequestDto {
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
      max = Constant.RULE_CONTENT_LENGTH,
      message =
          "ifContent: required, JSON string with length in [1, "
              + Constant.RULE_CONTENT_LENGTH
              + "]")
  @NotNull(
      message =
          "ifContent: required, JSON string with length in [1, "
              + Constant.RULE_CONTENT_LENGTH
              + "]")
  private String ifContent;

  @Length(
      min = 1,
      max = Constant.RULE_CONTENT_LENGTH,
      message =
          "thenContent: required, JSON string with length in [1, "
              + Constant.RULE_CONTENT_LENGTH
              + "]")
  @NotNull(
      message =
          "thenContent: required, JSON string with length in [1, "
              + Constant.RULE_CONTENT_LENGTH
              + "]")
  private String thenContent;

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
