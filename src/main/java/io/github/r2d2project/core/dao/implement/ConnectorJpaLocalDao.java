package io.github.r2d2project.core.dao.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.r2d2project.core.dao.*;
import io.github.r2d2project.core.dto.api.connectors.GetAllResponseDto;
import io.github.r2d2project.core.dto.api.connectors.GetPutPostDeleteResponseDto;
import io.github.r2d2project.core.dto.api.connectors.Item;
import io.github.r2d2project.core.dto.api.connectors.PutPostRequestDto;
import io.github.r2d2project.core.exception.type.*;
import io.github.r2d2project.core.processor.log.Log;
import io.github.r2d2project.core.processor.rule.Rule;
import io.github.r2d2project.core.processor.rule.then.Body;
import io.github.r2d2project.core.persistence.Constant;
import io.github.r2d2project.core.persistence.po.ConnectorPo;
import io.github.r2d2project.core.persistence.repository.ConnectorJpaRepository;
import io.github.r2d2project.core.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
  private static final int INDEX_INSTANCE = 0;
  private static final int INDEX_CATEGORY = 1;
  public static final String EXCEPTION_MESSAGE_INSTANCE =
      String.format(
          "fetch => instance: required, a non \"%s\" string with length in [1, %d] which cannot be converted to long",
          ConfigMapSingleton.getInstance().load("core.instance"), Constant.INSTANCE_LENGTH);
  public static final String EXCEPTION_MESSAGE_CATEGORY =
      String.format(
          "fetch => category: required, string with length in [1, %d]", Constant.CATEGORY_LENGTH);

  private final ConnectorJpaRepository repository;
  private final ConfigDao configDao;
  private final LogInsertDao logInsertDao;
  private final RuleDao ruleDao;
  private final ProcessorDao processorDao;
  private final Map<Long, ConnectorPo> connectorMapById;
  private final Map<String, ConnectorPo> connectorMapByInstance;
  private final Lock lock;

  public ConnectorJpaLocalDao(
      @Autowired ConnectorJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired @Qualifier("LogInsertJpaDao") LogInsertDao LogInsertJpaDao,
      @Autowired @Qualifier("LogInsertAsyncJpaDao") LogInsertDao LogInsertAsyncJpaDao,
      @Autowired @Lazy RuleDao ruleDao,
      @Autowired @Lazy ProcessorDao processorDao,
      @Value("${spring.datasource.driver-class-name}") String driverClassName) {
    // initialize
    this.repository = repository;
    this.configDao = configDao;
    this.logInsertDao =
        driverClassName.equals("org.sqlite.JDBC") ? LogInsertJpaDao : LogInsertAsyncJpaDao;
    this.ruleDao = ruleDao;
    this.processorDao = processorDao;
    connectorMapById = new HashMap<>();
    connectorMapByInstance = new HashMap<>();
    lock = new Lock();
    // create virtual connector
    ConnectorPo po = new ConnectorPo();
    po.setId(0L);
    po.setInstance(configDao.load("core.instance"));
    po.setCategory("webhook-connector");
    po.setUrl("");
    po.setRpcToken("");
    repository.save(po);
    // cache
    refreshCache();
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
      connectorMapById.clear();
      connectorMapByInstance.clear();
      List<ConnectorPo> data = repository.findAll();
      for (ConnectorPo item : data) {
        connectorMapById.put(item.getId(), item);
        connectorMapByInstance.put(item.getInstance(), item);
      }
      // UNLOCK
      lock.writeDecrease();
      //
    }
  }

  @Override
  public void executeRule(Rule rule, Log log) {
    ConnectorPo po = find(rule.getThenUseConnectorId());
    // `po` will never be `null`,
    // since `rule.getThenUseConnectorId()` always returns a valid id,
    if (po.getId().equals(0L)) {
      // virtual connector
      executeWebhookHelper(
          rule.getThenUseUrlPath(),
          rule.getThenUseHeaderContentType(),
          Body.fill(rule.getThenUseBodyTemplate(), log, rule.getThenUseBodyJson()));
    } else {
      // normal connector
      executeHelper(
          po,
          rule.getThenUseUrlPath(),
          rule.getThenUseHeaderContentType(),
          rule.getThenUseBodyJson(),
          Body.fill(rule.getThenUseBodyTemplate(), log, rule.getThenUseBodyJson()));
    }
    // If request header content-type" is an empty string, use GET method,
    // otherwise, use POST method.
  }

  @Override
  @SuppressWarnings("Duplicates")
  public ResponseEntity<String> executeDelegate(
      Long id, String path, String contentType, String body) {
    String method = contentType.length() > 0 ? "POST" : "GET";
    ConnectorPo po = find(id);
    if (po == null) {
      logInsertDao.insert(
          configDao.load("core.instance"),
          "core.delegate.failure.connector-not-found",
          Constant.LOG_LEVEL_WARN,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestPath", path)
              .put("requestMethod", method)
              .put("requestHeaderContentType", contentType)
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
        "core.delegate",
        Constant.LOG_LEVEL_INFO,
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("connectorInstance", po.getInstance())
            .put("connectorId", po.getId())
            .put("requestUrl", po.getUrl() + path)
            .put("requestMethod", method)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", body)
            .toString());
    // response
    return executeHelper(
        po, path, contentType, contentType.toLowerCase().contains("application/json"), body);
  }

  @Override
  @SuppressWarnings("Duplicates")
  public ResponseEntity<String> executeDelegate(
      String instance, String path, String contentType, String body) {
    String method = contentType.length() > 0 ? "POST" : "GET";
    ConnectorPo po = find(instance);
    if (po == null) {
      // log
      logInsertDao.insert(
          configDao.load("core.instance"),
          "core.delegate.failure.connector-not-found",
          Constant.LOG_LEVEL_WARN,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestPath", path)
              .put("requestMethod", method)
              .put("requestHeaderContentType", contentType)
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
        "core.delegate",
        Constant.LOG_LEVEL_INFO,
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("connectorInstance", po.getInstance())
            .put("connectorId", po.getId())
            .put("requestUrl", po.getUrl() + path)
            .put("requestMethod", method)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", body)
            .toString());
    // response
    return executeHelper(
        po, path, contentType, contentType.toLowerCase().contains("application/json"), body);
  }

  @Override
  public boolean notExistent(Long id) {
    return find(id) == null;
  }

  @Override
  public String getUrlById(Long id) {
    if (notExistent(id)) {
      return "";
    }
    return find(id).getUrl();
  }

  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    // convert and validate
    ConnectorPo po = new ConnectorPo();
    requestDtoToPo(null, dto, po);
    // cache
    if (find(po.getInstance()) != null) {
      throw new ConnectorAlreadyExistentException(
          String.format("connector with instance [%s]: already existent", po.getInstance()));
    }
    // database
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
    // disable corresponding rules before removing
    ruleDao.disableRuleByConnectorId(id);
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
    if (instance.equals(configDao.load("core.instance"))) {
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
    // convert and validate
    if (id.equals(Constant.CONNECTOR_ID_VIRTUAL)) {
      throw new ConnectorVirtualException(String.format("connector with id [%d]: virtual", id));
    }
    ConnectorPo updatedPo = new ConnectorPo();
    requestDtoToPo(id, dto, updatedPo);
    // cache
    ConnectorPo po = find(id);
    if (po == null) {
      throw new ConnectorNotFoundException(String.format("connector with id [%d]: not found", id));
    }
    if (!po.getInstance().equals(updatedPo.getInstance())) {
      po = find(updatedPo.getInstance());
      if (po != null) {
        throw new ConnectorAlreadyExistentException(
            String.format(
                "connector with instance [%s]: already existent", updatedPo.getInstance()));
      }
    }
    // database
    repository.save(updatedPo);
    refreshCache();
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(updatedPo, responseDto);
    return responseDto;
  }

  @SuppressWarnings({"Duplicates", "BusyWait"})
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
    ConnectorPo po = connectorMapById.get(id);
    // UNLOCK
    lock.readDecrease();
    //
    return po;
  }

  @SuppressWarnings({"Duplicates", "BusyWait"})
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
    ConnectorPo po = connectorMapByInstance.get(instance);
    // UNLOCK
    lock.readDecrease();
    //
    return po;
  }

  @SuppressWarnings("BusyWait")
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
    Collection<ConnectorPo> poCollection = connectorMapById.values();
    // UNLOCK
    lock.readDecrease();
    //
    return poCollection;
  }

  private ResponseEntity<String> executeHelper(
      ConnectorPo po, String path, String contentType, boolean bodyJson, String body) {
    String url = po.getUrl() + path;
    String method = contentType.length() > 0 ? "POST" : "GET";
    String bodyWithToken = bodyJson ? insertToken(body, po.getRpcToken()) : body;
    // log
    logInsertDao.insert(
        configDao.load("core.instance"),
        "core.executor",
        Constant.LOG_LEVEL_VERB,
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", method)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", bodyWithToken)
            .toString());
    HttpRequest request;
    try {
      request =
          contentType.length() > 0
              ? HttpRequest.newBuilder()
                  .uri(new URI(url))
                  .headers("content-type", contentType)
                  .POST(HttpRequest.BodyPublishers.ofString(bodyWithToken))
                  .build()
              : HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    } catch (URISyntaxException e) {
      // log
      logInsertDao.insert(
          configDao.load("core.instance"),
          "core.executor.failure.invalid-url",
          Constant.LOG_LEVEL_WARN,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", method)
              .put("requestHeaderContentType", contentType)
              .put("requestBody", bodyWithToken)
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
          "core.executor.failure.cannot-connect",
          Constant.LOG_LEVEL_WARN,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", method)
              .put("requestHeaderContentType", contentType)
              .put("requestBody", bodyWithToken)
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
            .put("requestMethod", method)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", bodyWithToken)
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
        success ? "core.executor" : "core.executor.failure.http-client-error",
        success ? Constant.LOG_LEVEL_INFO : Constant.LOG_LEVEL_WARN,
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
    String requestMethod = contentType.length() > 0 ? "POST" : "GET";
    // [webhook-connector] request
    HttpRequest request;
    try {
      request =
          contentType.length() > 0
              ? HttpRequest.newBuilder()
                  .uri(new URI(url))
                  .headers("content-type", contentType)
                  .POST(HttpRequest.BodyPublishers.ofString(body))
                  .build()
              : HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    // [webhook-connector] log
    processorDao.startWithWebhook(
        configDao.load("core.instance"),
        "webhook-connector.send.request",
        Constant.LOG_LEVEL_INFO,
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", body)
            .toString());
    // [webhook-connector] response
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      // [webhook-connector] log
      processorDao.startWithWebhook(
          configDao.load("core.instance"),
          "webhook-connector.send.response.failure.cannot-connect",
          Constant.LOG_LEVEL_WARN,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("requestUrl", url)
              .put("requestMethod", requestMethod)
              .put("requestHeaderContentType", contentType)
              .put("requestBody", body)
              .toString());
      return;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // [webhook-connector] log
    processorDao.startWithWebhook(
        configDao.load("core.instance"),
        "webhook-connector.send.response",
        Constant.LOG_LEVEL_INFO,
        ObjectMapperSingleton.getInstance()
            .getNodeFactory()
            .objectNode()
            .put("requestUrl", url)
            .put("requestMethod", requestMethod)
            .put("requestHeaderContentType", contentType)
            .put("requestBody", body)
            .put("responseStatus", response.statusCode())
            .put("responseBody", response.body())
            .toString());
    // log
    logInsertDao.insert(
        configDao.load("core.instance"), "core.executor.virtual", Constant.LOG_LEVEL_INFO, "");
  }

  private String insertToken(String requestBody, String token) {
    JsonNode node;
    try {
      node = ObjectMapperSingleton.getInstance().readTree(requestBody);
    } catch (JsonProcessingException e) {
      return ObjectMapperSingleton.getInstance()
          .getNodeFactory()
          .objectNode()
          .put("rpcToken", token)
          .toString();
    }
    if (!node.isObject()) {
      return ObjectMapperSingleton.getInstance()
          .getNodeFactory()
          .objectNode()
          .put("rpcToken", token)
          .toString();
    }
    return ((ObjectNode) node).put("rpcToken", token).toString();
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
    String[] result = fetchAndValidate(dto.getUrl(), dto.getRpcToken());
    po.setInstance(result[INDEX_INSTANCE]);
    po.setCategory(result[INDEX_CATEGORY]);
    po.setUrl(dto.getUrl());
    po.setRpcToken(dto.getRpcToken());
    if (id == null) {
      po.setId(IdGenerator.getInstance().generate());
    } else {
      po.setId(id);
    }
  }

  private String[] fetchAndValidate(String url, String rpcToken) {
    String urlWithPath = url + "/rpc/status";
    HttpRequest request;
    try {
      // validate both `url` and `urlWithPath`
      new URI(url);
      request =
          HttpRequest.newBuilder()
              .uri(new URI(urlWithPath))
              .headers("content-type", "application/json;charset=utf-8")
              .POST(
                  HttpRequest.BodyPublishers.ofString(
                      ObjectMapperSingleton.getInstance()
                          .getNodeFactory()
                          .objectNode()
                          .put("rpcToken", rpcToken)
                          .toString()))
              .build();
    } catch (URISyntaxException e) {
      throw new ConnectorFetchInvalidUrlException(PutPostRequestDto.EXCEPTION_MESSAGE_URL);
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new ConnectorFetchCannotConnectException(
          String.format("cannot connect url [%s]", urlWithPath));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    int statusCode = response.statusCode();
    if (statusCode <= 199 || statusCode >= 300) {
      throw new ConnectorFetchHttpErrorException(
          String.format(
              "fetch url [%s] with method POST => status code [%d]", urlWithPath, statusCode));
    }
    String json = response.body();
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new ConnectorFetchInvalidJsonException(
          String.format(
              "fetch url [%s] with method POST => response body [%s] => invalid JSON",
              urlWithPath, json));
    }
    String[] result = new String[2];
    JsonNode temp = jsonNode.get("instance");
    if (temp != null && temp.isTextual()) {
      result[INDEX_INSTANCE] = temp.asText();
      if (result[INDEX_INSTANCE].length() <= 0
          || result[INDEX_INSTANCE].length() > Constant.INSTANCE_LENGTH) {
        throw new ConnectorInstanceInvalidFormatException(EXCEPTION_MESSAGE_INSTANCE);
      }
      if (result[INDEX_INSTANCE].equals(configDao.load("core.instance"))) {
        throw new ConnectorVirtualException(EXCEPTION_MESSAGE_INSTANCE);
      }
      try {
        Long.parseLong(result[INDEX_INSTANCE]);
        throw new ConnectorInstanceNumericalException(EXCEPTION_MESSAGE_INSTANCE);
      } catch (NumberFormatException ignored) {
      }
    } else {
      throw new ConnectorInstanceInvalidFormatException(EXCEPTION_MESSAGE_INSTANCE);
    }
    temp = jsonNode.get("category");
    if (temp != null && temp.isTextual()) {
      result[INDEX_CATEGORY] = temp.asText();
    } else {
      throw new ConnectorCategoryInvalidFormatException(EXCEPTION_MESSAGE_CATEGORY);
    }
    return result;
  }
}
