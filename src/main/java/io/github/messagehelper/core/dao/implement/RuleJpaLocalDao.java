package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogInsertDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.dto.api.rules.GetAllResponseDto;
import io.github.messagehelper.core.dto.api.rules.GetPutPostDeleteResponseDto;
import io.github.messagehelper.core.dto.api.rules.Item;
import io.github.messagehelper.core.dto.api.rules.PutPostRequestDto;
import io.github.messagehelper.core.exception.*;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.mysql.repository.RuleJpaRepository;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.Rule;
import io.github.messagehelper.core.processor.rule._if.Condition;
import io.github.messagehelper.core.utils.IdGenerator;
import io.github.messagehelper.core.utils.Lock;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;
import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class RuleJpaLocalDao implements RuleDao {
  private static final String EXCEPTION_MESSAGE_CORE_INSTANCE =
      String.format(
          "value of \"core.instance\": required, string with length in [1, %s] which cannot be any `ifLogInstanceEqual` in rules",
          Constant.INSTANCE_LENGTH);

  private final RuleJpaRepository repository;
  private final ConfigDao configDao;
  private final ConnectorDao connectorDao;
  private final LogInsertDao logInsertDao;
  private final List<Rule> ruleList;
  private final Lock lock;
  private final Logger logger;

  public RuleJpaLocalDao(
      @Autowired RuleJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired ConnectorDao connectorDao,
      @Autowired @Qualifier("LogInsertAsyncJpaDao") LogInsertDao logInsertDao) {
    this.repository = repository;
    this.configDao = configDao;
    this.connectorDao = connectorDao;
    this.logInsertDao = logInsertDao;
    ruleList = new ArrayList<>();
    lock = new Lock();
    logger = LoggerFactory.getLogger(RuleJpaLocalDao.class);
    //
    refreshCache();
    fix();
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
      ruleList.clear();
      List<RulePo> list = repository.findAll();
      for (RulePo po : list) {
        try {
          ruleList.add(Rule.parse(po));
        } catch (InvalidRuleIfException | InvalidRuleThenException e) {
          logger.error(String.format("rule with name [%s]: %s", po.getName(), e.toString()));
        }
      }
      Collections.sort(ruleList);
      // UNLOCK
      lock.writeDecrease();
    }
  }

  @SuppressWarnings("BusyWait")
  @Override
  public void process(Log log) {
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
    for (Rule rule : ruleList) {
      if (!rule.getEnable()) {
        continue;
      }
      // match rule
      boolean breakFor = false;
      int match = rule.satisfy(log);
      switch (match) {
        case Rule.HIT:
          // log
          logInsertDao.insert(
              configDao.load("core.instance"),
              "core.rule.hit",
              Constant.LOG_LEVEL_INFO,
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("ruleName", rule.getName())
                  .put("ruleId", rule.getId())
                  .put("logId", log.getId())
                  .toString());
          // execute rule
          connectorDao.executeRule(rule, log);
          // terminate or not
          if (rule.getTerminate()) {
            breakFor = true;
          }
          break;
        case Rule.MISS_INSTANCE:
          logInsertDao.insert(
              configDao.load("core.instance"),
              "core.rule.miss.instance",
              Constant.LOG_LEVEL_VERB,
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("ruleName", rule.getName())
                  .put("ruleId", rule.getId())
                  .put("logId", log.getId())
                  .toString());
          break;
        case Rule.MISS_CATEGORY:
          logInsertDao.insert(
              configDao.load("core.instance"),
              "core.rule.miss.category",
              Constant.LOG_LEVEL_VERB,
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("ruleName", rule.getName())
                  .put("ruleId", rule.getId())
                  .put("logId", log.getId())
                  .toString());
          break;
        case Rule.MISS_CONTENT_FORMAT:
          // log
          logInsertDao.insert(
              configDao.load("core.instance"),
              "core.rule.miss.content.format",
              Constant.LOG_LEVEL_WARN,
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("ruleName", rule.getName())
                  .put("ruleId", rule.getId())
                  .put("logId", log.getId())
                  .toString());
          break;
        default:
          logInsertDao.insert(
              configDao.load("core.instance"),
              "core.rule.miss.content",
              Constant.LOG_LEVEL_VERB,
              ObjectMapperSingleton.getInstance()
                  .getNodeFactory()
                  .objectNode()
                  .put("ruleName", rule.getName())
                  .put("ruleId", rule.getId())
                  .put("logId", log.getId())
                  .put("conditionIndex", match)
                  .toString());
          break;
      }
      if (breakFor) {
        break;
      }
    }
    // UNLOCK
    lock.readDecrease();
  }

  @Override
  public void disableRuleByConnectorId(Long connectorId) {
    // check `thenUseConnectorId` and ignore `ifLogInstanceEqual`
    boolean cache = false;
    for (Rule rule : ruleList) {
      if (rule.getThenUseConnectorId().equals(connectorId) && rule.getEnable()) {
        RulePo po = new RulePo();
        ruleToPo(rule, po);
        po.setEnable(false);
        repository.save(po);
        cache = true;
      }
    }
    if (cache) {
      refreshCache();
      // log
      logInsertDao.insert(
          configDao.load("core.instance"), "core.rule.auto-disable", Constant.LOG_LEVEL_INFO, "");
    }
  }

  @Override
  public void fix() {
    // check `thenUseConnectorId` and ignore `ifLogInstanceEqual`
    boolean cache = false;
    for (Rule rule : ruleList) {
      if (!rule.getEnable()) {
        continue;
      }
      if (connectorDao.notExistent(rule.getThenUseConnectorId())) {
        // disable rules with invalid connector id
        RulePo po = new RulePo();
        ruleToPo(rule, po);
        po.setEnable(false);
        repository.save(po);
        cache = true;
      } else {
        try {
          new URI(connectorDao.getUrlById(rule.getThenUseConnectorId()) + rule.getThenUseUrlPath());
        } catch (URISyntaxException e) {
          // disable rules with invalid url
          RulePo po = new RulePo();
          ruleToPo(rule, po);
          po.setEnable(false);
          repository.save(po);
          cache = true;
        }
      }
    }
    if (cache) {
      refreshCache();
      // log
      logInsertDao.insert(
          configDao.load("core.instance"), "core.rule.auto-disable", Constant.LOG_LEVEL_INFO, "");
    }
  }

  @Override
  public void updateCoreInstance(String before, String after) {
    if (after.length() > Constant.INSTANCE_LENGTH) {
      throw new ConfigCoreInstanceException(EXCEPTION_MESSAGE_CORE_INSTANCE);
    }
    if (findOneByIfLogInstanceEqual(after) != null) {
      throw new ConfigCoreInstanceException(EXCEPTION_MESSAGE_CORE_INSTANCE);
    }
    boolean cache = false;
    for (Rule rule : ruleList) {
      if (rule.getIfLogInstanceEqual().equals(before)) {
        RulePo po = new RulePo();
        ruleToPo(rule, po);
        po.setIfLogInstanceEqual(after);
        repository.save(po);
        cache = true;
      }
    }
    if (cache) {
      refreshCache();
      // log
      logInsertDao.insert(
          configDao.load("core.instance"),
          "core.rule.update-core-instance",
          Constant.LOG_LEVEL_INFO,
          ObjectMapperSingleton.getInstance()
              .getNodeFactory()
              .objectNode()
              .put("before", before)
              .put("after", after)
              .toString());
    }
  }

  @SuppressWarnings("Duplicates")
  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    // validate and convert
    RulePo po = new RulePo();
    requestDtoToPo(null, dto, po);
    // cache
    if (find(po.getName()) != null) {
      throw new RuleAlreadyExistentException(
          String.format("rule with name [%s]: already existent", dto.getName()));
    }
    if (dto.getIfLogCategoryEqual().equals(configDao.load("core.instance"))) {
      throw new RuleIfInvalidInstanceException(
          PutPostRequestDto.EXCEPTION_MESSAGE_IF_LOG_INSTANCE_EQUAL);
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
  public GetPutPostDeleteResponseDto delete(Long id) {
    // cache
    Rule rule = find(id);
    if (rule == null) {
      throw new RuleNotFoundException(String.format("rule with id [%d]: not found", id));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    ruleToResponseDto(rule, responseDto);
    // database
    repository.deleteById(id);
    refreshCache();
    //
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto readById(Long id) {
    // cache
    Rule rule = find(id);
    if (rule == null) {
      throw new RuleNotFoundException(String.format("rule with id [%d]: not found", id));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    ruleToResponseDto(rule, responseDto);
    return responseDto;
  }

  @Override
  public GetPutPostDeleteResponseDto readByName(String name) {
    // cache
    Rule rule = find(name);
    if (rule == null) {
      throw new RuleNotFoundException(String.format("rule with name [%s]: not found", name));
    }
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    ruleToResponseDto(rule, responseDto);
    return responseDto;
  }

  @Override
  public GetAllResponseDto readAll() {
    // cache
    List<Rule> list = findAll();
    // response
    GetAllResponseDto responseDto = new GetAllResponseDto();
    Collection<Item> data = responseDto.getData();
    Item item;
    for (Rule rule : list) {
      item = new Item();
      ruleToResponseDtoItem(rule, item);
      data.add(item);
    }
    return responseDto;
  }

  @SuppressWarnings("Duplicates")
  @Override
  public GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto) {
    // convert and validate
    RulePo po = new RulePo();
    requestDtoToPo(id, dto, po);
    // cache
    Rule rule = find(id);
    if (rule == null) {
      throw new RuleNotFoundException(String.format("rule with id [%d]: not found", id));
    }
    if (!rule.getName().equals(po.getName())) {
      rule = find(po.getName());
      if (rule != null) {
        throw new RuleAlreadyExistentException(
            String.format("rule with name [%s]: already existent", po.getName()));
      }
    }
    if (po.getIfLogCategoryEqual().equals(configDao.load("core.instance"))) {
      throw new RuleIfInvalidInstanceException(
          PutPostRequestDto.EXCEPTION_MESSAGE_IF_LOG_INSTANCE_EQUAL);
    }
    // database
    repository.save(po);
    refreshCache();
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

  @SuppressWarnings("BusyWait")
  private Rule find(Long id) {
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
    Rule rule = null;
    for (Rule item : ruleList) {
      if (item.getId().equals(id)) {
        rule = item;
        break;
      }
    }
    // UNLOCK
    lock.readDecrease();
    //
    return rule;
  }

  @SuppressWarnings("BusyWait")
  private Rule find(String name) {
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
    Rule rule = null;
    for (Rule item : ruleList) {
      if (item.getName().equals(name)) {
        rule = item;
        break;
      }
    }
    // UNLOCK
    lock.readDecrease();
    //
    return rule;
  }

  @SuppressWarnings("BusyWait")
  private Rule findOneByIfLogInstanceEqual(String ifLogInstanceEqual) {
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
    Rule rule = null;
    for (Rule item : ruleList) {
      if (item.getIfLogInstanceEqual().equals(ifLogInstanceEqual)) {
        rule = item;
        break;
      }
    }
    // UNLOCK
    lock.readDecrease();
    //
    return rule;
  }

  @SuppressWarnings("BusyWait")
  private List<Rule> findAll() {
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
    List<Rule> list = new ArrayList<>(ruleList);
    // UNLOCK
    lock.readDecrease();
    //
    return list;
  }

  @SuppressWarnings("Duplicates")
  private void poToResponseDto(RulePo po, GetPutPostDeleteResponseDto dto) {
    Item data = dto.getData();
    data.setId(po.getId());
    data.setName(po.getName());
    data.setIfLogInstanceEqual(po.getIfLogInstanceEqual());
    data.setIfLogCategoryEqual(po.getIfLogCategoryEqual());
    data.setIfLogContentSatisfy(po.getIfLogContentSatisfy());
    data.setThenUseConnectorId(po.getThenUseConnectorId());
    data.setThenUseUrlPath(po.getThenUseUrlPath());
    data.setThenUseHeaderContentType(po.getThenUseHeaderContentType());
    data.setThenUseBodyTemplate(po.getThenUseBodyTemplate());
    data.setPriority(po.getPriority());
    data.setTerminate(po.getTerminate());
    data.setEnable(po.getEnable());
    data.setAnnotation(po.getAnnotation());
  }

  @SuppressWarnings("Duplicates")
  private void requestDtoToPo(Long id, PutPostRequestDto dto, RulePo po) {
    // validate `name`
    String name = dto.getName();
    try {
      Long.parseLong(name);
      throw new RuleNameNumericalException(PutPostRequestDto.EXCEPTION_MESSAGE_NAME);
    } catch (NumberFormatException ignored) {
    }
    po.setName(name);
    // validate `thenUseConnectorId` and `enable`
    Long thenUseConnectorId = dto.getThenUseConnectorId();
    boolean enable = dto.getEnable();
    if (connectorDao.notExistent(thenUseConnectorId) && enable) {
      throw new RuleEnableWithInvalidConnectorException(
          String.format(
              "connector with id [%d]: not found => cannot enable rule, please revise connector id or disable rule",
              thenUseConnectorId));
    }
    po.setThenUseConnectorId(thenUseConnectorId);
    po.setEnable(enable);
    // validate `thenUseHeaderContentType` and `thenUseBodyJson`
    String thenUseHeaderContentType = dto.getThenUseHeaderContentType();
    try {
      po.setThenUseBodyJson(
          ContentType.parse(thenUseHeaderContentType).equals(ContentType.APPLICATION_JSON));
    } catch (ParseException | UnsupportedCharsetException e) {
      throw new RuleThenInvalidContentTypeException(
          PutPostRequestDto.EXCEPTION_MESSAGE_THEN_USE_HEADER_CONTENT_TYPE);
    }
    po.setThenUseHeaderContentType(thenUseHeaderContentType);
    // validate `thenUseUrlPath`
    String thenUseUrlPath = dto.getThenUseUrlPath();
    String url = connectorDao.getUrlById(thenUseConnectorId) + thenUseUrlPath;
    try {
      new URI(url);
    } catch (URISyntaxException e) {
      throw new RuleThenInvalidUrlException(
          String.format(
              "path [%s] concatenated as url [%s]: invalid format, please revise rule path or connector url",
              thenUseUrlPath, url));
    }
    po.setThenUseUrlPath(thenUseUrlPath);
    // validate `setIfLogContentSatisfy`
    po.setIfLogContentSatisfy(Condition.validateJsonAsList(dto.getIfLogContentSatisfy()));
    // already validated
    po.setIfLogInstanceEqual(dto.getIfLogInstanceEqual());
    po.setIfLogCategoryEqual(dto.getIfLogCategoryEqual());
    po.setThenUseBodyTemplate(dto.getThenUseBodyTemplate());
    po.setPriority(dto.getPriority());
    po.setTerminate(dto.getTerminate());
    po.setAnnotation(dto.getAnnotation());
    // id
    if (id == null) {
      po.setId(IdGenerator.getInstance().generate());
    } else {
      po.setId(id);
    }
  }

  @SuppressWarnings("Duplicates")
  private void ruleToPo(Rule rule, RulePo po) {
    po.setId(rule.getId());
    po.setName(rule.getName());
    po.setIfLogInstanceEqual(rule.getIfLogInstanceEqual());
    po.setIfLogCategoryEqual(rule.getIfLogCategoryEqual());
    po.setIfLogContentSatisfy(Condition.listToJson(rule.getIfLogContentSatisfy()));
    po.setThenUseConnectorId(rule.getThenUseConnectorId());
    po.setThenUseUrlPath(rule.getThenUseUrlPath());
    po.setThenUseHeaderContentType(rule.getThenUseHeaderContentType());
    po.setThenUseBodyJson(rule.getThenUseBodyJson());
    po.setThenUseBodyTemplate(rule.getThenUseBodyTemplate());
    po.setPriority(rule.getPriority());
    po.setTerminate(rule.getTerminate());
    po.setEnable(rule.getEnable());
    po.setAnnotation(rule.getAnnotation());
  }

  private void ruleToResponseDto(Rule rule, GetPutPostDeleteResponseDto dto) {
    Item data = dto.getData();
    ruleToResponseDtoItem(rule, data);
  }

  @SuppressWarnings("Duplicates")
  private void ruleToResponseDtoItem(Rule rule, Item item) {
    item.setId(rule.getId());
    item.setName(rule.getName());
    item.setIfLogInstanceEqual(rule.getIfLogInstanceEqual());
    item.setIfLogCategoryEqual(rule.getIfLogCategoryEqual());
    item.setIfLogContentSatisfy(Condition.listToJson(rule.getIfLogContentSatisfy()));
    item.setThenUseConnectorId(rule.getThenUseConnectorId());
    item.setThenUseUrlPath(rule.getThenUseUrlPath());
    item.setThenUseHeaderContentType(rule.getThenUseHeaderContentType());
    item.setThenUseBodyTemplate(rule.getThenUseBodyTemplate());
    item.setPriority(rule.getPriority());
    item.setTerminate(rule.getTerminate());
    item.setEnable(rule.getEnable());
    item.setAnnotation(rule.getAnnotation());
  }
}
