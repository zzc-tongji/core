package io.github.r2d2project.core.exception;

public class ConfigReadOnlyException extends RuntimeException {
  public ConfigReadOnlyException(String message) {
    super(message);
  }
}
