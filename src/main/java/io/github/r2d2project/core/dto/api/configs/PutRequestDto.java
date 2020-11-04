package io.github.r2d2project.core.dto.api.configs;

import io.github.r2d2project.core.dto.api.ApiTokenRequestDto;
import io.github.r2d2project.core.persistence.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PutRequestDto extends ApiTokenRequestDto {
  private static final String EXCEPTION_MESSAGE_VALUE =
      "value: required, string with length in [1, " + Constant.CONFIG_VALUE_LENGTH + "]";

  @Length(min = 1, max = Constant.CONFIG_VALUE_LENGTH, message = EXCEPTION_MESSAGE_VALUE)
  @NotNull(message = EXCEPTION_MESSAGE_VALUE)
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
