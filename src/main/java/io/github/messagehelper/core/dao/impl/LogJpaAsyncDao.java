package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.rpc.log.post.RequestDto;
import io.github.messagehelper.core.mysql.po.LogPo;
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
  public void insert(LogPo po) {
    sync.insert(po);
  }

  @Override
  public void insert(RequestDto logDto) {
    sync.insert(logDto);
  }
}
