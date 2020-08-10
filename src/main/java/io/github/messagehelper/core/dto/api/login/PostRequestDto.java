package io.github.messagehelper.core.dto.api.login;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PostRequestDto {
  @Length(min = 1, message = "password: required, string with length >= 1")
  @NotNull(message = "password: required, string with length >= 1")
  private String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
