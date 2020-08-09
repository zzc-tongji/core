package io.github.messagehelper.core.controller;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.dto.api.connectors.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PostPutDeleteResponseDto;
import io.github.messagehelper.core.dto.api.connectors.PostPutRequestDto;
import io.github.messagehelper.core.dto.api.deliveries.PostRequestDto;
import io.github.messagehelper.core.exception.TokenInvalidException;
import io.github.messagehelper.core.utils.DisableCacheHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
  private static final String PREFIX = "/api";

  private ConfigDao configDao;
  private ConnectorDao connectorDao;

  @Autowired
  public ApiController(ConfigDao configDao, ConnectorDao connectorDao) {
    this.configDao = configDao;
    this.connectorDao = connectorDao;
  }

  //  PREFIX + "/deliveries"

  @PostMapping(value = PREFIX + "/deliveries")
  public ResponseEntity<String> deliveriesPost(@RequestBody @Validated PostRequestDto dto) {
    dto.authenticate(configDao.load("core.backend.token"));
    return connectorDao.execute(dto.getPayload());
  }

  //  PREFIX + "/connectors"

  @GetMapping(value = PREFIX + "/connectors")
  public ResponseEntity<GetAllResponseDto> connectorsGetAll(@RequestHeader("token") String token) {
    if (!configDao.load("core.backend.token").equals(token)) {
      throw new TokenInvalidException("token: not valid");
    }
    return ResponseEntity.status(200)
        .headers(DisableCacheHeader.getInstance())
        .body(connectorDao.readAll());
  }

  @GetMapping(value = PREFIX + "/connectors/{idOrInstance}")
  public ResponseEntity<io.github.messagehelper.core.dto.api.connectors.GetResponseDto>
      connectorsGet(
          @PathVariable("idOrInstance") String idOrInstance, @RequestHeader("token") String token) {
    if (!configDao.load("core.backend.token").equals(token)) {
      throw new TokenInvalidException("token: not valid");
    }
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

  @PostMapping(value = PREFIX + "/connectors")
  public PostPutDeleteResponseDto connectorsPost(@RequestBody @Validated PostPutRequestDto dto) {
    dto.authenticate(configDao.load("core.backend.token"));
    return connectorDao.create(dto);
  }

  @PutMapping(value = PREFIX + "/connectors/{id}")
  public PostPutDeleteResponseDto connectorsPut(
      @PathVariable("id") Long id, @RequestBody @Validated PostPutRequestDto dto) {
    dto.authenticate(configDao.load("core.backend.token"));
    return connectorDao.update(id, dto);
  }

  @DeleteMapping(value = PREFIX + "/connectors/{id}")
  public PostPutDeleteResponseDto connectorsDelete(
      @PathVariable("id") Long id, @RequestBody @Validated TokenRequestDto dto) {
    dto.authenticate(configDao.load("core.backend.token"));
    return connectorDao.delete(id);
  }
}
