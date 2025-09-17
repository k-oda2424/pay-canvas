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

@Service
public class JwtService {
  private final Key signingKey;
  private final Duration accessTokenDuration;

  public record JwtToken(String token, Instant expiresAt) {}

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(ensureSecret(secret)));
    this.accessTokenDuration = Duration.ofMinutes(expirationMinutes);
  }

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

  public Claims parseToken(String token) {
    Jws<Claims> jws =
        Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
    return jws.getBody();
  }

  public String extractRole(UserAccount user) {
    return user.getRoles().stream()
        .map(userRole -> userRole.getRole().getRoleKey())
        .findFirst()
        .orElse("STAFF");
  }

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
