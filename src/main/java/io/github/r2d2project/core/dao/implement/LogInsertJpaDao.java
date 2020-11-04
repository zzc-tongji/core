package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.LogInsertDao;
import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;
import io.github.r2d2project.core.persistence.po.LogPo;
import io.github.r2d2project.core.persistence.repository.LogJpaRepository;
import io.github.r2d2project.core.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LogInsertJpaDao")
public class LogInsertJpaDao implements LogInsertDao {
  private final LogJpaRepository repository;

  public LogInsertJpaDao(@Autowired LogJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void insert(String instance, String category, String level, String content) {
    // database
    LogPo po = new LogPo();
    po.setId(IdGenerator.getInstance().generate());
    po.setLevel(level);
    po.setInstance(instance);
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
    po.setCategory(dto.getCategory());
    po.setLevel(dto.getLevel());
    po.setTimestampMs(dto.getTimestampMs());
    po.setContent(dto.getContent());
    repository.save(po);
  }
}
