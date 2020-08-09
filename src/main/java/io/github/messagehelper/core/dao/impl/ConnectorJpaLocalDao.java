package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.exception.ConnectorAlreadyExistentException;
import io.github.messagehelper.core.exception.ConnectorNotFoundException;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.ConnectorPo;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.repository.ConnectorJpaRepository;
import io.github.messagehelper.core.rule.then.BodyTemplate;
import io.github.messagehelper.core.rule.then.RuleThen;
import io.github.messagehelper.core.utils.ErrorJsonGenerator;
import io.github.messagehelper.core.utils.HttpClientSingleton;
import io.github.messagehelper.core.utils.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorJpaLocalDao implements ConnectorDao {
  private final Logger logger = LoggerFactory.getLogger(ConnectorJpaLocalDao.class);

  private ConnectorJpaRepository repository;
  private ConfigDao configDao;
  private LogDao logDao;
  private Map<String, ConnectorPo> connectorMap;
  private final Lock lock;

  public ConnectorJpaLocalDao(
      @Autowired ConnectorJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.repository = repository;
    this.configDao = configDao;
    this.logDao = logDao;
    connectorMap = new HashMap<>();
    lock = new Lock();
    //
    refreshCache();
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
      connectorMap.clear();
      List<ConnectorPo> data = repository.findAll();
      for (ConnectorPo item : data) {
        connectorMap.put(item.getInstance(), item);
      }
      // UNLOCK
      lock.writeDecrease();
      //
    }
  }

  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    if (find(dto.getInstance()) != null) {
      throw new ConnectorAlreadyExistentException(
          String.format("connector with instance [%s]: already existent", dto.getInstance()));
    }
    ConnectorPo po = new ConnectorPo(dto);
    repository.save(po);
    refreshCache();
    return new GetPutPostDeleteResponseDto(po);
  }

  @Override
  public GetPutPostDeleteResponseDto delete(Long id) {
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    repository.deleteById(id);
    refreshCache();
    return new GetPutPostDeleteResponseDto(po);
  }

  @Override
  public ResponseEntity<String> execute(RuleThen ruleThen) {
    return executeHelper(ruleThen, ruleThen.getBodyTemplate());
  }

  @Override
  public void execute(RuleThen ruleThen, Log log) {
    executeHelper(ruleThen, BodyTemplate.parse(ruleThen.getBodyTemplate(), log));
  }

  @Override
  public GetPutPostDeleteResponseDto readById(Long id) {
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    return new GetPutPostDeleteResponseDto(po);
  }

  @Override
  public GetPutPostDeleteResponseDto readByInstance(String instance) {
    ConnectorPo po = find(instance);
    if (po == null) {
      throw new ConnectorNotFoundException(
          String.format("connector with instance [%s]: not found", instance));
    }
    return new GetPutPostDeleteResponseDto(po);
  }

  @Override
  public GetAllResponseDto readAll() {
    return new GetAllResponseDto(findAll());
  }

  @Override
  public GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto) {
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    if (!po.getInstance().equals(dto.getInstance())) {
      po = find(dto.getInstance());
      if (po != null) {
        throw new ConnectorAlreadyExistentException(
            String.format("connector with instance [%s]: already existent", dto.getInstance()));
      }
    }
    ConnectorPo updatedPo = new ConnectorPo(dto, id);
    repository.save(updatedPo);
    refreshCache();
    return new GetPutPostDeleteResponseDto(updatedPo);
  }

  private ConnectorPo find(Long id) {
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
    ConnectorPo po = null;
    for (ConnectorPo item : connectorMap.values()) {
      if (item.getId().equals(id)) {
        po = item;
        break;
      }
    }
    // UNLOCK
    lock.readDecrease();
    //
    return po;
  }

  private ConnectorPo find(String instance) {
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
    ConnectorPo po = connectorMap.get(instance);
    // UNLOCK
    lock.readDecrease();
    //
    return po;
  }

  private Collection<ConnectorPo> findAll() {
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
    Collection<ConnectorPo> poCollection = connectorMap.values();
    // UNLOCK
    lock.readDecrease();
    //
    return poCollection;
  }

  private ResponseEntity<String> executeHelper(RuleThen ruleThen, String requestBody) {
    String instance = ruleThen.getInstance();
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
    ConnectorPo po = connectorMap.get(instance);
    // UNLOCK
    lock.readDecrease();
    //
    if (po == null) {
      String errorMessage = String.format("connector with instance [%s]: not found", instance);
      logDao.insert(
          new LogPo(
              configDao.load("core.instance"),
              Constant.LOG_ERR,
              "core.dao.impl.config-jpa-dao.exception",
              ErrorJsonGenerator.getInstance().generate(errorMessage, "", "")));
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(String.format("{\"information\":%s}", errorMessage));
    }
    HttpRequest request;
    String url = po.getUrl() + ruleThen.getPath();
    String requestBodyWithToken = insertToken(requestBody, po.getToken());
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(url))
              .headers("content-type", "application/json;charset=utf-8")
              .POST(HttpRequest.BodyPublishers.ofString(requestBodyWithToken))
              .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      String errorMessage =
          String.format(
              "connector with instance [%s] and url [%s]: fail to connect", instance, url);
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(String.format("{\"error\":%s}", errorMessage));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    int statusCode = response.statusCode();
    if (statusCode <= 199 || statusCode >= 300) {
      logDao.insert(
          new LogPo(
              configDao.load("core.instance"),
              Constant.LOG_ERR,
              "core.dao.impl.config-jpa-dao.exception",
              ErrorJsonGenerator.getInstance()
                  .generate(
                      String.format(
                          "fetch: [%s] || connector: instance [%s] as category [%s] || response: [%d] => [%s] || request: [%s]",
                          url,
                          instance,
                          po.getCategory(),
                          statusCode,
                          response.body(),
                          requestBodyWithToken),
                      "",
                      "")));
    }
    String responseBody = response.body();
    if (responseBody.length() <= 0) {
      return ResponseEntity.status(statusCode).body(responseBody);
    }
    return ResponseEntity.status(statusCode)
        .header("content-type", response.headers().allValues("content-type").get(0))
        .body(response.body());
  }

  private String insertToken(String requestBody, String token) {
    if (requestBody.contains("\"token\"")) {
      return requestBody;
    }
    StringBuilder builder = new StringBuilder(requestBody);
    builder.insert(builder.length() - 1, String.format(",\"token\":\"%s\"", token));
    return builder.toString();
  }

  private void logException(Exception e) {
    logDao.insert(
        new LogPo(
            configDao.load("core.instance"),
            Constant.LOG_ERR,
            "core.dao.impl.config-jpa-dao.exception",
            ErrorJsonGenerator.getInstance().generate(e)));
  }
}
