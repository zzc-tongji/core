package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.RulePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleJpaRepository extends JpaRepository<RulePo, Long> {}
