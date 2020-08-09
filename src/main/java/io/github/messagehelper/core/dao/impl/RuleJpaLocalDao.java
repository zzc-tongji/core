package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.dao.RuleDao;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.exception.InvalidRuleThenException;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.mysql.repository.RuleJpaRepository;
import io.github.messagehelper.core.rule.Rule;
import io.github.messagehelper.core.utils.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RuleJpaLocalDao implements RuleDao {
  private final Logger logger = LoggerFactory.getLogger(RuleJpaLocalDao.class);

  private RuleJpaRepository repository;
  private ConfigDao configDao;
  private ConnectorDao connectorDao;
  private LogDao logDao;
  private List<Rule> ruleList;
  private final Lock lock;

  public RuleJpaLocalDao(
      @Autowired RuleJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired ConnectorDao connectorDao,
      @Autowired @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.repository = repository;
    this.configDao = configDao;
    this.connectorDao = connectorDao;
    this.logDao = logDao;
    ruleList = new ArrayList<>();
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
      ruleList.clear();
      List<RulePo> data = repository.findAll();
      for (RulePo item : data) {
        try {
          ruleList.add(new Rule(item));
        } catch (InvalidRuleIfException | InvalidRuleThenException e) {
          logger.error(String.format("rule with name [%s]: %s", item.getName(), e.toString()));
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
      if (rule.getPriority() <= 0) {
        continue;
      }
      if (rule.getRuleIf().satisfy(log)) {
        logDao.insert(
            new LogPo(
                configDao.load("core.instance"),
                Constant.LOG_INFO,
                "core.dao.impl.rule-jpa-local-dao.process.hit",
                String.format("{\"ruleName\":\"%s\",\"logId\":%d}", rule.getName(), log.getId())));
        connectorDao.execute(rule.getRuleThen(), log);
        if (rule.getTerminate()) {
          break;
        }
      }
    }
    // UNLOCK
    lock.readDecrease();
  }
}
