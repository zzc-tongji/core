package io.github.r2d2project.core.dto.api.login;

public class Item {
  private String token;
  private Long expiredTimestampMs;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Long getExpiredTimestampMs() {
    return expiredTimestampMs;
  }

  public void setExpiredTimestampMs(Long expiredTimestampMs) {
    this.expiredTimestampMs = expiredTimestampMs;
  }
}
