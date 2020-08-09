package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RpcController {
  private static final String PREFIX = "/rpc";

  private ConfigDao configDao;
  private LogDao logDao;
  private RuleDao ruleDao;

  @Autowired
  public RpcController(
      ConfigDao configDao, @Qualifier("LogJpaAsyncDao") LogDao logDao, RuleDao ruleDao) {
    this.configDao = configDao;
    this.logDao = logDao;
    this.ruleDao = ruleDao;
  }

  @PostMapping(value = PREFIX + "/log")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void logPost(@RequestBody @Validated PostRequestDto dto) {
    dto.authenticate(configDao.load("core.rpc.token"));
    Log log = new Log(dto);
    logDao.insert(dto);
    ruleDao.process(log);
  }
}
