package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.api.configs.GetAllResponseDto;
import io.github.r2d2project.core.dto.api.configs.GetPutResponseDto;
import io.github.r2d2project.core.dto.api.configs.PutRequestDto;

public interface ConfigDao {
  void refreshCache();

  String load(String key);

  void save(String key, String value);

  //

  GetPutResponseDto read(String key);

  GetAllResponseDto readAll();

  GetPutResponseDto update(String key, PutRequestDto dto);
}
