package io.github.messagehelper.core.exception;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.PostRpcLogResponseDto;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.utils.ErrorJsonGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
  ConfigDao configDao;
  LogDao logDao;

  @Autowired
  public GlobalExceptionHandler(ConfigDao configDao, @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.configDao = configDao;
    this.logDao = logDao;
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public PostRpcLogResponseDto handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    logException(e);
    return new PostRpcLogResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public PostRpcLogResponseDto handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    logException(e);
    return new PostRpcLogResponseDto(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  @ExceptionHandler(value = InvalidContentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public PostRpcLogResponseDto handleInvalidContentException(InvalidContentException e) {
    logException(e);
    return new PostRpcLogResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = AuthFailureException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public PostRpcLogResponseDto handleAuthFailureException(AuthFailureException e) {
    logException(e);
    return new PostRpcLogResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public PostRpcLogResponseDto handleRuntimeException(RuntimeException e) {
    logException(e);
    return new PostRpcLogResponseDto(e.getMessage());
  }

  private void logException(Exception e) {
    logDao.insert(
        new LogPo(
            configDao.load("processor.instance"),
            Constant.LOG_ERR,
            "processor.exception.global-exception-handler",
            ErrorJsonGenerator.getInstance().generate(e)));
  }
}
