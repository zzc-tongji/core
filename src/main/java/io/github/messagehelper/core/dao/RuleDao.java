package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.rules.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.rules.PutPostRequestDto;
import io.github.messagehelper.core.processor.log.Log;

public interface RuleDao {
  void refreshCache();

  void process(Log log);

  //

  GetPutPostDeleteResponseDto create(PutPostRequestDto dto);

  GetPutPostDeleteResponseDto delete(Long id);

  GetPutPostDeleteResponseDto readById(Long id);

  GetPutPostDeleteResponseDto readByName(String name);

  GetAllResponseDto readAll();

  GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto);
}
