package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;

public interface LogInsertDao {
  void insert(String instance, String category, String level, String content);

  void insert(PostRequestDto logDto);
}
