package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.api.connectors.GetAllResponseDto;
import io.github.r2d2project.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.r2d2project.core.dto.api.connectors.PutPostRequestDto;
import io.github.r2d2project.core.processor.log.Log;
import io.github.r2d2project.core.processor.rule.Rule;
import org.springframework.http.ResponseEntity;

public interface ConnectorDao {
  void refreshCache();

  void executeRule(Rule rule, Log log);

  ResponseEntity<String> executeDelegate(Long id, String path, String contentType, String body);

  ResponseEntity<String> executeDelegate(
      String instance, String path, String contentType, String body);

  boolean notExistent(Long id);

  String getUrlById(Long id);

  //

  GetPutPostDeleteResponseDto create(PutPostRequestDto dto);

  GetPutPostDeleteResponseDto delete(Long id);

  GetPutPostDeleteResponseDto readById(Long id);

  GetPutPostDeleteResponseDto readByInstance(String instance);

  GetAllResponseDto readAll();

  GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto);
}
