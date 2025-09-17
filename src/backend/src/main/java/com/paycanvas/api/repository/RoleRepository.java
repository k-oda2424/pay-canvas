package com.paycanvas.api.repository;

import com.paycanvas.api.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByRoleKey(String roleKey);
}
