package io.github.messagehelper.core.dto;

import javax.validation.constraints.NotEmpty;

public class TokenRequestDto {
  @NotEmpty(message = "token: required, non-empty string")
  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
