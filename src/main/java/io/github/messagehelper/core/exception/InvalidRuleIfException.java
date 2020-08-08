package io.github.messagehelper.core.exception;

public class InvalidRuleIfException extends RuntimeException {
  public InvalidRuleIfException(Throwable cause) {
    super(cause);
  }

  public InvalidRuleIfException(String message) {
    super(message);
  }
}
