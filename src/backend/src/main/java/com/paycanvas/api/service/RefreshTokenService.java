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

/**
 * リフレッシュトークンの管理を担当するサービスクラスです。
 * リフレッシュトークンの生成、検証、ローテーション機能を提供し、
 * JWTアクセストークンの更新をサポートします。
 */
@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final Duration refreshTokenDuration;

  /**
   * RefreshTokenServiceのコンストラクタです。
   *
   * @param refreshTokenRepository リフレッシュトークンのリポジトリ
   * @param refreshDays リフレッシュトークンの有効期限（日数）
   */
  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository,
      @Value("${security.jwt.refresh-expiration-days:14}") long refreshDays) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.refreshTokenDuration = Duration.ofDays(refreshDays);
  }

  /**
   * 指定されたユーザーに対してリフレッシュトークンを新規作成します。
   * 既存のリフレッシュトークンがある場合は削除してから新しいトークンを作成します。
   *
   * @param user リフレッシュトークンを作成するユーザー
   * @return 作成されたリフレッシュトークン
   */
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

  /**
   * リフレッシュトークンの有効性を検証します。
   * トークンの存在確認と有効期限のチェックを行います。
   *
   * @param tokenValue 検証するリフレッシュトークンの値
   * @return 有効なリフレッシュトークン
   * @throws IllegalArgumentException トークンが無効または期限切れの場合
   */
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

  /**
   * 既存のリフレッシュトークンをローテーションします。
   * 古いトークンを削除し、同じユーザーに対して新しいトークンを作成します。
   *
   * @param existing ローテーション対象の既存リフレッシュトークン
   * @return 新しく作成されたリフレッシュトークン
   */
  @Transactional
  public RefreshToken rotate(RefreshToken existing) {
    refreshTokenRepository.delete(existing);
    return create(existing.getUser());
  }
}
