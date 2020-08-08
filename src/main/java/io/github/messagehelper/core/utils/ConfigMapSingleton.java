package io.github.messagehelper.core.utils;

import java.util.Map;

public class ConfigMapSingleton {
  private static ConfigMapSingleton instance = new ConfigMapSingleton();

  private Map<String, String> configMap;
  private Lock lock;

  public static ConfigMapSingleton getInstance() {
    return instance;
  }

  private ConfigMapSingleton() {}

  public void setConfigMap(Map<String, String> configMap) {
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
        return "";
      }
    }
    // LOCK
    lock.readIncrease();
    // DO
    String value = configMap.get(key);
    // UNLOCK
    lock.readDecrease();
    //
    if (value == null) {
      value = "";
    }
    return value;
  }
}
