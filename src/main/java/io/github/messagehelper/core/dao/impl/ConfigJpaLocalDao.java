package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.api.configs.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.configs.GetPutResponseDto;
import io.github.messagehelper.core.dto.api.configs.PutRequestDto;
import io.github.messagehelper.core.exception.ConfigHiddenException;
import io.github.messagehelper.core.exception.ConfigNotFoundException;
import io.github.messagehelper.core.mysql.po.ConfigPo;
import io.github.messagehelper.core.mysql.repository.ConfigJpaRepository;
import io.github.messagehelper.core.utils.ConfigMapSingleton;
import io.github.messagehelper.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigJpaLocalDao implements ConfigDao {
  private ConfigJpaRepository repository;
  private Map<String, ConfigPo> configMap;
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
          throw new RuntimeException(e);
        }
      }
      // LOCK
      lock.writeIncrease();
      // DO
      configMap.clear();
      List<ConfigPo> data = repository.findAll();
      for (ConfigPo po : data) {
        configMap.put(po.getKey(), po);
      }
      // UNLOCK
      lock.writeDecrease();
      //
    }
  }

  @Override
  public String load(String key) {
    ConfigPo po = find(key);
    if (po == null) {
      return "";
    }
    String value = po.getValue();
    if (value == null || value.length() <= 0) {
      return "";
    }
    return value;
  }

  @Override
  public void save(String key, String value) {
    repository.save(new ConfigPo(key, value));
    refreshCache();
  }

  @Override
  public GetPutResponseDto read(String key) {
    ConfigPo po = readUpdateHelper(key);
    return new GetPutResponseDto(po);
  }

  @Override
  public GetAllResponseDto readAll() {
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
    List<ConfigPo> poList = new ArrayList<>();
    for (ConfigPo po : configMap.values()) {
      if (po.getKey().equals("core.backend.password")) {
        continue;
      }
      if (po.getKey().equals("core.backend.salt")) {
        continue;
      }
      poList.add(po);
    }
    // UNLOCK
    lock.readDecrease();
    //
    return new GetAllResponseDto(poList);
  }

  @Override
  public GetPutResponseDto update(String key, PutRequestDto dto) {
    ConfigPo po = readUpdateHelper(key);
    po.setValue(dto.getValue());
    repository.save(po);
    refreshCache();
    return new GetPutResponseDto(po);
  }

  private ConfigPo readUpdateHelper(String key) {
    if (key.equals("core.backend.password") || key.equals("core.backend.salt")) {
      throw new ConfigHiddenException(String.format("key `%s`: hidden", key));
    }
    ConfigPo po = find(key);
    if (po == null) {
      throw new ConfigNotFoundException(String.format("key `%s`: not found", key));
    }
    return po;
  }

  private ConfigPo find(String key) {
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
    return po;
  }
}
