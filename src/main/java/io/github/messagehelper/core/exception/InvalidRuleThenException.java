package io.github.messagehelper.core.exception;

public class InvalidRuleThenException extends RuntimeException {
  public InvalidRuleThenException(Throwable cause) {
    super(cause);
  }

  public InvalidRuleThenException(String message) {
    super(message);
  }
}
