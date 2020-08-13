package io.github.messagehelper.core.mysql.po;

import io.github.messagehelper.core.mysql.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "token")
public class TokenPo implements Serializable {
  @Id
  @Column(columnDefinition = "char(" + Constant.TOKEN_LENGTH + ")")
  private String token;

  @Column(name = "expired_timestamp_ms", nullable = false)
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

  public TokenPo() {}

  public TokenPo(String token, Long expiredTimestampMs) {
    setToken(token);
    setExpiredTimestampMs(expiredTimestampMs);
  }
}