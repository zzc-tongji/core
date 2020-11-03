package io.github.r2d2project.core.storage.repository;

import io.github.r2d2project.core.storage.po.ConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigJpaRepository extends JpaRepository<ConfigPo, String> {}
