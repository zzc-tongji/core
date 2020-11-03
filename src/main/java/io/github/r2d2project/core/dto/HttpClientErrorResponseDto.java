package io.github.r2d2project.core.dto;

import io.github.r2d2project.core.utils.ConfigMapSingleton;
import io.github.r2d2project.core.utils.ThrowableTool;

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
