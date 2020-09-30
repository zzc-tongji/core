package io.github.messagehelper.core.dto.api.connectors;

import java.util.ArrayList;
import java.util.Collection;

public class GetAllResponseDto {
  private final Collection<Item> data;

  public Collection<Item> getData() {
    return data;
  }

  public GetAllResponseDto() {
    this.data = new ArrayList<>();
  }
}
