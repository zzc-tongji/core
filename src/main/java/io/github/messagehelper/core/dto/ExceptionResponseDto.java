package io.github.messagehelper.core.dto;

public class ExceptionResponseDto {
  private String error;

  public ExceptionResponseDto(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
