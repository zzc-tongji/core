package io.github.r2d2project.core.dto.api.connectors;

public class Item {
  private Long id;
  private String instance;
  private String category;
  private String url;
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

  public void setRpcToken(String rpcToken) {
    this.rpcToken = rpcToken;
  }
}
