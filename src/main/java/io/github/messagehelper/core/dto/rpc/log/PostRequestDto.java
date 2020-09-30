package io.github.messagehelper.core.dto.rpc.log;

import io.github.messagehelper.core.dto.rpc.RpcTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PostRequestDto extends RpcTokenRequestDto {
  public static final String EXCEPTION_MESSAGE_CONTENT =
      "content: required, string with length in [1, " + Constant.LOG_CONTENT_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_ID = "id: required, long";
  private static final String EXCEPTION_MESSAGE_INSTANCE =
      "instance: required, string with length in [1, " + Constant.INSTANCE_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_CATEGORY =
      "category: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_LEVEL =
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
  private static final String EXCEPTION_MESSAGE_TIMESTAMP_MS = "timestampMs: required, long";

  private static final String REGEXP_LEVEL =
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

  @NotNull(message = EXCEPTION_MESSAGE_ID)
  private Long id;

  @Length(min = 1, max = Constant.INSTANCE_LENGTH, message = EXCEPTION_MESSAGE_INSTANCE)
  @NotNull(message = EXCEPTION_MESSAGE_INSTANCE)
  private String instance;

  @Length(min = 1, max = Constant.CATEGORY_LENGTH, message = EXCEPTION_MESSAGE_CATEGORY)
  @NotNull(message = EXCEPTION_MESSAGE_CATEGORY)
  private String category;

  @Pattern(regexp = REGEXP_LEVEL, message = EXCEPTION_MESSAGE_LEVEL)
  @NotNull(message = EXCEPTION_MESSAGE_LEVEL)
  private String level;

  @NotNull(message = EXCEPTION_MESSAGE_TIMESTAMP_MS)
  private Long timestampMs;

  @Length(min = 1, max = Constant.LOG_CONTENT_LENGTH, message = EXCEPTION_MESSAGE_CONTENT)
  @NotNull(message = EXCEPTION_MESSAGE_CONTENT)
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
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
