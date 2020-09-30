package io.github.messagehelper.core.dao.implement;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dao.ProcessorDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.utils.IdGenerator;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;
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
  public void start(io.github.messagehelper.core.dto.rpc.log.PostRequestDto dto) {
    logInsertDao.insert(dto);
    ruleDao.process(Log.parse(dto));
  }

  @Override
  public void startWithWebhook(io.github.messagehelper.core.dto.api.webhooks.PostRequestDto dto) {
    // convert
    io.github.messagehelper.core.dto.rpc.log.PostRequestDto logDto =
        new io.github.messagehelper.core.dto.rpc.log.PostRequestDto();
    logDto.setId(IdGenerator.getInstance().generate());
    logDto.setInstance(configDao.load("core.instance"));
    logDto.setCategory("webhook-connector.receive");
    logDto.setLevel(Constant.LOG_LEVEL_INFO);
    logDto.setTimestampMs(System.currentTimeMillis());
    ObjectNode objectNode =
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("value1", dto.getValue1())
            .put("value2", dto.getValue2())
            .put("value3", dto.getValue3());
    logDto.setContent(objectNode.toString());
    logDto.setRpcToken("");
    // start
    start(logDto);
  }

  @Override
  public void startWithWebhook(String instance, String category, String level, String content) {
    // convert
    io.github.messagehelper.core.dto.rpc.log.PostRequestDto logDto =
        new io.github.messagehelper.core.dto.rpc.log.PostRequestDto();
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
