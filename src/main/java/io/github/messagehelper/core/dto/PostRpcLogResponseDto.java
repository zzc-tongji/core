package io.github.messagehelper.core.dto;

public class PostRpcLogResponseDto {
  private String reason;

  public PostRpcLogResponseDto(String reason) {
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
