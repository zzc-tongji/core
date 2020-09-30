package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.*;
import io.github.messagehelper.core.dto.api.logs.Constant;
import io.github.messagehelper.core.exception.IdNotNumericalException;
import io.github.messagehelper.core.processor.rule._if.Operator;
import io.github.messagehelper.core.utils.DisableCacheHeader;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Controller {
  private static final String API_TOKEN_QUERY_STRING = "apiToken";
  private static final String API_TOKEN_HEADER = "api-token";

  private final ConfigDao configDao;
  private final ConnectorDao connectorDao;
  private final LogReadDao logReadDao;
  private final ProcessorDao processorDao;
  private final RuleDao ruleDao;
  private final ApiTokenDao apiTokenDao;
  private final RpcTokenDao rpcTokenDao;

  public Controller(
      @Autowired ConfigDao configDao,
      @Autowired ConnectorDao connectorDao,
      @Autowired LogReadDao logReadDao,
      @Autowired ProcessorDao processorDao,
      @Autowired RuleDao ruleDao,
      @Autowired ApiTokenDao apiTokenDao,
      @Autowired RpcTokenDao rpcTokenDao) {
    this.configDao = configDao;
    this.connectorDao = connectorDao;
    this.logReadDao = logReadDao;
    this.processorDao = processorDao;
    this.ruleDao = ruleDao;
    this.apiTokenDao = apiTokenDao;
    this.rpcTokenDao = rpcTokenDao;
  }

  // "/api"

  @GetMapping(value = "/api")
  public ResponseEntity<io.github.messagehelper.core.dto.api.GetResponse> get() {
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(new io.github.messagehelper.core.dto.api.GetResponse());
  }

  // "/api/cache"

  @PostMapping(value = "/api/cache")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void apiCachePost(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.cache.PostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    apiTokenDao.refreshCache();
    configDao.refreshCache();
    connectorDao.refreshCache();
    ruleDao.refreshCache();
    ruleDao.fix();
  }

  // "/api/configs"

  @GetMapping(value = "/api/configs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetAllResponseDto>
      configsGetALL(
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.readAll());
  }

  @GetMapping(value = "/api/configs/{key}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetPutResponseDto>
      apiConfigsGet(
          @PathVariable("key") String key,
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(
        new String[] {headerApiToken, queryStringApiToken == null ? "1" : "2"});
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.read(key));
  }

  @PutMapping(value = "/api/configs/{key}")
  public io.github.messagehelper.core.dto.api.configs.GetPutResponseDto apiConfigsPut(
      @PathVariable("key") String key,
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.configs.PutRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    return configDao.update(key, dto);
  }

  // "/api/connectors"

  @GetMapping(value = "/api/connectors")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto>
      apiConnectorsGetAll(
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(connectorDao.readAll());
  }

  @GetMapping(value = "/api/connectors/{idOrInstance}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto>
      apiConnectorsGet(
          @PathVariable("idOrInstance") String idOrInstance,
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .headers(DisableCacheHeader.getInstance())
          .body(connectorDao.readById(Long.parseLong(idOrInstance)));
    } catch (NumberFormatException e) {
      return ResponseEntity.status(HttpStatus.OK)
          .headers(DisableCacheHeader.getInstance())
          .body(connectorDao.readByInstance(idOrInstance));
    }
  }

  @PutMapping(value = "/api/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsPut(
          @PathVariable("id") String id,
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
          @RequestBody @Validated
              io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    try {
      return connectorDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = "/api/connectors")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsPost(
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
          @RequestBody @Validated
              io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    return connectorDao.create(dto);
  }

  @DeleteMapping(value = "/api/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsDelete(
          @PathVariable("id") String id,
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return connectorDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  // "/api/connectors/{idOrInstance}/delegate?path={path}"

  @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "delegate-status")
  @GetMapping(value = "/api/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> apiConnectorsDelegateGet(
      @PathVariable("idOrInstance") String idOrInstance,
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestParam(name = "path", defaultValue = "") String path,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return connectorDao.executeDelegate(Long.parseLong(idOrInstance), path, "", "");
    } catch (NumberFormatException e) {
      return connectorDao.executeDelegate(idOrInstance, path, "", "");
    }
  }

  @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "delegate-status")
  @PostMapping(value = "/api/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> apiConnectorsDelegatePost(
      @PathVariable("idOrInstance") String idOrInstance,
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestParam(name = "path", defaultValue = "") String path,
      @RequestHeader(name = "content-type") String headerContentType,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody(required = false) String request) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return connectorDao.executeDelegate(
          Long.parseLong(idOrInstance), path, headerContentType, request == null ? "" : request);
    } catch (NumberFormatException e) {
      return connectorDao.executeDelegate(
          idOrInstance, path, headerContentType, request == null ? "" : request);
    }
  }

  // "/api/login"

  @PostMapping(value = "/api/login")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto apiLoginPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return apiTokenDao.login(dto);
  }

  @PostMapping(value = "/api/login/permanent")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto apiLoginPermanentPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return apiTokenDao.loginPermanent(dto);
  }

  // "/api/logout"

  @DeleteMapping(value = "/api/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void apiLogoutPost(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.revoke(headerApiToken);
    apiTokenDao.revoke(queryStringApiToken);
  }

  // "/api/logs"
  @GetMapping(value = "/api/logs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.logs.GetResponse> apiLogGet(
      HttpServletRequest httpRequest) {
    apiTokenDao.authenticate(
        new String[] {
          httpRequest.getHeader(API_TOKEN_HEADER), httpRequest.getParameter(API_TOKEN_QUERY_STRING)
        });
    io.github.messagehelper.core.dto.api.logs.GetRequest request =
        new io.github.messagehelper.core.dto.api.logs.GetRequest(
            httpRequest.getRequestURL().toString(),
            httpRequest.getParameter(Constant.ID_GREATER_THAN),
            httpRequest.getParameter(Constant.ID_LESS_THAN),
            httpRequest.getParameter(Constant.INSTANCE_CONTAIN),
            httpRequest.getParameter(Constant.LEVEL_CONTAIN),
            httpRequest.getParameter(Constant.CATEGORY_CONTAIN),
            httpRequest.getParameter(Constant.TIMESTAMP_MS_GREATER_THAN),
            httpRequest.getParameter(Constant.TIMESTAMP_MS_LESS_THAN),
            httpRequest.getParameter(Constant.CONTENT_CONTAIN),
            httpRequest.getParameter(Constant.ORDER),
            httpRequest.getParameter(Constant.ASCENDING),
            httpRequest.getParameter(Constant.PAGE),
            httpRequest.getParameter(Constant.SIZE));
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(logReadDao.readAdvance(request));
  }

  // "/api/operators"
  @GetMapping(value = "/api/operators")
  public ResponseEntity<String> apiOperatorsGet(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(Operator.DTO);
  }

  // "/api/register"

  @PostMapping(value = "/api/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void apiRegisterPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.register.PostRequestDto dto) {
    apiTokenDao.register(dto);
  }

  // "/api/rules"

  @GetMapping(value = "/api/rules")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetAllResponseDto>
      apiRulesGetAll(
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    return ResponseEntity.status(HttpStatus.OK)
        .headers(DisableCacheHeader.getInstance())
        .body(ruleDao.readAll());
  }

  @GetMapping(value = "/api/rules/{idOrName}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto>
      apiRulesGet(
          @PathVariable("idOrName") String idOrName,
          @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "")
              String queryStringApiToken,
          @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return ResponseEntity.status(HttpStatus.OK)
          .headers(DisableCacheHeader.getInstance())
          .body(ruleDao.readById(Long.parseLong(idOrName)));
    } catch (NumberFormatException e) {
      return ResponseEntity.status(HttpStatus.OK)
          .headers(DisableCacheHeader.getInstance())
          .body(ruleDao.readByName(idOrName));
    }
  }

  @PutMapping(value = "/api/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRulePut(
      @PathVariable("id") String id,
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    try {
      return ruleDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = "/api/rules")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRulePost(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    return ruleDao.create(dto);
  }

  @DeleteMapping(value = "/api/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRuleDelete(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @PathVariable("id") String id) {
    apiTokenDao.authenticate(new String[] {headerApiToken, queryStringApiToken});
    try {
      return ruleDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(value = "/api/webhooks")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void apiWebhooksPost(
      @RequestParam(name = API_TOKEN_QUERY_STRING, defaultValue = "") String queryStringApiToken,
      @RequestHeader(name = API_TOKEN_HEADER, defaultValue = "") String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.webhooks.PostRequestDto dto) {
    apiTokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken, queryStringApiToken});
    processorDao.startWithWebhook(dto);
  }

  // "/rpc"

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(value = "/rpc/log")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void rpcLogPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.rpc.log.PostRequestDto dto) {
    rpcTokenDao.authenticate(dto.getRpcToken());
    processorDao.start(dto);
  }

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(value = "/rpc/status")
  public ResponseEntity<String> rpcStatusPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.rpc.status.PostRequestDto dto) {
    rpcTokenDao.authenticate(dto.getRpcToken());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            ObjectMapperSingleton.getInstance()
                .getNodeFactory()
                .objectNode()
                .put("instance", configDao.load("core.instance"))
                .put("category", "core")
                .put("status", "ready")
                .toString());
  }
}
