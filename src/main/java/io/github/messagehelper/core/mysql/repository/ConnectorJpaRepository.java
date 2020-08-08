package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.ConnectorPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectorJpaRepository extends JpaRepository<ConnectorPo, Long> {}
