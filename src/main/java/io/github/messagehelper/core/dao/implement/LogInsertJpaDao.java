package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.repository.LogJpaRepository;
import io.github.messagehelper.core.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LogInsertJpaDao")
public class LogInsertJpaDao implements LogInsertDao {
  private final LogJpaRepository repository;

  public LogInsertJpaDao(@Autowired LogJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void insert(String instance, String level, String category, String content) {
    // database
    LogPo po = new LogPo();
    po.setId(IdGenerator.getInstance().generate());
    po.setInstance(instance);
    po.setLevel(level);
    po.setCategory(category);
    po.setTimestampMs(System.currentTimeMillis());
    po.setContent(content);
    repository.save(po);
  }

  @Override
  public void insert(PostRequestDto dto) {
    // database
    LogPo po = new LogPo();
    po.setId(dto.getId());
    po.setInstance(dto.getInstance());
    po.setLevel(dto.getLevel());
    po.setCategory(dto.getCategory());
    po.setTimestampMs(dto.getTimestampMs());
    po.setContent(dto.getContent());
    repository.save(po);
  }
}
