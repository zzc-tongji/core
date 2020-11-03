package io.github.r2d2project.core.storage.repository;

import io.github.r2d2project.core.storage.po.TokenPo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenJpaRepository extends JpaRepository<TokenPo, String> {}
