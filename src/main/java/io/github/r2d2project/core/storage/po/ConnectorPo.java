package io.github.r2d2project.core.storage.po;

import io.github.r2d2project.core.storage.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "connector")
public class ConnectorPo implements Serializable {
  @Id private Long id;

  @Column(length = Constant.INSTANCE_LENGTH, nullable = false, unique = true)
  private String instance;

  @Column(length = Constant.CATEGORY_LENGTH, nullable = false)
  private String category;

  @Column(length = Constant.CONNECTOR_URL_LENGTH, nullable = false)
  private String url;

  @Column(length = Constant.CONNECTOR_RPC_TOKEN_LENGTH, name = "rpc_token", nullable = false)
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

  public String getRpcToken() {
    return rpcToken;
  }

  public void setRpcToken(String token) {
    this.rpcToken = token;
  }
}
