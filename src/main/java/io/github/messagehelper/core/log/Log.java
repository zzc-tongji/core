package io.github.messagehelper.core.log;

import io.github.messagehelper.core.dto.rpc.log.post.RequestDto;
import io.github.messagehelper.core.log.content.Content;

public class Log {
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

  public void setCategory(String category) {
    this.category = category;
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

  public void setContent(Content content) {
    this.content = content;
  }

  public Log(RequestDto dto) {
    id = dto.getId();
    instance = dto.getInstance();
    level = dto.getLevel();
    category = dto.getCategory();
    timestampMs = dto.getTimestampMs();
    content = Content.parse(dto.getCategory(), dto.getContent());
  }
}
