package org.example.repository;

import org.example.entity.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByEmail(String email);
    Optional<MyUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}