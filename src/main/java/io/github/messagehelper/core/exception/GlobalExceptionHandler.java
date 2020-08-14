package io.github.messagehelper.core.exception;

import io.github.messagehelper.core.dto.ExceptionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // JSON string not valid
  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(HttpMessageNotReadableException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  // JSON format not correct (based on different requests)
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(MethodArgumentNotValidException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  // header `token` not found (GET request only)
  @ExceptionHandler(value = MissingRequestHeaderException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(MissingRequestHeaderException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConfigHiddenException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConfigHiddenException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConfigNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConfigNotFoundException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConnectorAlreadyExistentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConnectorAlreadyExistentException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConnectorNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConnectorNotFoundException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = IdNotNumericalException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(IdNotNumericalException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = InvalidRuleIfException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(InvalidRuleIfException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = InvalidRuleThenException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(InvalidRuleThenException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = LogContentInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(LogContentInvalidException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = PasswordAlreadySetException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
  public ExceptionResponseDto handle(PasswordAlreadySetException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = PasswordInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ExceptionResponseDto handle(PasswordInvalidException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = PasswordNotSetException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
  public ExceptionResponseDto handle(PasswordNotSetException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = RuleAlreadyExistentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(RuleAlreadyExistentException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = RuleNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(RuleNotFoundException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = TokenInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ExceptionResponseDto handle(TokenInvalidException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = RuntimeException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ExceptionResponseDto handle(RuntimeException e) {
    logger.info(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }
}
