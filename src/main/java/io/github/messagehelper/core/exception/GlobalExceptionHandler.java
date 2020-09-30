package io.github.messagehelper.core.exception;

import io.github.messagehelper.core.dto.HttpClientErrorResponseDto;
import io.github.messagehelper.core.dto.HttpServerErrorResponseDto;
import io.github.messagehelper.core.utils.ThrowableTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
  private final Logger logger;
  private final Boolean debug;

  public GlobalExceptionHandler(@Value("${setting.debug}") Boolean debug) {
    logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    this.debug = debug;
  }

  // JSON string not valid
  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(HttpMessageNotReadableException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  // JSON format not correct (based on different requests)
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(MethodArgumentNotValidException e) {
    develop(e);
    return new HttpClientErrorResponseDto(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  // header `token` not found (GET request only)
  @ExceptionHandler(value = MissingRequestHeaderException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(MissingRequestHeaderException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ApiTokenInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public HttpClientErrorResponseDto handle(ApiTokenInvalidException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConfigHiddenException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConfigHiddenException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConfigNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConfigNotFoundException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConfigReadOnlyException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConfigReadOnlyException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorAlreadyExistentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorAlreadyExistentException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorCategoryInvalidFormatException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorCategoryInvalidFormatException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorFetchCannotConnectException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorFetchCannotConnectException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorFetchHttpErrorException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorFetchHttpErrorException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorFetchInvalidJsonException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorFetchInvalidJsonException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorFetchInvalidUrlException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorFetchInvalidUrlException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorInstanceInvalidFormatException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorInstanceInvalidFormatException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorInstanceNumericalException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorInstanceNumericalException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorVirtualException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorVirtualException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = ConnectorNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(ConnectorNotFoundException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = IdNotNumericalException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(IdNotNumericalException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = InvalidRuleIfException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(InvalidRuleIfException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = InvalidRuleThenException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(InvalidRuleThenException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = LogContentInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(LogContentInvalidException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = PasswordAlreadySetException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
  public HttpClientErrorResponseDto handle(PasswordAlreadySetException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = PasswordInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public HttpClientErrorResponseDto handle(PasswordInvalidException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = PasswordNotSetException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
  public HttpClientErrorResponseDto handle(PasswordNotSetException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RpcTokenInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public HttpClientErrorResponseDto handle(RpcTokenInvalidException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleAlreadyExistentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleAlreadyExistentException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleEnableWithInvalidConnectorException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleEnableWithInvalidConnectorException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleThenInvalidContentTypeException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleThenInvalidContentTypeException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleThenInvalidHttpMethodException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleThenInvalidHttpMethodException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleIfInvalidContentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleIfInvalidContentException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleThenInvalidUrlException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleThenInvalidUrlException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleNameNumericalException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleNameNumericalException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuleNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpClientErrorResponseDto handle(RuleNotFoundException e) {
    develop(e);
    return new HttpClientErrorResponseDto(e);
  }

  @ExceptionHandler(value = RuntimeException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public HttpServerErrorResponseDto handle(RuntimeException e) {
    String detail = ThrowableTool.getInstance().convertToString(e, 3);
    logger.error(detail);
    return new HttpServerErrorResponseDto(detail);
  }

  private void develop(Throwable t) {
    if (debug) {
      logger.error(ThrowableTool.getInstance().convertToString(t, 3));
    }
  }
}
