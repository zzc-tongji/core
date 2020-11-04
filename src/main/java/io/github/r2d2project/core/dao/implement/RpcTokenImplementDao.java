package io.github.r2d2project.core.dao.implement;

import io.github.r2d2project.core.dao.ConfigDao;
import io.github.r2d2project.core.dao.RpcTokenDao;
import io.github.r2d2project.core.exception.type.RpcTokenInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RpcTokenImplementDao implements RpcTokenDao {
  private final ConfigDao configDao;

  public RpcTokenImplementDao(@Autowired ConfigDao configDao) {
    this.configDao = configDao;
  }

  @Override
  public void authenticate(String token) {
    if (!configDao.load("core.rpc-token").equals(token)) {
      throw new RpcTokenInvalidException("rpc token: not valid");
    }
  }
}
