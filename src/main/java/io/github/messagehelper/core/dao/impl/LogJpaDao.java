package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.repository.LogJpaRepository;
import io.github.messagehelper.core.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LogJpaDao")
public class LogJpaDao implements LogDao {
  private LogJpaRepository repository;

  @Autowired
  public LogJpaDao(LogJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public void insert(LogPo po) {
    Long id = po.getId();
    if (id < 0 && repository.existsById(id)) {
      do {
        id = IdGenerator.getInstance().generate();
      } while (id < 0 && repository.existsById(id));
      po.setId(id);
    }
    repository.save(po);
  }

  @Override
  public void insert(PostRequestDto dto) {
    LogPo po = new LogPo(dto);
    insert(po);
  }
}
