package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;

public interface LogInsertDao {
  void insert(String instance, String level, String category, String content);

  void insert(PostRequestDto logDto);
}
