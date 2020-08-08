package io.github.messagehelper.core.mysql.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "connector")
public class ConnectorPo implements Serializable {
  private static final int LENGTH = 64;
  private static final int URL_LENGTH = 2048;

  @Id private Long id;

  @Column(length = LENGTH, nullable = false, unique = true)
  private String instance;

  @Column(length = LENGTH, nullable = false)
  private String category;

  @Column(length = URL_LENGTH, nullable = false)
  private String url;

  @Column(length = LENGTH, nullable = false)
  private String token;

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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
