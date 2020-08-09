package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.configs.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.configs.GetPutResponseDto;
import io.github.messagehelper.core.dto.api.configs.PutRequestDto;

public interface ConfigDao {
  void refreshCache();

  String load(String key);

  GetPutResponseDto read(String key);

  GetAllResponseDto readAll();

  GetPutResponseDto update(String key, PutRequestDto dto);
}
