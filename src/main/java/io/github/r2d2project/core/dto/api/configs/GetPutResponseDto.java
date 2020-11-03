package io.github.r2d2project.core.dto.api.configs;

public class GetPutResponseDto {
  private final Item data;

  public Item getData() {
    return data;
  }

  public GetPutResponseDto() {
    this.data = new Item();
  }
}
