package org.example.repository;


import org.example.entity.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = "SELECT * FROM projects WHERE " +
            "(CAST(:name AS TEXT) IS NULL OR name LIKE %:name%) AND " +
            "(CAST(:startDate AS TIMESTAMP) IS NULL OR created_at >= :startDate) AND " +
            "(CAST(:endDate AS TIMESTAMP) IS NULL OR created_at <= :endDate)",
            nativeQuery = true)
    List<Project> findWithFiltersNative(
            @Param("name") String name,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}