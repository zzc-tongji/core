package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.dto.api.delegate.PostRequestDto;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.then.RuleThen;
import org.springframework.http.ResponseEntity;

public interface ConnectorDao {
  void refreshCache();

  ResponseEntity<String> execute(PostRequestDto request);

  void execute(RuleThen ruleThen, Log log);

  //

  GetPutPostDeleteResponseDto create(PutPostRequestDto dto);

  GetPutPostDeleteResponseDto delete(Long id);

  GetPutPostDeleteResponseDto readById(Long id);

  GetPutPostDeleteResponseDto readByInstance(String instance);

  GetAllResponseDto readAll();

  GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto);
}
