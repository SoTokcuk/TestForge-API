package org.example.repository;

import org.example.entity.GenerationScope;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface GenerationScopeRepository extends JpaRepository<GenerationScope, Long> {
    List<GenerationScope> findByProjectId(Long projectId);
}