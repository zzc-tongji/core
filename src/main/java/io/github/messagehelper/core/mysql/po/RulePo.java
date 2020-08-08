package io.github.messagehelper.core.mysql.po;

import io.github.messagehelper.core.mysql.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "rule")
public class RulePo implements Serializable {
  @Id private Long id;

  @Column(length = Constant.RULE_NAME_LENGTH, nullable = false, unique = true)
  private String name;

  @Column(name = "rule_if", length = Constant.RULE_CONTENT_LENGTH, nullable = false)
  private String ifContent;

  @Column(name = "rule_then", length = Constant.RULE_CONTENT_LENGTH, nullable = false)
  private String thenContent;

  @Column(nullable = false)
  private Integer priority;

  @Column(nullable = false)
  private Boolean terminate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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
}
