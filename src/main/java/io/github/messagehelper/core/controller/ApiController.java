package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.dao.TokenDao;
import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.dto.api.configs.PutRequestDto;
import io.github.messagehelper.core.exception.IdNotNumericalException;
import io.github.messagehelper.core.utils.DisableCacheHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
  private static final String PREFIX = "/api";

  private ConfigDao configDao;
  private ConnectorDao connectorDao;
  private RuleDao ruleDao;
  private TokenDao tokenDao;

  @Autowired
  public ApiController(
      ConfigDao configDao, ConnectorDao connectorDao, RuleDao ruleDao, TokenDao tokenDao) {
    this.configDao = configDao;
    this.connectorDao = connectorDao;
    this.ruleDao = ruleDao;
    this.tokenDao = tokenDao;
  }

  // PREFIX + "/configs"

  @GetMapping(value = PREFIX + "/configs")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetAllResponseDto>
      configsGetALL(@RequestHeader("token") String token) {
    tokenDao.authenticate(token);
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.readAll());
  }

  @GetMapping(value = PREFIX + "/configs/{key}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.configs.GetPutResponseDto> configsGet(
      @PathVariable("key") String key, @RequestHeader("token") String token) {
    tokenDao.authenticate(token);
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(configDao.read(key));
  }

  @PutMapping(value = PREFIX + "/configs/{key}")
  public io.github.messagehelper.core.dto.api.configs.GetPutResponseDto configsPut(
      @PathVariable("key") String key, @RequestBody @Validated PutRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    return configDao.update(key, dto);
  }

  // PREFIX + "/connectors"

  @GetMapping(value = PREFIX + "/connectors")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto>
      connectorsGetAll(@RequestHeader("token") String token) {
    tokenDao.authenticate(token);
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(connectorDao.readAll());
  }

  @GetMapping(value = PREFIX + "/connectors/{idOrInstance}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto>
      connectorsGet(
          @PathVariable("idOrInstance") String idOrInstance, @RequestHeader("token") String token) {
    tokenDao.authenticate(token);
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

  @PutMapping(value = PREFIX + "/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto connectorsPut(
      @PathVariable("id") String id,
      @RequestBody @Validated
          io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    try {
      return connectorDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = PREFIX + "/connectors")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto connectorsPost(
      @RequestBody @Validated
          io.github.messagehelper.core.dto.api.connectors.PutPostRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    return connectorDao.create(dto);
  }

  @DeleteMapping(value = PREFIX + "/connectors/{id}")
  public io.github.messagehelper.core.dto.api.connectors.GetPutPostDeleteResponseDto
      connectorsDelete(@PathVariable("id") String id, @RequestBody @Validated TokenRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    try {
      return connectorDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  // PREFIX + "/delegate"

  @PostMapping(value = PREFIX + "/delegate")
  public ResponseEntity<String> delegatePost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.delegate.PostRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    return connectorDao.execute(dto);
  }

  // PREFIX + "/login"

  @PostMapping(value = PREFIX + "/login")
  public io.github.messagehelper.core.dto.api.login.PostResponseDto loginPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.login.PostRequestDto dto) {
    return tokenDao.login(dto);
  }

  // PREFIX + "/register"

  @PostMapping(value = PREFIX + "/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void registerPost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.register.PostRequestDto dto) {
    tokenDao.register(dto);
  }

  // PREFIX + "/rules"

  @GetMapping(value = PREFIX + "/rules")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetAllResponseDto> rulesGetAll(
      @RequestHeader("token") String token) {
    tokenDao.authenticate(token);
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(ruleDao.readAll());
  }

  @GetMapping(value = PREFIX + "/rules/{idOrName}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto>
      rulesGet(@PathVariable("idOrName") String idOrName, @RequestHeader("token") String token) {
    tokenDao.authenticate(token);
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

  @PutMapping(value = PREFIX + "/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto rulePut(
      @PathVariable("id") String id,
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    try {
      return ruleDao.update(Long.parseLong(id), dto);
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }

  @PostMapping(value = PREFIX + "/rules")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto rulePost(
      @RequestBody @Validated io.github.messagehelper.core.dto.api.rules.PutPostRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    return ruleDao.create(dto);
  }

  @DeleteMapping(value = PREFIX + "/rules/{id}")
  public io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto ruleDelete(
      @PathVariable("id") String id, @RequestBody @Validated TokenRequestDto dto) {
    tokenDao.authenticate(dto.getToken());
    try {
      return ruleDao.delete(Long.parseLong(id));
    } catch (NumberFormatException e) {
      throw new IdNotNumericalException("path {id}: required, long");
    }
  }
}
