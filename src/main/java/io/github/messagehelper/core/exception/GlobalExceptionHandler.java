package io.github.messagehelper.core.exception;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.rpc.log.post.ResponseDto;
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
  public ResponseDto handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    logException(e);
    return new ResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    logException(e);
    return new ResponseDto(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  @ExceptionHandler(value = InvalidContentException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDto handleInvalidContentException(InvalidContentException e) {
    logException(e);
    return new ResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = InvalidTokenException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseDto handleAuthFailureException(InvalidTokenException e) {
    logException(e);
    return new ResponseDto(e.getMessage());
  }

  @ExceptionHandler(value = RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDto handleRuntimeException(RuntimeException e) {
    logException(e);
    return new ResponseDto(e.toString());
  }

  private void logException(Exception e) {
    logDao.insert(
        new LogPo(
            configDao.load("core.instance"),
            Constant.LOG_ERR,
            "core.exception.global-exception-handler",
            ErrorJsonGenerator.getInstance().generate(e)));
  }
}
