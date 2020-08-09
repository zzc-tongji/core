package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PostPutDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PostPutRequestDto;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.rule.then.RuleThen;
import org.springframework.http.ResponseEntity;

public interface ConnectorDao {
  void refreshCache();

  PostPutDeleteResponseDto create(PostPutRequestDto dto);

  PostPutDeleteResponseDto delete(Long id);

  ResponseEntity<String> execute(RuleThen ruleThen);

  void execute(RuleThen ruleThen, Log log);

  GetResponseDto readById(Long id);

  GetResponseDto readByInstance(String instance);

  GetAllResponseDto readAll();

  PostPutDeleteResponseDto update(Long id, PostPutRequestDto dto);
}
