package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.ConfigPo;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.repository.ConfigJpaRepository;
import io.github.messagehelper.core.utils.ConfigMapSingleton;
import io.github.messagehelper.core.utils.ErrorJsonGenerator;
import io.github.messagehelper.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigJpaLocalDao implements ConfigDao {
  private ConfigJpaRepository repository;
  private Map<String, String> configMap;
  private LogDao logDao;
  private final Lock lock;

  public ConfigJpaLocalDao(
      @Autowired ConfigJpaRepository repository,
      @Autowired @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.repository = repository;
    this.logDao = logDao;
    configMap = new HashMap<>();
    lock = new Lock();
    //
    refreshCache();
    ConfigMapSingleton.getInstance().setLock(lock);
    ConfigMapSingleton.getInstance().setConfigMap(configMap);
  }

  @Override
  public void refreshCache() {
    // CHECK
    synchronized (lock) {
      // CHECK
      while (lock.isReadLocked()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          logException(e);
          return;
        }
      }
      // LOCK
      lock.writeIncrease();
      // DO
      configMap.clear();
      List<ConfigPo> data = repository.findAll();
      for (ConfigPo item : data) {
        configMap.put(item.getKey(), item.getValue());
      }
      // UNLOCK
      lock.writeDecrease();
      //
    }
  }

  @Override
  public String load(String key) {
    // CHECK
    while (lock.isWriteLocked()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logException(e);
        logEmptyValue(key);
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
    if (value.length() == 0) {
      logEmptyValue(key);
    }
    return value;
  }

  @Override
  public void save(String key, String value) {
    // CHECK
    synchronized (lock) {
      // CHECK
      while (lock.isReadLocked()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          logException(e);
          return;
        }
      }
      // LOCK
      lock.writeIncrease();
      // DO
      configMap.put(key, value);
      repository.save(new ConfigPo(key, value));
      // UNLOCK
      lock.writeDecrease();
    }
  }

  private void logException(Exception e) {
    logDao.insert(
        new LogPo(
            load("processor.instance"),
            Constant.LOG_ERR,
            "process.dao.impl.config-jpa-local-dao.exception",
            ErrorJsonGenerator.getInstance().generate(e)));
  }

  private void logEmptyValue(String key) {
    logDao.insert(
        new LogPo(
            load("processor.instance"),
            Constant.LOG_ERR,
            "process.dao.impl.config-jpa-local-dao.empty-value",
            ErrorJsonGenerator.getInstance()
                .generate(String.format("key \"%s\": empty value (\"\")", key), "", "")));
  }
}
