package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;

public interface ProcessorDao {
  void start(PostRequestDto dto);

  void startWithWebhook(String instance, String category, String level, String content);
}
