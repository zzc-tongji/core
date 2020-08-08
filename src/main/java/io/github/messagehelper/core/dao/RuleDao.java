package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.log.Log;

public interface RuleDao {
  void refreshCache();

  void process(Log log);
}
