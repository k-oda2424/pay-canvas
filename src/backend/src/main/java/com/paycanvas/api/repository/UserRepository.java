package com.paycanvas.api.repository;

import com.paycanvas.api.entity.UserAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserAccount, Integer> {
  @Query("SELECT u FROM UserAccount u LEFT JOIN FETCH u.company WHERE u.email = :email")
  Optional<UserAccount> findByEmail(@Param("email") String email);
}
