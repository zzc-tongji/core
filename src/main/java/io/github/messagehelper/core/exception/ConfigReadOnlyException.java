package io.github.messagehelper.core.exception;

public class ConfigReadOnlyException extends RuntimeException {
  public ConfigReadOnlyException(String message) {
    super(message);
  }
}
