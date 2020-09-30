package io.github.messagehelper.core.utils;

import io.github.messagehelper.core.mysql.po.ConfigPo;

import java.util.Map;

public class ConfigMapSingleton {
  private static final ConfigMapSingleton instance = new ConfigMapSingleton();

  private Map<String, ConfigPo> configMap;
  private Lock lock;

  public static ConfigMapSingleton getInstance() {
    return instance;
  }

  private ConfigMapSingleton() {}

  public void setConfigMap(Map<String, ConfigPo> configMap) {
    this.configMap = configMap;
  }

  public void setLock(Lock lock) {
    this.lock = lock;
  }

  public String load(String key) {
    if (configMap == null || lock == null) {
      return "";
    }
    // CHECK
    while (lock.isWriteLocked()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    // LOCK
    lock.readIncrease();
    // DO
    ConfigPo po = configMap.get(key);
    // UNLOCK
    lock.readDecrease();
    //
    if (po == null) {
      return "";
    }
    String value = po.getValue();
    return value == null ? "" : value;
  }
}
