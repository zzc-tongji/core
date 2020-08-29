package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.TokenPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<TokenPo, String> {}
