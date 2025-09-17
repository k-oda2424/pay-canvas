package com.paycanvas.api.service;

import com.paycanvas.api.entity.RefreshToken;
import com.paycanvas.api.entity.UserAccount;
import com.paycanvas.api.repository.RefreshTokenRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final Duration refreshTokenDuration;

  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      @Value("${security.jwt.refresh-expiration-days:14}") long refreshDays) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.refreshTokenDuration = Duration.ofDays(refreshDays);
  }

  @Transactional
  public RefreshToken create(UserAccount user) {
    refreshTokenRepository.deleteByUser_Id(user.getId());
    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setCreatedAt(Instant.now());
    token.setExpiresAt(Instant.now().plus(refreshTokenDuration));
    return refreshTokenRepository.save(token);
  }

  @Transactional(readOnly = true)
  public RefreshToken validate(String tokenValue) {
    RefreshToken token =
        refreshTokenRepository
            .findByToken(tokenValue)
            .orElseThrow(() -> new IllegalArgumentException("無効なリフレッシュトークンです"));
    if (token.getExpiresAt().isBefore(Instant.now())) {
      throw new IllegalArgumentException("リフレッシュトークンの有効期限が切れています");
    }
    return token;
  }

  @Transactional
  public RefreshToken rotate(RefreshToken existing) {
    refreshTokenRepository.delete(existing);
    return create(existing.getUser());
  }
}
