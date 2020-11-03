package io.github.r2d2project.core.storage.po;

import io.github.r2d2project.core.storage.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "log")
public class LogPo implements Serializable {
  @Id
  @Column(name = Constant.LOG_COLUMN_NAME_ID)
  private Long id;

  @Column(
      length = Constant.INSTANCE_LENGTH,
      name = Constant.LOG_COLUMN_NAME_INSTANCE,
      nullable = false)
  private String instance;

  @Column(
      length = Constant.CATEGORY_LENGTH,
      name = Constant.LOG_COLUMN_NAME_CATEGORY,
      nullable = false)
  private String category;

  @Column(
      columnDefinition = "char(" + Constant.LOG_LEVEL_LENGTH + ")",
      name = Constant.LOG_COLUMN_NAME_LEVEL,
      nullable = false)
  private String level;

  @Column(name = Constant.LOG_COLUMN_NAME_TIMESTAMP_MS, nullable = false)
  private Long timestampMs;

  @Column(
      length = Constant.LOG_CONTENT_LENGTH,
      name = Constant.LOG_COLUMN_NAME_CONTENT,
      nullable = false)
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
