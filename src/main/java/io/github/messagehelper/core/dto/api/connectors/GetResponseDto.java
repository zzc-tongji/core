package io.github.messagehelper.core.dto.api.connectors;

import io.github.messagehelper.core.mysql.po.ConnectorPo;

public class GetResponseDto {
  private ConnectorPo data;

  public ConnectorPo getData() {
    return data;
  }

  public void setData(ConnectorPo data) {
    this.data = data;
  }

  public GetResponseDto(ConnectorPo data) {
    setData(data);
  }
}
