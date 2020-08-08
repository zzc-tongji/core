package io.github.messagehelper.core.dao;

public interface ConfigDao {
  void refreshCache();

  String load(String key);

  void save(String key, String value);
}
