package io.github.messagehelper.core.dao.impl;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.ConnectorDao;
import io.github.messagehelper.core.dao.LogDao;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.mysql.Constant;
import io.github.messagehelper.core.mysql.po.ConnectorPo;
import io.github.messagehelper.core.mysql.po.LogPo;
import io.github.messagehelper.core.mysql.repository.ConnectorJpaRepository;
import io.github.messagehelper.core.rule.then.BodyTemplate;
import io.github.messagehelper.core.rule.then.RuleThen;
import io.github.messagehelper.core.utils.ErrorJsonGenerator;
import io.github.messagehelper.core.utils.HttpClientSingleton;
import io.github.messagehelper.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConnectorJpaLocalDao implements ConnectorDao {
  private ConnectorJpaRepository repository;
  private ConfigDao configDao;
  private LogDao logDao;
  private Map<String, ConnectorPo> connectorMap;
  private final Lock lock;

  public ConnectorJpaLocalDao(
      @Autowired ConnectorJpaRepository repository,
      @Autowired ConfigDao configDao,
      @Autowired @Qualifier("LogJpaAsyncDao") LogDao logDao) {
    this.repository = repository;
    this.configDao = configDao;
    this.logDao = logDao;
    connectorMap = new HashMap<>();
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
          logException(e);
          return;
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
  public void execute(RuleThen ruleThen) {
    helper(ruleThen, ruleThen.getBodyTemplate());
  }

  @Override
  public void execute(RuleThen ruleThen, Log log) {
    helper(ruleThen, BodyTemplate.parse(ruleThen.getBodyTemplate(), log));
  }

  private void helper(RuleThen ruleThen, String requestBody) {
    String instance = ruleThen.getInstance();
    // CHECK
    while (lock.isWriteLocked()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        logException(e);
        return;
      }
    }
    // LOCK
    lock.readIncrease();
    // DO
    ConnectorPo po = connectorMap.get(instance);
    // UNLOCK
    lock.readDecrease();
    //
    if (po == null) {
      logDao.insert(
          new LogPo(
              configDao.load("processor.instance"),
              Constant.LOG_ERR,
              "process.dao.impl.config-jpa-dao.exception",
              ErrorJsonGenerator.getInstance()
                  .generate(String.format("connector instance %s: bot found", instance), "", "")));
      return;
    }
    HttpRequest request;
    String url = po.getUrl() + ruleThen.getPath();
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(url))
              .headers("content-type", "application/json;charset=utf-8")
              .POST(HttpRequest.BodyPublishers.ofString(requestBody))
              .build();
    } catch (URISyntaxException e) {
      logException(e);
      return;
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      logException(e);
      return;
    }
    int statusCode = response.statusCode();
    if (statusCode >= 400) {
      logDao.insert(
          new LogPo(
              configDao.load("processor.instance"),
              Constant.LOG_ERR,
              "process.dao.impl.config-jpa-dao.exception",
              ErrorJsonGenerator.getInstance()
                  .generate(
                      String.format(
                          "fetch \"%s\" (\"%s\" as \"%s\"): %d => %s",
                          url, instance, po.getCategory(), statusCode, response.body()),
                      "",
                      "")));
    }
  }

  private void logException(Exception e) {
    logDao.insert(
        new LogPo(
            configDao.load("processor.instance"),
            Constant.LOG_ERR,
            "process.dao.impl.config-jpa-dao.exception",
            ErrorJsonGenerator.getInstance().generate(e)));
  }
}
