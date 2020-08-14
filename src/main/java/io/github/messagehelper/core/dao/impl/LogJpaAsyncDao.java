package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service("LogJpaAsyncDao")
public class LogJpaAsyncDao implements LogDao {
  private LogDao sync;

  @Autowired
  public LogJpaAsyncDao(@Qualifier("LogJpaDao") LogDao sync) {
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
