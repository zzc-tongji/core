package io.github.messagehelper.core.dao.implement;

import io.github.messagehelper.core.dao.ConfigDao;
import io.github.messagehelper.core.dao.TokenDao;
import io.github.messagehelper.core.dto.api.login.Item;
import io.github.messagehelper.core.dto.api.login.PostRequestDto;
import io.github.messagehelper.core.dto.api.login.PostResponseDto;
import io.github.messagehelper.core.exception.PasswordAlreadySetException;
import io.github.messagehelper.core.exception.PasswordInvalidException;
import io.github.messagehelper.core.exception.PasswordNotSetException;
import io.github.messagehelper.core.exception.TokenInvalidException;
import io.github.messagehelper.core.mysql.po.TokenPo;
import io.github.messagehelper.core.mysql.repository.TokenJpaRepository;
import io.github.messagehelper.core.utils.IdGenerator;
import io.github.messagehelper.core.utils.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenJpaLocalDao implements TokenDao {
  private TokenJpaRepository repository;
  private ConfigDao configDao;
  private Map<String, TokenPo> tokenMap;
  private final Lock lock;
  private final Long lifetimeMs;
  private final MessageDigest messageDigest;

  public TokenJpaLocalDao(
      @Autowired TokenJpaRepository repository, @Autowired ConfigDao configDao) {
    this.repository = repository;
    this.configDao = configDao;
    tokenMap = new HashMap<>();
    lock = new Lock();
    lifetimeMs = 86400000L; // 1 day
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    // cache
    refreshCache();
    // clear expired token periodically
    (new Thread(
            () -> {
              List<TokenPo> expiredList = new ArrayList<>();
              while (true) {
                // execute periodically
                try {
                  Thread.sleep(lifetimeMs);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                // get all expired tokens
                expiredList.clear();
                long expiredTimestampMs;
                long delta;
                for (TokenPo po : tokenMap.values()) {
                  expiredTimestampMs = po.getExpiredTimestampMs();
                  if (expiredTimestampMs <= 0) {
                    // permanent token
                    // => skip
                    continue;
                  }
                  delta = expiredTimestampMs - System.currentTimeMillis();
                  if (delta < 0) {
                    // expired
                    // => add
                    expiredList.add(po);
                  }
                }
                // remove them
                if (!expiredList.isEmpty()) {
                  repository.deleteAll(expiredList);
                  refreshCache();
                }
              }
            }))
        .start();
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
      tokenMap.clear();
      List<TokenPo> data = repository.findAll();
      for (TokenPo po : data) {
        tokenMap.put(po.getToken(), po);
      }
      // UNLOCK
      lock.writeDecrease();
      //
    }
  }

  @Override
  public void authenticate(String token) {
    // cache
    TokenPo po = find(token);
    if (po == null) {
      throw new TokenInvalidException("token: not valid");
    }
    long expiredTimestampMs = po.getExpiredTimestampMs();
    if (expiredTimestampMs <= 0) {
      // permanent token
      // => succeed
      return;
    }
    long delta = expiredTimestampMs - System.currentTimeMillis();
    if (delta < 0) {
      // expired now
      // => fail
      throw new TokenInvalidException("token: expired");
    }
    if (delta < (lifetimeMs >> 4)) {
      // expired in 1.5 hours
      // => extend, succeed
      expiredTimestampMs = System.currentTimeMillis() + lifetimeMs;
      // database
      TokenPo updatedPo = new TokenPo();
      updatedPo.setToken(token);
      updatedPo.setExpiredTimestampMs(expiredTimestampMs);
      repository.save(updatedPo);
      refreshCache();
    }
  }

  @Override
  public PostResponseDto login(PostRequestDto dto) {
    // cache
    if (configDao.load("core.backend.password").length() <= 0
        || configDao.load("core.backend.salt").length() <= 0) {
      throw new PasswordNotSetException("not registered, please register first");
    }
    if (!cipher(dto.getPassword(), configDao.load("core.backend.salt"))
        .equals(configDao.load("core.backend.password"))) {
      throw new PasswordInvalidException("password: invalid");
    }
    String token = String.format("token%d", IdGenerator.getInstance().generateNegative());
    Long expiredTimestampMs = System.currentTimeMillis() + lifetimeMs;
    // database
    TokenPo po = new TokenPo();
    po.setToken(token);
    po.setExpiredTimestampMs(expiredTimestampMs);
    repository.save(po);
    refreshCache();
    // response
    PostResponseDto responseDto = new PostResponseDto();
    Item data = responseDto.getData();
    data.setToken(token);
    data.setExpiredTimestampMs(expiredTimestampMs);
    return responseDto;
  }

  @Override
  public void register(io.github.messagehelper.core.dto.api.register.PostRequestDto dto) {
    // cache
    if (configDao.load("core.backend.password").length() > 0
        && configDao.load("core.backend.salt").length() > 0) {
      throw new PasswordAlreadySetException("already registered, please login instead");
    }
    // database
    String salt = String.format("salt%d", IdGenerator.getInstance().generateNegative());
    configDao.save("core.backend.password", cipher(dto.getPassword(), salt));
    configDao.save("core.backend.salt", salt);
  }

  private TokenPo find(String key) {
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
    TokenPo po = tokenMap.get(key);
    // UNLOCK
    lock.readDecrease();
    //
    return po;
  }

  private String cipher(String password, String salt) {
    byte[] array = messageDigest.digest(String.format("%s%s", password, salt).getBytes());
    StringBuilder builder = new StringBuilder();
    for (byte b : array) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }
}