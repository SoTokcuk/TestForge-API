package org.example.repository;

import org.example.entity.GenerationScope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationScopeRepository extends JpaRepository<GenerationScope, Long> {
}