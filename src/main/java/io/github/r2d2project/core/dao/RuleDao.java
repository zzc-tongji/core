package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.api.rules.GetAllResponseDto;
import io.github.r2d2project.core.dto.api.rules.GetPutPostDeleteResponseDto;
import io.github.r2d2project.core.dto.api.rules.PutPostRequestDto;
import io.github.r2d2project.core.processor.log.Log;

public interface RuleDao {
  void refreshCache();

  void process(Log log);

  void disableRuleByConnectorId(Long connectorId);

  void fix();

  void updateCoreInstance(String before, String after);

  //

  GetPutPostDeleteResponseDto create(PutPostRequestDto dto);

  GetPutPostDeleteResponseDto delete(Long id);

  GetPutPostDeleteResponseDto readById(Long id);

  GetPutPostDeleteResponseDto readByName(String name);

  GetAllResponseDto readAll();

  GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto);
}
