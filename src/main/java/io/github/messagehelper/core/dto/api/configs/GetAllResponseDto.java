package io.github.messagehelper.core.dto.api.configs;

import java.util.ArrayList;
import java.util.Collection;

public class GetAllResponseDto {
  private Collection<Item> data;

  public Collection<Item> getData() {
    return data;
  }

  public GetAllResponseDto() {
    this.data = new ArrayList<>();
  }
}
