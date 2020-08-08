package io.github.messagehelper.core.mysql.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "config")
public class ConfigPo implements Serializable {
  private static final int LENGTH = 512;

  @Id
  @Column(length = LENGTH, name = "item_key")
  private String key;

  @Column(length = LENGTH, name = "item_value", nullable = false)
  private String value;

  public ConfigPo() {}

  public ConfigPo(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
