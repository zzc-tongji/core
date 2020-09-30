package io.github.messagehelper.core.dto.api.rules;

public class GetPutPostDeleteResponseDto {
  private final Item data;

  public Item getData() {
    return data;
  }

  public GetPutPostDeleteResponseDto() {
    this.data = new Item();
  }
}
