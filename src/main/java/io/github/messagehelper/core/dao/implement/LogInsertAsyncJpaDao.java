package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service("LogInsertAsyncJpaDao")
public class LogInsertAsyncJpaDao implements LogInsertDao {
  private LogInsertDao sync;

  @Autowired
  public LogInsertAsyncJpaDao(@Qualifier("LogInsertJpaDao") LogInsertDao sync) {
    this.sync = sync;
  }

  @Override
  public void insert(String instance, String level, String category, String content) {
    sync.insert(instance, level, category, content);
  }

  @Override
  public void insert(PostRequestDto logDto) {
    sync.insert(logDto);
  }
}
