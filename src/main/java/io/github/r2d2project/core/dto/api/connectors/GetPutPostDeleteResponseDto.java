package io.github.r2d2project.core.dto.api.connectors;

public class GetPutPostDeleteResponseDto {
  private final Item data;

  public Item getData() {
    return data;
  }

  public GetPutPostDeleteResponseDto() {
    this.data = new Item();
  }
}
