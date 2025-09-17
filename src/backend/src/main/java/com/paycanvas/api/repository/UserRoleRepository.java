package com.paycanvas.api.repository;

import com.paycanvas.api.entity.UserRole;
import com.paycanvas.api.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {}
