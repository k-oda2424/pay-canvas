package com.paycanvas.api.repository;

import com.paycanvas.api.entity.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByUser_Id(Integer userId);

  void deleteByExpiresAtBefore(Instant instant);
}
