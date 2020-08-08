package io.github.messagehelper.core.dao;

import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.rule.then.RuleThen;

public interface ConnectorDao {
  void refreshCache();

  void execute(RuleThen ruleThen);

  void execute(RuleThen ruleThen, Log log);
}
