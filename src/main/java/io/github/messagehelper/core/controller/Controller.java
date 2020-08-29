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
  private static final String API = "/api";

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

  // PREFIX + "/configs"

  @GetMapping(value = API + "/configs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetAllResponseDto>
      configsGetALL(@RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.readAll());
  }

  @GetMapping(value = API + "/configs/{key}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetPutResponseDto> configsGet(
      @PathVariable("key") String key,
      @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.read(key));
  }

  @PutMapping(value = API + "/configs/{key}")
  public io.github.messagehelper.core.dto.api.configs.GetPutResponseDto configsPut(
      @PathVariable("key") String key,
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.configs.PutRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return configDao.update(key, dto);
  }

  // PREFIX + "/connectors"

  @GetMapping(value = API + "/connectors")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto>
      connectorsGetAll(@RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(connectorDao.readAll());
  }

  @GetMapping(value = API + "/connectors/{idOrInstance}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto>
      connectorsGet(
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

  @PutMapping(value = API + "/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto connectorsPut(
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

  @PostMapping(value = API + "/connectors")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto connectorsPost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated
          io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return connectorDao.create(dto);
  }

  @DeleteMapping(value = API + "/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      connectorsDelete(
          @PathVariable("id") String id,
          @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return connectorDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  // PREFIX + "/connectors/{idOrInstance}/delegate?path={path}"

  @GetMapping(value = API + "/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> connectorsDelegateGet(
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

  @PostMapping(value = API + "/connectors/{idOrInstance}/delegate")
  public ResponseEntity<String> connectorsDelegatePost(
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

  // PREFIX + "/login"

  @PostMapping(value = API + "/login")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto loginPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return tokenDao.login(dto);
  }

  @PostMapping(value = API + "/login/permanent")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto loginPermanentPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return tokenDao.loginPermanent(dto);
  }

  // PREFIX + "/logout"

  @DeleteMapping(value = API + "/logout")
  public ResponseEntity<String> logoutPost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.revoke(headerApiToken);
    return ResponseEntity.status(204).body("");
  }

  // PREFIX + "/logs"
  @GetMapping(value = API + "/logs")
  public io.github.messagehelper.core.dto.api.logs.GetResponse logGet(
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
    return logReadDao.readAdvance(request);
  }

  // PREFIX + "/register"

  @PostMapping(value = API + "/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void registerPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.register.PostRequestDto dto) {
    tokenDao.register(dto);
  }

  // PREFIX + "/rules"

  @GetMapping(value = API + "/rules")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetAllResponseDto> rulesGetAll(
      @RequestHeader(name = "api-token", required = false) String headerApiToken) {
    tokenDao.authenticate(new String[] {headerApiToken});
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(ruleDao.readAll());
  }

  @GetMapping(value = API + "/rules/{idOrName}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto>
      rulesGet(
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

  @PutMapping(value = API + "/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto rulePut(
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

  @PostMapping(value = API + "/rules")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto rulePost(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    tokenDao.authenticate(new String[] {dto.getApiToken(), headerApiToken});
    return ruleDao.create(dto);
  }

  @DeleteMapping(value = API + "/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto ruleDelete(
      @RequestHeader(name = "api-token", required = false) String headerApiToken,
      @PathVariable("id") String id) {
    tokenDao.authenticate(new String[] {headerApiToken});
    try {
      return ruleDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  // "/rpc/log"

  @PostMapping(value = "/rpc/log")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void logPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.rpc.log.PostRequestDto dto) {
    processorDao.start(dto);
  }
}
