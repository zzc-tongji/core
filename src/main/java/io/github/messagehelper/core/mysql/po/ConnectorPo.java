package io.github.messagehelper.core.mysql.po;

import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.utils.IdGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "connector")
public class ConnectorPo implements Serializable {
  @Id private Long id;

  @Column(length = Constant.CONNECTOR_LENGTH, nullable = false, unique = true)
  private String instance;

  @Column(length = Constant.CONNECTOR_LENGTH, nullable = false)
  private String category;

  @Column(length = Constant.CONNECTOR_URL_LENGTH, nullable = false)
  private String url;

  @Column(length = Constant.CONNECTOR_LENGTH, nullable = false)
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

  public ConnectorPo() {}

  public ConnectorPo(PutPostRequestDto dto) {
    setId(IdGenerator.getInstance().generate());
    constructorHelper(dto);
  }

  public ConnectorPo(PutPostRequestDto dto, Long id) {
    setId(id);
    constructorHelper(dto);
  }

  private void constructorHelper(PutPostRequestDto dto) {
    setInstance(dto.getInstance());
    setCategory(dto.getCategory());
    setUrl(dto.getUrl());
    setToken(dto.getToken());
  }
}
