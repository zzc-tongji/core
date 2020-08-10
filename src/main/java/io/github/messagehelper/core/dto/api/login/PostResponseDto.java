package io.github.messagehelper.core.dto.api.login;

public class PostResponseDto {
  private static class Data {
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

    public Data(String token, Long expiredTimestampMs) {
      setToken(token);
      setExpiredTimestampMs(expiredTimestampMs);
    }
  }

  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public PostResponseDto(String password, Long expiredTimestampMs) {
    setData(new Data(password, expiredTimestampMs));
  }
}
