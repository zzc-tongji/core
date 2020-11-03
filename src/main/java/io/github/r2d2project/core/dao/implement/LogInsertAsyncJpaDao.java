package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.LogInsertDao;
import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service("LogInsertAsyncJpaDao")
public class LogInsertAsyncJpaDao implements LogInsertDao {
  private final LogInsertDao sync;

  @Autowired
  public LogInsertAsyncJpaDao(@Autowired @Qualifier("LogInsertJpaDao") LogInsertDao sync) {
    this.sync = sync;
  }

  @Override
  public void insert(String instance, String category, String level, String content) {
    sync.insert(instance, category, level, content);
  }

  @Override
  public void insert(PostRequestDto logDto) {
    sync.insert(logDto);
  }
}
