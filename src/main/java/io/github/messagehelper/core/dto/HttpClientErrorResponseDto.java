package io.github.messagehelper.core.dto;

import io.github.messagehelper.core.utils.ConfigMapSingleton;
import io.github.messagehelper.core.utils.ThrowableTool;

public class HttpClientErrorResponseDto {
  private final String error;
  private final String document;

  public HttpClientErrorResponseDto(Throwable throwable) {
    this.error = ThrowableTool.getInstance().convertToString(throwable);
    document = ConfigMapSingleton.getInstance().load("core.api-document");
  }

  public HttpClientErrorResponseDto(String string) {
    this.error = string;
    document = ConfigMapSingleton.getInstance().load("core.api-document");
  }

  public String getError() {
    return error;
  }

  public String getDocument() {
    return document;
  }
}
