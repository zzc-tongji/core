package io.github.r2d2project.core.storage.repository;

import io.github.r2d2project.core.storage.po.ConnectorPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectorJpaRepository extends JpaRepository<ConnectorPo, Long> {}
