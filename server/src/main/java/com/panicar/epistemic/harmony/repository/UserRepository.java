package com.panicar.epistemic.harmony.repository;

import com.panicar.epistemic.harmony.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);  // Added this
    List<User> findByRole(User.Role role);
    List<User> findByIsActiveTrue();


    boolean existsByEmail(String email);
    boolean existsByUsername(String username);  // Added this
    long countByRole(User.Role role);

    List<User> findByIsActive(Boolean isActive);
}