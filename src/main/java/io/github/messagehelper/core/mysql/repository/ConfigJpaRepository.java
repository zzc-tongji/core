package io.github.messagehelper.core.mysql.repository;

import io.github.messagehelper.core.mysql.po.ConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigJpaRepository extends JpaRepository<ConfigPo, String> {}
