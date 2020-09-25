package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.login.PostResponseDto;

public interface ApiTokenDao {
  void refreshCache();

  void authenticate(String[] tokenList);

  void revoke(String token);

  //

  PostResponseDto login(io.github.messagehelper.core.dto.api.login.PostRequestDto dto);

  PostResponseDto loginPermanent(io.github.messagehelper.core.dto.api.login.PostRequestDto dto);

  void register(io.github.messagehelper.core.dto.api.register.PostRequestDto dto);
}
