package io.github.messagehelper.core.exception;

public class LogContentInvalidException extends RuntimeException {
  public LogContentInvalidException(Throwable cause) {
    super(cause);
  }

  public LogContentInvalidException(String message) {
    super(message);
  }
}
