package io.github.messagehelper.core.dao.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.Item;
import io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto;
import io.github.messagehelper.core.exception.ConnectorAlreadyExistentException;
import io.github.messagehelper.core.exception.ConnectorInstanceNumericalException;
import io.github.messagehelper.core.exception.ConnectorNotFoundException;
import io.github.messagehelper.core.exception.ConnectorVirtualException;
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
  private LogInsertDao logInsertDao;
  private Map<String, ConnectorPo> connectorMap;
  private final Lock lock;

  public ConnectorJpaLocalDao(
      @Autowired ConnectorJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired @Qualifier("LogInsertAsyncJpaDao") LogInsertDao logInsertDao) {
    this.repository = repository;
    this.configDao = configDao;
    this.logInsertDao = logInsertDao;
    connectorMap = new HashMap<>();
    lock = new Lock();
    //
    ConnectorPo po = new ConnectorPo();
    po.setId(0L);
    po.setInstance(Constant.CONNECTOR_INSTANCE_VIRTUAL);
    po.setCategory("webhook-connector");
    po.setUrl(configDao.load("core.instance"));
    po.setRpcToken(configDao.load("core.instance"));
    repository.save(po);
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
    ConnectorPo po = find(rule.getRuleThenInstance());
    // `po` will never be `null`,
    // since `rule.getRuleThenInstance()` always returns a valid instance,
    if (po.getId().equals(0L)) {
      // virtual connector
      executeWebhookHelper(
          rule.getRuleThenPath(), // => URL
          rule.getRuleThenMethod(), // => request header "content-type"
          BodyTemplate.fill(rule.getBodyTemplate(), log)); // => request body
      // If "request body" is an empty string, use GET method. If not, use POST method instead.
    } else {
      // normal connector
      executeHelper(
          po,
          rule.getRuleThenMethod(),
          rule.getRuleThenPath(),
          BodyTemplate.fill(rule.getBodyTemplate(), log));
    }
  }

  @Override
  @SuppressWarnings("Duplicates")
  public ResponseEntity<String> executeDelegate(Long id, String method, String path, String body) {
    if (id.equals(Constant.CONNECTOR_ID_VIRTUAL)) {
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.delegate.failure.not-allowed",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", path)
              .put("requestMethod", method)
              .put("requestBody", body)
              .toString());
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("error", String.format("connector with id [%d]: not allowed", id))
                  .toString());
    }
    ConnectorPo po = find(id);
    if (po == null) {
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.delegate.failure.connector-not-found",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", path)
              .put("requestMethod", method)
              .put("requestBody", body)
              .toString());
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("error", String.format("connector with id [%d]: not found", id))
                  .toString());
    }
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_INFO,
        "core.delegate",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("connectorInstance", po.getInstance())
            .put("connectorId", po.getId())
            .put("requestUrl", po.getUrl() + path)
            .put("requestMethod", method)
            .put("requestBody", body)
            .toString());
    // response
    return executeHelper(po, method, path, body);
  }

  @Override
  @SuppressWarnings("Duplicates")
  public ResponseEntity<String> executeDelegate(
      String instance, String method, String path, String body) {
    if (instance.equals(Constant.CONNECTOR_INSTANCE_VIRTUAL)) {
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.delegate.failure.not-allowed",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", path)
              .put("requestMethod", method)
              .put("requestBody", body)
              .toString());
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put(
                      "error", String.format("connector with instance [%s]: not allowed", instance))
                  .toString());
    }
    ConnectorPo po = find(instance);
    if (po == null) {
      // log
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.delegate.failure.connector-not-found",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", path)
              .put("requestMethod", method)
              .put("requestBody", body)
              .toString());
      // response
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("error", String.format("connector with instance [%s]: not found", instance))
                  .toString());
    }
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_INFO,
        "core.delegate",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("connectorInstance", po.getInstance())
            .put("connectorId", po.getId())
            .put("requestUrl", po.getUrl() + path)
            .put("requestMethod", method)
            .put("requestBody", body)
            .toString());
    // response
    return executeHelper(po, method, path, body);
  }

  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    // TODO: fetch url with token and '/rpc/status' to check availability and then add
    // validate
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
  @SuppressWarnings("Duplicates")
  public GetPutPostDeleteResponseDto delete(Long id) {
    // TODO: disable corresponding rule before remove connector
    // cache
    if (id.equals(Constant.CONNECTOR_ID_VIRTUAL)) {
      throw new ConnectorVirtualException(String.format("connector with id [%d]: virtual", id));
    }
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
  @SuppressWarnings("Duplicates")
  public GetPutPostDeleteResponseDto readById(Long id) {
    // cache
    if (id.equals(Constant.CONNECTOR_ID_VIRTUAL)) {
      throw new ConnectorVirtualException(String.format("connector with id [%d]: virtual", id));
    }
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
    if (instance.equals(Constant.CONNECTOR_INSTANCE_VIRTUAL)) {
      throw new ConnectorVirtualException(
          String.format("connector with instance [%s]: virtual", instance));
    }
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
      // ignore virtual connector
      if (po.getId().equals(Constant.CONNECTOR_ID_VIRTUAL)) {
        continue;
      }
      // data
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
    // validate
    if (id.equals(Constant.CONNECTOR_ID_VIRTUAL)) {
      throw new ConnectorVirtualException(String.format("connector with id [%d]: virtual", id));
    }
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
    String url = po.getUrl() + path;
    String requestMethod = method.equals("GET") ? "GET" : "POST";
    String requestBodyWithToken = insertToken(body, po.getRpcToken());
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_VERB,
        "core.executor",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestBody", requestBodyWithToken)
            .toString());
    HttpRequest request;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(url))
              .headers("content-type", "application/json;charset=utf-8")
              .method(requestMethod, HttpRequest.BodyPublishers.ofString(requestBodyWithToken))
              .build();
    } catch (URISyntaxException e) {
      // log
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.executor.failure.invalid-url",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", requestMethod)
              .put("requestBody", requestBodyWithToken)
              .toString());
      // response
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("error", String.format("url [%s]: invalid format", url))
                  .toString());
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "core.executor.failure.cannot-connect",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", requestMethod)
              .put("requestBody", requestBodyWithToken)
              .toString());
      // response
      return ResponseEntity.status(400)
          .header("content-type", "application/json;charset=utf-8")
          .body(
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("error", String.format("url [%s]: cannot connect", url))
                  .toString());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    int statusCode = response.statusCode();
    String responseBody = response.body();
    boolean success = (statusCode >= 200 && statusCode <= 299);
    // generate log with appropriate length
    ObjectNode objectNode =
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestBody", requestBodyWithToken)
            .put("responseStatus", statusCode)
            .put("responseBody", responseBody);
    String jsonString = objectNode.toString();
    if (jsonString.length() > Constant.LOG_CONTENT_LENGTH) {
      objectNode.put("requestBody", "...");
      jsonString = objectNode.toString();
    }
    if (jsonString.length() > Constant.LOG_CONTENT_LENGTH) {
      objectNode.put("responseBody", "...");
      jsonString = objectNode.toString();
    }
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        success ? Constant.LOG_LEVEL_INFO : Constant.LOG_LEVEL_WARN,
        success ? "core.executor" : "core.executor.failure.http-client-error",
        jsonString);
    // response
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

  private void executeWebhookHelper(String url, String contentType, String body) {
    String requestMethod = body.length() > 0 ? "POST" : "GET";
    // [webhook-connector] request
    HttpRequest request;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(url))
              .headers("content-type", contentType)
              .method(requestMethod, HttpRequest.BodyPublishers.ofString(body))
              .build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    // [webhook-connector] log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_INFO,
        "webhook-connector.send.request",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestBody", body)
            .toString());
    // [webhook-connector] response
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      // [webhook-connector] log
      logInsertDao.insert(
          configDao.load("core.instance"),
          Constant.LOG_LEVEL_WARN,
          "webhook-connector.send.response.failure.cannot-connect",
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", requestMethod)
              .put("requestBody", body)
              .toString());
      return;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // [webhook-connector] log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_INFO,
        "webhook-connector.send.response",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestBody", body)
            .put("responseStatus", response.statusCode())
            .put("responseBody", response.body())
            .toString());
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        Constant.LOG_LEVEL_INFO,
        "core.executor",
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", "virtual://webhook-connector")
            .put("requestMethod", "POST")
            .put("requestBody", body)
            .put("responseStatus", 204)
            .put("responseBody", "")
            .toString());
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

  private void validateInstance(String instance) {
    String message =
        String.format(
            "instance: required, a not \"%s\" string with length in [1, %d] which cannot be converted to long",
            Constant.CONNECTOR_INSTANCE_VIRTUAL, Constant.INSTANCE_LENGTH);
    try {
      Long.parseLong(instance);
      throw new ConnectorInstanceNumericalException(message);
    } catch (NumberFormatException ignored) {
    }
    if (instance.equals(Constant.CONNECTOR_INSTANCE_VIRTUAL)) {
      throw new ConnectorVirtualException(message);
    }
  }
}
