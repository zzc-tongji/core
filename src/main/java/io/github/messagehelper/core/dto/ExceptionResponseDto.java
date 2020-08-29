package io.github.messagehelper.core.dto;

import io.github.messagehelper.core.utils.ConfigMapSingleton;

public class ExceptionResponseDto {
  private String error;
  private String document;

  public ExceptionResponseDto(String error) {
    this.error = error;
    document = ConfigMapSingleton.getInstance().load("core.api-document");
  }

  public String getError() {
    return error;
  }

  public String getDocument() {
    return document;
  }
}
