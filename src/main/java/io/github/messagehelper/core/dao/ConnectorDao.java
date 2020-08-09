package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.rule.then.RuleThen;
import org.springframework.http.ResponseEntity;

public interface ConnectorDao {
  void refreshCache();

  GetPutPostDeleteResponseDto create(PutPostRequestDto dto);

  GetPutPostDeleteResponseDto delete(Long id);

  ResponseEntity<String> execute(RuleThen ruleThen);

  void execute(RuleThen ruleThen, Log log);

  GetPutPostDeleteResponseDto readById(Long id);

  GetPutPostDeleteResponseDto readByInstance(String instance);

  GetAllResponseDto readAll();

  GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto);
}
