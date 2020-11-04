package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.ConfigDao;
import io.github.r2d2project.core.dao.LogInsertDao;
import io.github.r2d2project.core.dao.ProcessorDao;
import io.github.r2d2project.core.dao.RuleDao;
import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;
import io.github.r2d2project.core.processor.log.Log;
import io.github.r2d2project.core.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProcessorImplementDao implements ProcessorDao {
  private final ConfigDao configDao;
  private final LogInsertDao logInsertDao;
  private final RuleDao ruleDao;

  public ProcessorImplementDao(
      @Autowired ConfigDao configDao,
      @Qualifier("LogInsertAsyncJpaDao") LogInsertDao logInsertDao,
      @Autowired RuleDao ruleDao) {
    this.configDao = configDao;
    this.logInsertDao = logInsertDao;
    this.ruleDao = ruleDao;
  }

  @Override
  public void start(PostRequestDto dto) {
    logInsertDao.insert(dto);
    ruleDao.process(Log.parse(dto));
  }

  @Override
  public void startWithWebhook(String instance, String category, String level, String content) {
    // convert
    PostRequestDto logDto = new PostRequestDto();
    logDto.setId(IdGenerator.getInstance().generate());
    logDto.setInstance(instance);
    logDto.setCategory(category);
    logDto.setLevel(level);
    logDto.setTimestampMs(System.currentTimeMillis());
    logDto.setContent(content);
    logDto.setRpcToken("");
    // start
    start(logDto);
  }
}
