package io.github.r2d2project.core.exception.type;

public class LogContentInvalidException extends RuntimeException {
  public LogContentInvalidException(Throwable cause) {
    super(cause);
  }

  public LogContentInvalidException(String message) {
    super(message);
  }
}
