package io.github.messagehelper.core.exception;

public class InvalidContentException extends RuntimeException {
  public InvalidContentException(Throwable cause) {
    super(cause);
  }

  public InvalidContentException(String message) {
    super(message);
  }
}
