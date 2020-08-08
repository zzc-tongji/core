package io.github.messagehelper.core.dto;

import io.github.messagehelper.core.exception.AuthFailureException;

import javax.validation.constraints.NotEmpty;

public class AuthDto {
  @NotEmpty(message = "token: required, non-empty string")
  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void authenticate(String token) {
    if (!this.token.equals(token)) {
      throw new AuthFailureException("token: not valid");
    }
  }
}
