package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dto.api.configs.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.configs.GetPutResponseDto;
import io.github.messagehelper.core.dto.api.configs.Item;
import io.github.messagehelper.core.dto.api.configs.PutRequestDto;
import io.github.messagehelper.core.exception.ConfigHiddenException;
import io.github.messagehelper.core.exception.ConfigNotFoundException;
import io.github.messagehelper.core.exception.ConfigReadOnlyException;
import io.github.messagehelper.core.mysql.po.ConfigPo;
import io.github.messagehelper.core.mysql.repository.ConfigJpaRepository;
import io.github.messagehelper.core.utils.ConfigMapSingleton;
import io.github.messagehelper.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigJpaLocalDao implements ConfigDao {
  private ConfigJpaRepository repository;
  private Map<String, ConfigPo> configMap;
  private final Lock lock;

  public ConfigJpaLocalDao(@Autowired ConfigJpaRepository repository) {
    this.repository = repository;
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
    // cache
    ConfigPo po = find(key);
    if (po == null) {
      return "";
    }
    String value = po.getValue();
    if (value == null) {
      return "";
    }
    return value;
  }

  @Override
  public void save(String key, String value) {
    // database
    ConfigPo po = new ConfigPo();
    po.setKey(key);
    po.setValue(value);
    repository.save(po);
    refreshCache();
  }

  @Override
  public GetPutResponseDto read(String key) {
    // cache
    ConfigPo po = readUpdateHelper(key, false);
    // response
    GetPutResponseDto responseDto = new GetPutResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

  @Override
  public GetAllResponseDto readAll() {
    // cache
    Collection<ConfigPo> collection = findAll();
    // response
    GetAllResponseDto responseDto = new GetAllResponseDto();
    Collection<Item> data = responseDto.getData();
    Item item;
    String key;
    for (ConfigPo po : collection) {
      item = new Item();
      key = po.getKey();
      if (key.equals("core.api-password-hash") || key.equals("core.api-password-salt")) {
        continue;
      }
      item.setKey(key);
      item.setValue(po.getValue());
      data.add(item);
    }
    return responseDto;
  }

  @Override
  public GetPutResponseDto update(String key, PutRequestDto dto) {
    // cache
    readUpdateHelper(key, true);
    // database
    ConfigPo po = new ConfigPo();
    po.setKey(key);
    po.setValue(dto.getValue());
    repository.save(po);
    refreshCache();
    // response
    GetPutResponseDto responseDto = new GetPutResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
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

  public Collection<ConfigPo> findAll() {
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
    Collection<ConfigPo> poCollection = configMap.values();
    // UNLOCK
    lock.readDecrease();
    //
    return poCollection;
  }

  private void poToResponseDto(ConfigPo po, GetPutResponseDto dto) {
    Item data = dto.getData();
    data.setKey(po.getKey());
    data.setValue(po.getValue());
  }

  private ConfigPo readUpdateHelper(String key, boolean updateAsTrue) {
    if (key.equals("core.api-password-hash") || key.equals("core.api-password-salt")) {
      throw new ConfigHiddenException(String.format("key [%s]: hidden", key));
    }
    if (updateAsTrue) {
      if (key.equals("core.instance") || key.equals("core.rpc-token")) {
        throw new ConfigReadOnlyException(String.format("key [%s]: read only", key));
      }
    }
    ConfigPo po = find(key);
    if (po == null) {
      throw new ConfigNotFoundException(String.format("key [%s]: not found", key));
    }
    return po;
  }
}
