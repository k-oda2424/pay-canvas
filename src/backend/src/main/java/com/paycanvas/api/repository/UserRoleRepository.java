package com.paycanvas.api.repository;

import com.paycanvas.api.entity.UserRole;
import com.paycanvas.api.entity.UserRoleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
  @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.user.id = :userId")
  List<UserRole> findByUserIdWithRole(@Param("userId") Integer userId);
}
