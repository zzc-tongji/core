package io.github.r2d2project.core.dto.api.login;

public class Item {
  private String apiToken;
  private Long expiredTimestampMs;

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public Long getExpiredTimestampMs() {
    return expiredTimestampMs;
  }

  public void setExpiredTimestampMs(Long expiredTimestampMs) {
    this.expiredTimestampMs = expiredTimestampMs;
  }
}
