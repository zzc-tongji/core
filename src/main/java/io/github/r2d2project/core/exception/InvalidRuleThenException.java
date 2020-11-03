package io.github.r2d2project.core.exception;

public class InvalidRuleThenException extends RuntimeException {
  public InvalidRuleThenException(Throwable cause) {
    super(cause);
  }

  public InvalidRuleThenException(String message) {
    super(message);
  }
}
