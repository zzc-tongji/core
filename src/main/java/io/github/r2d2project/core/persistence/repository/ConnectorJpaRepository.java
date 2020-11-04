package io.github.r2d2project.core.persistence.repository;

import io.github.r2d2project.core.persistence.po.ConnectorPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectorJpaRepository extends JpaRepository<ConnectorPo, Long> {}
