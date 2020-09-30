package io.github.messagehelper.core.dto.api.configs;

public class GetPutResponseDto {
  private final Item data;

  public Item getData() {
    return data;
  }

  public GetPutResponseDto() {
    this.data = new Item();
  }
}
