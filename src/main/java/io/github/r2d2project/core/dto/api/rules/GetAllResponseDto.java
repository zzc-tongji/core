package io.github.r2d2project.core.dto.api.rules;

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
