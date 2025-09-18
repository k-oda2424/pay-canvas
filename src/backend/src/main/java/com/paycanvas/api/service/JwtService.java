package com.paycanvas.api.service;

import com.paycanvas.api.entity.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JWT（JSON Web Token）トークンの生成と検証を担当するサービスクラスです。
 * アクセストークンの生成、解析、署名検証の機能を提供します。
 */
@Service
public class JwtService {
  private final Key signingKey;
  private final Duration accessTokenDuration;

  /**
   * JWTトークンとその有効期限を表すレコードです。
   *
   * @param token JWTトークン文字列
   * @param expiresAt トークンの有効期限
   */
  public record JwtToken(String token, Instant expiresAt) {}

  /**
   * JwtServiceのコンストラクタです。
   *
   * @param secret JWT署名用の秘密鍵（Base64エンコード済み）
   * @param expirationMinutes アクセストークンの有効期限（分）
   */
  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ensureSecret(secret)));
    this.accessTokenDuration = Duration.ofMinutes(expirationMinutes);
  }

  /**
   * アクセストークンを生成します。
   * ユーザー情報、役割、利用可能機能をクレームとして含むJWTを作成します。
   *
   * @param user ユーザーアカウント
   * @param roleKey ユーザーの役割キー
   * @param enabledFeatures 利用可能な機能リスト
   * @return 生成されたJWTトークンと有効期限
   */
  public JwtToken generateAccessToken(
      UserAccount user, String roleKey, List<String> enabledFeatures) {
    Instant now = Instant.now();
    Instant expiry = now.plus(accessTokenDuration);
    Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId()));
    if (user.getCompany() != null) {
      claims.put("companyId", user.getCompany().getId());
    }
    claims.put("role", roleKey);
    claims.put("enabledFeatures", enabledFeatures);

    Date issuedAt = Date.from(now);
    Date expiration = Date.from(expiry);

    String token =
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();

    return new JwtToken(token, expiry);
  }

  /**
   * JWTトークンを解析してクレーム情報を取得します。
   * 署名の検証も同時に行います。
   *
   * @param token 解析対象のJWTトークン
   * @return トークンに含まれるクレーム情報
   * @throws io.jsonwebtoken.JwtException トークンが無効な場合
   */
  public Claims parseToken(String token) {
    Jws<Claims> jws =
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
    return jws.getBody();
  }

  /**
   * ユーザーアカウントから役割キーを抽出します。
   * 複数の役割が設定されている場合は最初の役割を返し、
   * 役割が設定されていない場合は"STAFF"を返します。
   *
   * @param user ユーザーアカウント
   * @return ユーザーの役割キー
   */
  public String extractRole(UserAccount user) {
    return user.getRoles().stream()
        .map(userRole -> userRole.getRole().getRoleKey())
        .findFirst()
        .orElse("STAFF");
  }

  /**
   * JWT秘密鍵の妥当性を確保し、適切な形式に変換します。
   * 鍵の長さが不足している場合は補完し、Base64エンコードを行います。
   *
   * @param secret 設定ファイルから取得した秘密鍵
   * @return Base64エンコードされた秘密鍵
   * @throws IllegalStateException 秘密鍵が未設定の場合
   */
  private String ensureSecret(String secret) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException("JWTシークレットが設定されていません");
    }
    // Ensure the secret is at least 256 bits (32 bytes) for HS256
    if (secret.length() < 32) {
      secret = secret + "0".repeat(32 - secret.length());
    }
    // Convert raw string to base64 bytes for HMAC key
    return io.jsonwebtoken.io.Encoders.BASE64.encode(secret.getBytes());
  }
}
