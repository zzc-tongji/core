package io.github.messagehelper.core.dto.api.connectors;

import io.github.messagehelper.core.mysql.po.ConnectorPo;

import java.util.Collection;

public class GetAllResponseDto {
  private Collection<ConnectorPo> data;

  public Collection<ConnectorPo> getData() {
    return data;
  }

  public void setData(Collection<ConnectorPo> data) {
    this.data = data;
  }

  public GetAllResponseDto(Collection<ConnectorPo> data) {
    setData(data);
  }
}
