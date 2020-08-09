package io.github.messagehelper.core.dto.api.configs;

import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PutRequestDto extends TokenRequestDto {
  @Length(
      min = 1,
      max = Constant.CONFIG_VALUE_LENGTH,
      message = "value: required, string with length in [1, " + Constant.CONFIG_VALUE_LENGTH + "]")
  @NotNull(
      message = "value: required, string with length in [1, " + Constant.CONFIG_VALUE_LENGTH + "]")
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
