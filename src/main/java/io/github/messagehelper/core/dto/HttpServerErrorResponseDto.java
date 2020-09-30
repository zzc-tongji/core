package io.github.messagehelper.core.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpServerErrorResponseDto {
  private final String dateTime;
  private final String stackTrace;

  public HttpServerErrorResponseDto(String stackTrace) {
    dateTime = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")).format(new Date());
    this.stackTrace = stackTrace;
  }

  public String getDateTime() {
    return dateTime;
  }

  public String getStackTrace() {
    return stackTrace;
  }
}
