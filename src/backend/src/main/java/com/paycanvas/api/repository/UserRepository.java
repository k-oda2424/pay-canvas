package com.paycanvas.api.repository;

import com.paycanvas.api.entity.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Integer> {
  @EntityGraph(attributePaths = {"roles", "roles.role", "company"})
  Optional<UserAccount> findByEmail(String email);
}
