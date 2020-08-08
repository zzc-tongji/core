package io.github.messagehelper.core.dto;

import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PostRpcLogRequestDto extends AuthDto {
  private static final String LEVEL_REGEXP =
      "^("
          + Constant.LOG_ERR
          + "|"
          + Constant.LOG_WARN
          + "|"
          + Constant.LOG_INFO
          + "|"
          + Constant.LOG_VERB
          + "|"
          + Constant.LOG_SILL
          + ")$";
  private static final String LEVEL_MESSAGE =
      "level: required, string in {\""
          + Constant.LOG_ERR
          + "\", \""
          + Constant.LOG_WARN
          + "\", \""
          + Constant.LOG_INFO
          + "\", \""
          + Constant.LOG_VERB
          + "\", \""
          + Constant.LOG_SILL
          + "\"}";

  @NotNull(message = "id: required, long")
  private Long id;

  @Length(
      min = 1,
      max = Constant.LOG_INSTANCE_LENGTH,
      message =
          "instance: required, string with length in [1, " + Constant.LOG_INSTANCE_LENGTH + "]")
  @NotNull(
      message =
          "instance: required, string with length in [1, " + Constant.LOG_INSTANCE_LENGTH + "]")
  private String instance;

  @Pattern(regexp = LEVEL_REGEXP, message = LEVEL_MESSAGE)
  @NotNull(message = LEVEL_MESSAGE)
  private String level;

  @Length(
      min = 1,
      max = Constant.LOG_CATEGORY_LENGTH,
      message =
          "category: required, string with length in [1, " + Constant.LOG_CATEGORY_LENGTH + "]")
  @NotNull(
      message =
          "content: required, string with length in [1, " + Constant.LOG_CATEGORY_LENGTH + "]")
  private String category;

  @NotNull(message = "timestampMs: required, long")
  private Long timestampMs;

  @Length(
      min = 1,
      max = Constant.LOG_CONTENT_LENGTH,
      message = "content: required, string with length in [1, " + Constant.LOG_CONTENT_LENGTH + "]")
  @NotNull(
      message = "content: required, string with length in [1, " + Constant.LOG_CONTENT_LENGTH + "]")
  private String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Long getTimestampMs() {
    return timestampMs;
  }

  public void setTimestampMs(Long timestampMs) {
    this.timestampMs = timestampMs;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
