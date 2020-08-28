package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.dto.api.logs.GetRequest;
import io.github.messagehelper.core.dto.api.logs.GetResponse;

public interface LogReadDao {
  GetResponse readAdvance(GetRequest request);
}
