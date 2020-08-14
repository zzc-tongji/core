package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;

public interface ProcessorDao {
  void start(PostRequestDto dto);
}
