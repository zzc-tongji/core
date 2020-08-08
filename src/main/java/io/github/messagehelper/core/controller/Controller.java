package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.dto.rpc.log.post.RequestDto;
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
public class Controller {
  private ConfigDao configDao;
  private LogDao logDao;
  private RuleDao ruleDao;

  @Autowired
  public Controller(
      ConfigDao configDao, @Qualifier("LogJpaAsyncDao") LogDao logDao, RuleDao ruleDao) {
    this.configDao = configDao;
    this.logDao = logDao;
    this.ruleDao = ruleDao;
  }

  @PostMapping(value = "/rpc/log")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void postRpcLog(@RequestBody @Validated RequestDto dto) {
    dto.authenticate(configDao.load("processor.token"));
    Log log = new Log(dto);
    logDao.insert(dto);
    ruleDao.process(log);
  }
}
