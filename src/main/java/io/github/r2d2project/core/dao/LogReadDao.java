package io.github.r2d2project.core.dao;

import io.github.r2d2project.core.dto.api.logs.GetRequest;
import io.github.r2d2project.core.dto.api.logs.GetResponse;

public interface LogReadDao {
  GetResponse readAdvance(GetRequest request);
}
