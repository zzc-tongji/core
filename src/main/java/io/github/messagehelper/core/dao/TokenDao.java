package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.login.PostResponseDto;

public interface TokenDao {
  void refreshCache();

  void authenticate(String token);

  //

  PostResponseDto login(io.github.messagehelper.core.dto.api.login.PostRequestDto dto);

  void register(io.github.messagehelper.core.dto.api.register.PostRequestDto dto);
}
