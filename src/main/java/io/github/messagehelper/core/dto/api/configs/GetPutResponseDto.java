package io.github.messagehelper.core.dto.api.configs;

import io.github.messagehelper.core.mysql.po.ConfigPo;

public class GetPutResponseDto {
  private ConfigPo data;

  public ConfigPo getData() {
    return data;
  }

  public void setData(ConfigPo data) {
    this.data = data;
  }

  public GetPutResponseDto(ConfigPo data) {
    setData(data);
  }
}
