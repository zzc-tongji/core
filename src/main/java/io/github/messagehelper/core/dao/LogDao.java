package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.rpc.log.post.RequestDto;
import io.github.messagehelper.core.mysql.po.LogPo;

public interface LogDao {
  void insert(LogPo po);

  void insert(RequestDto logDto);
}
