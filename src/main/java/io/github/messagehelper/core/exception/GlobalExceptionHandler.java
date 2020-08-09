package io.github.messagehelper.core.exception;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.ExceptionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
  ConfigDao configDao;
  LogDao logDao;

  @Autowired
  public GlobalExceptionHandler(ConfigDao configDao, @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.configDao = configDao;
    this.logDao = logDao;
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(MethodArgumentNotValidException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  @ExceptionHandler(value = MissingRequestHeaderException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(MissingRequestHeaderException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConnectorAlreadyExistentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConnectorAlreadyExistentException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = ConnectorNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(ConnectorNotFoundException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = LogContentInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponseDto handle(LogContentInvalidException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = TokenInvalidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ExceptionResponseDto handle(TokenInvalidException e) {
    logger.error(e.toString());
    return new ExceptionResponseDto(e.getMessage());
  }
}
