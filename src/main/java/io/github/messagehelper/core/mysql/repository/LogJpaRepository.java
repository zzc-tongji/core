package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.LogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogJpaRepository extends JpaRepository<LogPo, Long> {}
