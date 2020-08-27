package io.github.messagehelper.core.dao.implement;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.Item;
import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.exception.ConnectorAlreadyExistentException;
import io.github.messagehelper.core.exception.ConnectorNotFoundException;
import io.github.messagehelper.core.exception.RuleNameNumericalException;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.ConnectorPo;
import io.github.messagehelper.core.mysql.repository.ConnectorJpaRepository;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.BodyTemplate;
import io.github.messagehelper.core.processor.rule.Rule;
import io.github.messagehelper.core.utils.HttpClientSingleton;
import io.github.messagehelper.core.utils.IdGenerator;
import io.github.messagehelper.core.utils.Lock;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;
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
  public void executeRule(Rule rule, Log log) {
    executeDelegate(
        rule.getRuleThenInstance(),
        rule.getRuleThenMethod(),
        rule.getRuleThenPath(),
        BodyTemplate.fill(rule.getBodyTemplate(), log));
  }

  @Override
  public ResponseEntity<String> executeDelegate(Long id, String method, String path, String body) {
    ConnectorPo po = find(id);
    if (po == null) {
      String errorJson =
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("error", String.format("connector with id [%d]: not found", id))
              .toString();
      logDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_ERR,
          "core.dao.connector-dao.execute-delegate.connector-not-found.exception",
          errorJson);
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(errorJson);
    }
    return executeHelper(po, method, path, body);
  }

  @Override
  public ResponseEntity<String> executeDelegate(
      String instance, String method, String path, String body) {
    ConnectorPo po = find(instance);
    if (po == null) {
      String errorJson =
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("error", String.format("connector with instance [%s]: not found", instance))
              .toString();
      logDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_ERR,
          "core.dao.connector-dao.execute-delegate.connector-not.exception",
          errorJson);
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(errorJson);
    }
    return executeHelper(po, method, path, body);
  }

  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    validateInstance(dto.getInstance());
    // cache
    if (find(dto.getInstance()) != null) {
      throw new ConnectorAlreadyExistentException(
          String.format("connector with instance [%s]: already existent", dto.getInstance()));
    }
    // database
    ConnectorPo po = new ConnectorPo();
    requestDtoToPo(0L, dto, po);
    repository.save(po);
    refreshCache();
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto delete(Long id) {
    // cache
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    // database
    repository.deleteById(id);
    refreshCache();
    //
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto readById(Long id) {
    // cache
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto readByInstance(String instance) {
    // cache
    ConnectorPo po = find(instance);
    if (po == null) {
      throw new ConnectorNotFoundException(
          String.format("connector with instance [%s]: not found", instance));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

  @Override
  public GetAllResponseDto readAll() {
    // cache
    Collection<ConnectorPo> collection = findAll();
    // response
    GetAllResponseDto responseDto = new GetAllResponseDto();
    Collection<Item> data = responseDto.getData();
    Item item;
    for (ConnectorPo po : collection) {
      item = new Item();
      item.setId(po.getId());
      item.setInstance(po.getInstance());
      item.setCategory(po.getCategory());
      item.setUrl(po.getUrl());
      item.setRpcToken(po.getRpcToken());
      data.add(item);
    }
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto) {
    validateInstance(dto.getInstance());
    // cache
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
    // database
    ConnectorPo updatedPo = new ConnectorPo();
    requestDtoToPo(id, dto, updatedPo);
    repository.save(updatedPo);
    refreshCache();
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(updatedPo, responseDto);
    return responseDto;
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

  private ResponseEntity<String> executeHelper(
      ConnectorPo po, String method, String path, String body) {
    HttpRequest request;
    String url = po.getUrl() + path;
    String requestMethod = method.equals("GET") ? "GET" : "POST";
    String requestBodyWithToken = insertToken(body, po.getRpcToken());
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(url))
              .headers("content-type", "application/json;charset=utf-8")
              .method(requestMethod, HttpRequest.BodyPublishers.ofString(requestBodyWithToken))
              .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      ObjectNode objectNode =
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put(
                  "error",
                  String.format(
                      "connector with instance [%s] and url [%s]: fail to connect",
                      po.getInstance(), url));
      String jsonString = objectNode.toString();
      logDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_ERR,
          "core.dao.connector-dao.execute-helper.fail-to-connect.exception",
          jsonString);
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(jsonString);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    int statusCode = response.statusCode();
    if (statusCode <= 199 || statusCode >= 300) {
      ObjectNode objectNode =
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("instance", po.getInstance())
              .put("category", po.getCategory())
              .put("url", url)
              .put("requestMethod", requestMethod)
              .put("requestBody", requestBodyWithToken)
              .put("responseStatus", statusCode)
              .put("responseBody", response.body())
              .put("url", url);
      String jsonString = objectNode.toString();
      System.out.println(jsonString);
      if (jsonString.length() > Constant.LOG_CONTENT_LENGTH) {
        objectNode.put("requestBody", "...");
        jsonString = objectNode.toString();
      }
      if (jsonString.length() > Constant.LOG_CONTENT_LENGTH) {
        objectNode.put("responseBody", "...");
        jsonString = objectNode.toString();
      }
      logDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_ERR,
          "core.dao.connector-dao.execute-helper.fail-to-fetch.fetch.exception",
          jsonString);
    }
    String responseBody = response.body();
    if (responseBody.length() <= 0) {
      return ResponseEntity.status(204)
          .header("delegate-status", String.valueOf(statusCode))
          .body(responseBody);
    }
    return ResponseEntity.status(200)
        .header("content-type", response.headers().allValues("content-type").get(0))
        .header("delegate-status", String.valueOf(statusCode))
        .body(response.body());
  }

  private String insertToken(String requestBody, String token) {
    if (requestBody.length() <= 0
        || requestBody.equals("{}")
        || requestBody.equals("[]")
        || requestBody.equals("null")
        || requestBody.equals("undefined")) {
      return String.format("{\"rpcToken\":\"%s\"}", token);
    }
    if (requestBody.contains("\"rpcToken\"")) {
      return requestBody;
    }
    StringBuilder builder = new StringBuilder(requestBody);
    builder.insert(builder.length() - 1, String.format(",\"rpcToken\":\"%s\"", token));
    return builder.toString();
  }

  private void poToResponseDto(ConnectorPo po, GetPutPostDeleteResponseDto dto) {
    Item item = dto.getData();
    item.setId(po.getId());
    item.setInstance(po.getInstance());
    item.setCategory(po.getCategory());
    item.setUrl(po.getUrl());
    item.setRpcToken(po.getRpcToken());
  }

  private void requestDtoToPo(Long id, PutPostRequestDto dto, ConnectorPo po) {
    if (id.equals(0L)) {
      po.setId(IdGenerator.getInstance().generate());
    } else {
      po.setId(id);
    }
    po.setInstance(dto.getInstance());
    po.setCategory(dto.getCategory());
    po.setUrl(dto.getUrl());
    po.setRpcToken(dto.getRpcToken());
  }

  public void validateInstance(String instance) {
    try {
      Long.parseLong(instance);
      throw new RuleNameNumericalException(
          "instance: required, string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "] which cannot be converted to long");
    } catch (NumberFormatException ignored) {
    }
  }
}
