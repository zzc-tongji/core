package io.github.messagehelper.core.dao;

public interface ProcessorDao {
  void start(io.github.messagehelper.core.dto.rpc.log.PostRequestDto dto);

  void startWithWebhook(io.github.messagehelper.core.dto.api.webhooks.PostRequestDto dto);
}
