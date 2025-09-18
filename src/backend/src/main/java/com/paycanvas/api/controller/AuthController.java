package com.paycanvas.api.controller;

import com.paycanvas.api.model.LoginRequest;
import com.paycanvas.api.model.LoginResponse;
import com.paycanvas.api.model.RefreshTokenRequest;
import com.paycanvas.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証管理コントローラー
 *
 * <p>ユーザーのログインとトークンの更新を行うRESTコントローラーです。
 * JWT認証による認証機能を提供します。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  /**
   * コンストラクタ
   *
   * @param authService 認証サービス
   */
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * ユーザーログイン
   *
   * <p>メールアドレスとパスワードによる認証を行い、成功時にJWTトークンを発行します。
   * 認証に失敗した場合は401エラーを返却します。</p>
   *
   * @param request ログインリクエスト（メールアドレス、パスワード）
   * @return ログイン成功レスポンス（JWTトークン含む）、または401エラー
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request.email(), request.password());
    if (response == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(response);
  }

  /**
   * アクセストークンの更新
   *
   * <p>リフレッシュトークンを使用してアクセストークンを更新します。
   * 無効なリフレッシュトークンの場合は401エラーを返却します。</p>
   *
   * @param request リフレッシュトークンリクエスト
   * @return 新しいアクセストークンを含むレスポンス、または401エラー
   */
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    try {
      LoginResponse response = authService.refresh(request.refreshToken());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(401).build();
    }
  }
}
