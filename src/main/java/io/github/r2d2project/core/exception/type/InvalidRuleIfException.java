package io.github.r2d2project.core.exception.type;

public class InvalidRuleIfException extends RuntimeException {
  public InvalidRuleIfException(Throwable cause) {
    super(cause);
  }

  public InvalidRuleIfException(String message) {
    super(message);
  }
}
