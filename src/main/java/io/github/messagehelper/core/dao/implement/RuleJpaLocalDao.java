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
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.utils.IdGenerator;
import io.github.messagehelper.core.utils.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class RuleJpaLocalDao implements RuleDao {
  private RuleJpaRepository repository;
  private ConfigDao configDao;
  private ConnectorDao connectorDao;
  private LogInsertDao logInsertDao;
  private List<Rule> ruleList;
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
      if (rule.getRuleIf().satisfy(log)) {
        // log
        logInsertDao.insert(
            configDao.load("core.instance"),
            Constant.LOG_LEVEL_INFO,
            "core.dao.rule-dao.process.hit",
            String.format("{\"ruleName\":\"%s\",\"logId\":%d}", rule.getName(), log.getId()));
        // execute rule
        connectorDao.executeRule(rule, log);
        // terminate or not
        if (rule.getTerminate()) {
          break;
        }
      }
    }
    // UNLOCK
    lock.readDecrease();
  }

  @Override
  public GetPutPostDeleteResponseDto create(PutPostRequestDto dto) {
    validateName(dto.getName());
    // cache
    Rule rule = find(dto.getName());
    if (rule != null) {
      throw new RuleAlreadyExistentException(
          String.format("rule with name [%s]: already existent", dto.getName()));
    }
    // database
    RulePo po = new RulePo();
    requestDtoToPo(IdGenerator.getInstance().generate(), dto, po);
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

  @Override
  public GetPutPostDeleteResponseDto update(Long id, PutPostRequestDto dto) {
    validateName(dto.getName());
    // cache
    Rule rule = find(id);
    if (rule == null) {
      throw new RuleNotFoundException(String.format("rule with id [%d]: not found", id));
    }
    if (!rule.getName().equals(dto.getName())) {
      rule = find(dto.getName());
      if (rule != null) {
        throw new RuleAlreadyExistentException(
            String.format("rule with name [%s]: already existent", dto.getName()));
      }
    }
    // database
    RulePo po = new RulePo();
    requestDtoToPo(id, dto, po);
    repository.save(po);
    refreshCache();
    // response
    GetPutPostDeleteResponseDto responseDto = new GetPutPostDeleteResponseDto();
    poToResponseDto(po, responseDto);
    return responseDto;
  }

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
    data.setRuleIf(po.getRuleIf());
    data.setRuleThenInstance(po.getRuleThenInstance());
    data.setRuleThenMethod(po.getRuleThenMethod());
    data.setRuleThenPath(po.getRuleThenPath());
    data.setBodyTemplate(po.getBodyTemplate());
    data.setPriority(po.getPriority());
    data.setTerminate(po.getTerminate());
    data.setEnable(po.getEnable());
  }

  @SuppressWarnings("Duplicates")
  private void requestDtoToPo(Long id, PutPostRequestDto dto, RulePo po) {
    po.setId(id);
    po.setName(dto.getName());
    RuleIf.parse(dto.getRuleIf()); // validate
    po.setRuleIf(dto.getRuleIf());
    po.setRuleThenInstance(dto.getRuleThenInstance());
    po.setRuleThenMethod(dto.getRuleThenMethod());
    po.setRuleThenPath(dto.getRuleThenPath());
    po.setBodyTemplate(dto.getBodyTemplate());
    po.setPriority(dto.getPriority());
    po.setTerminate(dto.getTerminate());
    po.setEnable(dto.getEnable());
  }

  private void ruleToResponseDto(Rule rule, GetPutPostDeleteResponseDto dto) {
    Item data = dto.getData();
    ruleToResponseDtoItem(rule, data);
  }

  @SuppressWarnings("Duplicates")
  private void ruleToResponseDtoItem(Rule rule, Item item) {
    item.setId(rule.getId());
    item.setName(rule.getName());
    item.setRuleIf(rule.getRuleIf().toString());
    item.setRuleThenInstance(rule.getRuleThenInstance());
    item.setRuleThenMethod(rule.getRuleThenMethod());
    item.setRuleThenPath(rule.getRuleThenPath());
    item.setBodyTemplate(rule.getBodyTemplate());
    item.setPriority(rule.getPriority());
    item.setTerminate(rule.getTerminate());
    item.setEnable(rule.getEnable());
  }

  public void validateName(String name) {
    try {
      Long.parseLong(name);
      throw new RuleNameNumericalException(
          "name: required, string with length in [1, "
              + Constant.RULE_NAME_LENGTH
              + "] which cannot be converted to long");
    } catch (NumberFormatException ignored) {
    }
  }
}
