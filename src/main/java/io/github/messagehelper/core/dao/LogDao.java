package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;

public interface LogDao {
  void insert(String instance, String level, String category, String content);

  void insert(PostRequestDto logDto);
}
