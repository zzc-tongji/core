package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.ConfigDao;
import io.github.r2d2project.core.dao.RuleDao;
import io.github.r2d2project.core.dto.api.configs.GetAllResponseDto;
import io.github.r2d2project.core.dto.api.configs.GetPutResponseDto;
import io.github.r2d2project.core.dto.api.configs.Item;
import io.github.r2d2project.core.dto.api.configs.PutRequestDto;
import io.github.r2d2project.core.exception.ConfigCoreInstanceException;
import io.github.r2d2project.core.exception.ConfigNotFoundException;
import io.github.r2d2project.core.storage.po.ConfigPo;
import io.github.r2d2project.core.storage.repository.ConfigJpaRepository;
import io.github.r2d2project.core.utils.ConfigMapSingleton;
import io.github.r2d2project.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigJpaLocalDao implements ConfigDao {
  private final ConfigJpaRepository repository;
  private final RuleDao ruleDao;
  private final Map<String, ConfigPo> configMap;
  private final Lock lock;

  public ConfigJpaLocalDao(
      @Autowired ConfigJpaRepository repository, @Autowired @Lazy RuleDao ruleDao) {
    this.repository = repository;
    this.ruleDao = ruleDao;
    configMap = new HashMap<>();
    lock = new Lock();
    //
    refreshCache();
    initialize();
    ConfigMapSingleton.getInstance().setLock(lock);
    ConfigMapSingleton.getInstance().setConfigMap(configMap);
  }

  @SuppressWarnings("BusyWait")
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
    ConfigPo po = readUpdateHelper(key);
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
    ConfigPo po = readUpdateHelper(key);
    String value = po.getValue();
    String updatedValue = dto.getValue();
    // update corresponding rules
    if (key.equals("core.instance") && !value.equals(updatedValue)) {
      ruleDao.updateCoreInstance(value, updatedValue);
    }
    // database
    ConfigPo updatedPo = new ConfigPo();
    updatedPo.setKey(key);
    updatedPo.setValue(updatedValue);
    repository.save(updatedPo);
    refreshCache();
    // response
    GetPutResponseDto responseDto = new GetPutResponseDto();
    poToResponseDto(updatedPo, responseDto);
    return responseDto;
  }

  private void initialize() {
    if (load("core.api-document").length() <= 0) {
      save(
          "core.api-document",
          "https://editor.swagger.io/?url=https%3a%2f%2fraw.githubusercontent.com%2fmessage-helper%2fcore%2fmaster%2fdoc%2fapi.yaml");
    }
    if (load("core.frontend-config").length() <= 0) {
      save("core.frontend-config", "{}");
    }
    if (load("core.id-generator").length() <= 0) {
      save("core.id-generator", "https://id.zzc.icu/");
    }
    if (load("core.instance").length() <= 0) {
      save("core.instance", "core");
    }
    if (load("core.rpc-token").length() <= 0) {
      save("core.rpc-token", "core8r3ufurm9tqomosuul0s5s9ts6ko8g85pijxudbvpm2jtb2w01od1z69h5vi");
    }
  }

  @SuppressWarnings("BusyWait")
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

  @SuppressWarnings("BusyWait")
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

  private ConfigPo readUpdateHelper(String key) {
    if (key.equals("core.api-password-hash") || key.equals("core.api-password-salt")) {
      throw new ConfigCoreInstanceException(String.format("key [%s]: hidden", key));
    }
    ConfigPo po = find(key);
    if (po == null) {
      throw new ConfigNotFoundException(String.format("key [%s]: not found", key));
    }
    return po;
  }
}
