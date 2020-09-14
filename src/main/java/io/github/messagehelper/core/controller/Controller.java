package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.*;
import io.github.messagehelper.core.dto.api.logs.Constant;
import io.github.messagehelper.core.exception.IdNotNumericalException;
import io.github.messagehelper.core.utils.DisableCacheHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Controller {
  private ConfigDao configDao;
  private ConnectorDao connectorDao;
  private LogReadDao logReadDao;
  private ProcessorDao processorDao;
  private RuleDao ruleDao;
  private TokenDao tokenDao;

  @Autowired
  public Controller(
      ConfigDao configDao,
      ConnectorDao connectorDao,
      LogReadDao logReadDao,
      ProcessorDao processorDao,
      RuleDao ruleDao,
      TokenDao tokenDao) {
    this.configDao = configDao;
    this.connectorDao = connectorDao;
    this.logReadDao = logReadDao;
    this.processorDao = processorDao;
    this.ruleDao = ruleDao;
    this.tokenDao = tokenDao;
  }

  // "/api"

  @GetMapping(value = "/api")
  public ResponseEntity<io.github.messagehelper.core.dto.api.GetResponse> get() {
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(new io.github.messagehelper.core.dto.api.GetResponse());
  }

  // "/api/configs"

  @GetMapping(value = "/api/configs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetAllResponseDto>
      configsGetALL(@RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.readAll());
  }

  @GetMapping(value = "/api/configs/{key}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetPutResponseDto>
      apiConfigsGet(
          @PathVariable("key") String key,
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.read(key));
  }

  @PutMapping(value = "/api/configs/{key}")
  public io.github.messagehelper.core.dto.api.configs.GetPutResponseDto apiConfigsPut(
      @PathVariable("key") String key,
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.configs.PutRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return configDao.update(key, dto);
  }

  // "/api/connectors"

  @GetMapping(value = "/api/connectors")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto>
      apiConnectorsGetAll(
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(connectorDao.readAll());
  }

  @GetMapping(value = "/api/connectors/{idOrInstance}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto>
      apiConnectorsGet(
          @PathVariable("idOrInstance") String idOrInstance,
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return ResponseEntity.status(200)
          .headers(DisableCacheHeader.getInstance())
          .body(connectorDao.readById(Long.parseLong(idOrInstance)));
    } catch (NumberFormatException e) {
      return ResponseEntity.status(200)
          .headers(DisableCacheHeader.getInstance())
          .body(connectorDao.readByInstance(idOrInstance));
    }
  }

  @PutMapping(value = "/api/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsPut(
          @PathVariable("id") String id,
          @RequestHeader(name = "api-token", required = false) String headerApiToken,
          @RequestBody @Validated
              io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    try {
      return connectorDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = "/api/connectors")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsPost(
          @RequestHeader(name = "api-token", required = false) String headerApiToken,
          @RequestBody @Validated
              io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return connectorDao.create(dto);
  }

  @DeleteMapping(value = "/api/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      apiConnectorsDelete(
          @PathVariable("id") String id,
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return connectorDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  // "/api/connectors/{idOrInstance}/delegate?path={path}"

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @GetMapping(value = "/api/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> apiConnectorsDelegateGet(
      @PathVariable("idOrInstance") String idOrInstance,
      @RequestParam(name = "path", required = false, defaultValue = "") String path,
      @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return connectorDao.executeDelegate(Long.parseLong(idOrInstance), "GET", path, "");
    } catch (NumberFormatException e) {
      return connectorDao.executeDelegate(idOrInstance, "GET", path, "");
    }
  }

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(value = "/api/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> apiConnectorsDelegatePost(
      @PathVariable("idOrInstance") String idOrInstance,
      @RequestParam(name = "path", required = false, defaultValue = "") String path,
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody(required = false) String request) {
    if (request == null) {
      request = "";
    }
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return connectorDao.executeDelegate(Long.parseLong(idOrInstance), "POST", path, request);
    } catch (NumberFormatException e) {
      return connectorDao.executeDelegate(idOrInstance, "POST", path, request);
    }
  }

  // "/api/login"

  @PostMapping(value = "/api/login")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto apiLoginPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return tokenDao.login(dto);
  }

  @PostMapping(value = "/api/login/permanent")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto apiLoginPermanentPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return tokenDao.loginPermanent(dto);
  }

  // "/api/logout"

  @DeleteMapping(value = "/api/logout")
  public ResponseEntity<String> apiLogoutPost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.revoke(headerApiToken);
    return ResponseEntity.status(204).body("");
  }

  // "/api/logs"
  @GetMapping(value = "/api/logs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.logs.GetResponse> apiLogGet(
      HttpServletRequest httpRequest) {
    tokenDao.authenticate(new String[] {httpRequest.getHeader("api-token")});
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
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(logReadDao.readAdvance(request));
  }

  // "/api/register"

  @PostMapping(value = "/api/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void apiRegisterPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.register.PostRequestDto dto) {
    tokenDao.register(dto);
  }

  // "/api/rules"

  @GetMapping(value = "/api/rules")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetAllResponseDto>
      apiRulesGetAll(@RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(ruleDao.readAll());
  }

  @GetMapping(value = "/api/rules/{idOrName}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto>
      apiRulesGet(
          @PathVariable("idOrName") String idOrName,
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return ResponseEntity.status(200)
          .headers(DisableCacheHeader.getInstance())
          .body(ruleDao.readById(Long.parseLong(idOrName)));
    } catch (NumberFormatException e) {
      return ResponseEntity.status(200)
          .headers(DisableCacheHeader.getInstance())
          .body(ruleDao.readByName(idOrName));
    }
  }

  @PutMapping(value = "/api/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRulePut(
      @PathVariable("id") String id,
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    try {
      return ruleDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = "/api/rules")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRulePost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return ruleDao.create(dto);
  }

  @DeleteMapping(value = "/api/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto apiRuleDelete(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @PathVariable("id") String id) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return ruleDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(value = "/api/webhooks")
  public ResponseEntity<String> apiWebhooksPost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.webhooks.PostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    processorDao.startWithWebhook(dto);
    return ResponseEntity.status(204).body("");
  }

  // "/rpc/log"

  @PostMapping(value = "/rpc/log")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void rpcLogPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.rpc.log.PostRequestDto dto) {
    processorDao.start(dto);
  }
}
