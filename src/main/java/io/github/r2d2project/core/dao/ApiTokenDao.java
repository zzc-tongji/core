package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.api.login.PostRequestDto;
import io.github.r2d2project.core.dto.api.login.PostResponseDto;

public interface ApiTokenDao {
  void refreshCache();

  void authenticate(String[] tokenList);

  void revoke(String token);

  //

  PostResponseDto login(PostRequestDto dto);

  PostResponseDto loginPermanent(PostRequestDto dto);

  void register(io.github.r2d2project.core.dto.api.register.PostRequestDto dto);
}
