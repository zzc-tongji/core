package io.github.messagehelper.core.mysql.po;

import io.github.messagehelper.core.mysql.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "log")
public class LogPo implements Serializable {
  @Id private Long id;

  @Column(length = Constant.LOG_INSTANCE_LENGTH, nullable = false)
  private String instance;

  @Column(columnDefinition = "char(" + Constant.LOG_LEVEL_LENGTH + ")", nullable = false)
  private String level;

  @Column(length = Constant.LOG_CATEGORY_LENGTH, nullable = false)
  private String category;

  @Column(name = "timestamp_ms", nullable = false)
  private Long timestampMs;

  @Column(length = Constant.LOG_CONTENT_LENGTH, nullable = false)
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
