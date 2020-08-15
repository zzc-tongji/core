package io.github.messagehelper.core.dto;

public class TokenRequestDto {
  private String token;

  public String getToken() {
    if (token == null) {
      return "";
    }
    return token;
  }

  public void setToken(String token) {
    if (token == null) {
      this.token = "";
    }
    this.token = token;
  }
}
