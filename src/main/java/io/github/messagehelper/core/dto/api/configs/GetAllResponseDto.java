package io.github.messagehelper.core.dto.api.configs;

import io.github.messagehelper.core.mysql.po.ConfigPo;

import java.util.List;

public class GetAllResponseDto {
  private List<ConfigPo> data;

  public List<ConfigPo> getData() {
    return data;
  }

  public void setData(List<ConfigPo> data) {
    this.data = data;
  }

  public GetAllResponseDto(List<ConfigPo> data) {
    setData(data);
  }
}
