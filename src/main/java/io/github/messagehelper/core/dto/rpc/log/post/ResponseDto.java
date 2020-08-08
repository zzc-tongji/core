package io.github.messagehelper.core.dto.rpc.log.post;

public class ResponseDto {
  private String reason;

  public ResponseDto(String reason) {
    this.reason = reason;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
