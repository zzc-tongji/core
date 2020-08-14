package io.github.messagehelper.core.processor.log;

import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.processor.log.content.Content;

public class Log {
  public static Log parse(PostRequestDto dto) {
    Log log = new Log();
    log.setId(dto.getId());
    log.setInstance(dto.getInstance());
    log.setLevel(dto.getLevel());
    log.setTimestampMs(dto.getTimestampMs());
    log.setContent(dto.getCategory(), dto.getContent());
    return log;
  }

  private Long id;
  private String instance;
  private String level;
  private String category;
  private Long timestampMs;
  private Content content;

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

  public Long getTimestampMs() {
    return timestampMs;
  }

  public void setTimestampMs(Long timestampMs) {
    this.timestampMs = timestampMs;
  }

  public Content getContent() {
    return content;
  }

  public void setContent(String category, String json) {
    this.category = category;
    this.content = Content.parse(category, json);
  }

  private Log() {}
}
