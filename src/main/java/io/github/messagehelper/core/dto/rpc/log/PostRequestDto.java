package io.github.messagehelper.core.dto.rpc.log;

import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PostRequestDto {
  private static final String LEVEL_REGEXP =
      "^("
          + Constant.LOG_LEVEL_ERR
          + "|"
          + Constant.LOG_LEVEL_WARN
          + "|"
          + Constant.LOG_LEVEL_INFO
          + "|"
          + Constant.LOG_LEVEL_VERB
          + "|"
          + Constant.LOG_LEVEL_SILL
          + ")$";
  private static final String LEVEL_MESSAGE =
      "level: required, string in {\""
          + Constant.LOG_LEVEL_ERR
          + "\", \""
          + Constant.LOG_LEVEL_WARN
          + "\", \""
          + Constant.LOG_LEVEL_INFO
          + "\", \""
          + Constant.LOG_LEVEL_VERB
          + "\", \""
          + Constant.LOG_LEVEL_SILL
          + "\"}";

  @NotNull(message = "id: required, long")
  private Long id;

  @Length(
      min = 1,
      max = Constant.INSTANCE_LENGTH,
      message = "instance: required, string with length in [1, " + Constant.INSTANCE_LENGTH + "]")
  @NotNull(
      message = "instance: required, string with length in [1, " + Constant.INSTANCE_LENGTH + "]")
  private String instance;

  @Pattern(regexp = LEVEL_REGEXP, message = LEVEL_MESSAGE)
  @NotNull(message = LEVEL_MESSAGE)
  private String level;

  @Length(
      min = 1,
      max = Constant.CATEGORY_LENGTH,
      message = "category: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]")
  @NotNull(
      message = "category: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]")
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

  @Length(
      min = 1,
      max = Constant.CONFIG_VALUE_LENGTH,
      message =
          "rpcToken: required, string with length in [1, " + Constant.CONFIG_VALUE_LENGTH + "]")
  @NotNull(
      message =
          "rpcToken: required, string with length in [1, " + Constant.CONFIG_VALUE_LENGTH + "]")
  private String rpcToken;

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

  public String getRpcToken() {
    return rpcToken;
  }

  public void setRpcToken(String rpcToken) {
    this.rpcToken = rpcToken;
  }
}
