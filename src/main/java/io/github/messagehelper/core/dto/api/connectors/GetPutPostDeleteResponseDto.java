package io.github.messagehelper.core.dto.api.connectors;

import io.github.messagehelper.core.mysql.po.ConnectorPo;

public class GetPutPostDeleteResponseDto {
  private ConnectorPo data;

  public ConnectorPo getData() {
    return data;
  }

  public void setData(ConnectorPo data) {
    this.data = data;
  }

  public GetPutPostDeleteResponseDto(ConnectorPo data) {
    setData(data);
  }
}
