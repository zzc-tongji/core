package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.PostRpcLogRequestDto;
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
    boolean existent = repository.existsById(po.getId());
    if (existent) {
      long id;
      do {
        id = IdGenerator.getInstance().generate();
      } while (repository.existsById(id));
      po.setId(id);
    }
    repository.save(po);
  }

  @Override
  public void insert(PostRpcLogRequestDto dto) {
    LogPo po = new LogPo(dto);
    insert(po);
  }
}
