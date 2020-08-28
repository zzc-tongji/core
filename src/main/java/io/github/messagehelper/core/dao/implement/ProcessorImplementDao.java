package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dao.ProcessorDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.exception.TokenInvalidException;
import io.github.messagehelper.core.processor.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProcessorImplementDao implements ProcessorDao {
  private ConfigDao configDao;
  private LogInsertDao logInsertDao;
  private RuleDao ruleDao;

  @Autowired
  public ProcessorImplementDao(
      ConfigDao configDao,
      @Qualifier("LogInsertAsyncJpaDao") LogInsertDao logInsertDao,
      RuleDao ruleDao) {
    this.configDao = configDao;
    this.logInsertDao = logInsertDao;
    this.ruleDao = ruleDao;
  }

  @Override
  public void start(PostRequestDto dto) {
    if (!configDao.load("core.rpc-token").equals(dto.getRpcToken())) {
      throw new TokenInvalidException("rpc token: not valid");
    }
    logInsertDao.insert(dto);
    ruleDao.process(Log.parse(dto));
  }
}
