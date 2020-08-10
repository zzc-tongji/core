package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.TokenPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

public interface TokenJpaRepository extends JpaRepository<TokenPo, String> {
  @Async
  @Modifying
  @Query(
      value =
          "INSERT INTO token (token, expired_timestamp_ms) VALUES (?1, ?2) ON DUPLICATE KEY UPDATE expired_timestamp_ms=?2",
      nativeQuery = true)
  void saveAsync(String token, Long expiredTimestampMs);
}
