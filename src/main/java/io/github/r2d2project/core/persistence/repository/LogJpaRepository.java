package io.github.r2d2project.core.persistence.repository;

import io.github.r2d2project.core.persistence.po.LogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogJpaRepository
    extends JpaRepository<LogPo, Long>, JpaSpecificationExecutor<LogPo> {}
