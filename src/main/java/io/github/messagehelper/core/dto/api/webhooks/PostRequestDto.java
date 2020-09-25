package io.github.messagehelper.core.dto.api.webhooks;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PostRequestDto extends ApiTokenRequestDto {
  private static final String EXCEPTION_MESSAGE_VALUE_1 = "value1: required";
  private static final String EXCEPTION_MESSAGE_VALUE_2 = "value2: required";
  private static final String EXCEPTION_MESSAGE_VALUE_3 = "value3: required";

  @Length(message = EXCEPTION_MESSAGE_VALUE_1)
  @NotNull(message = EXCEPTION_MESSAGE_VALUE_1)
  private String value1;

  @Length(message = EXCEPTION_MESSAGE_VALUE_2)
  @NotNull(message = EXCEPTION_MESSAGE_VALUE_2)
  private String value2;

  @Length(message = EXCEPTION_MESSAGE_VALUE_3)
  @NotNull(message = EXCEPTION_MESSAGE_VALUE_3)
  private String value3;

  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  public String getValue3() {
    return value3;
  }

  public void setValue3(String value3) {
    this.value3 = value3;
  }
}
