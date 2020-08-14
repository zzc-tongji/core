package io.github.messagehelper.core.dto.api.login;

public class PostResponseDto {
  private Item data;

  public Item getData() {
    return data;
  }

  public PostResponseDto() {
    this.data = new Item();
  }
}
