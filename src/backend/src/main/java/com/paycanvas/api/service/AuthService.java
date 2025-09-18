package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.CompanyFeature;
import com.paycanvas.api.entity.RefreshToken;
import com.paycanvas.api.entity.UserAccount;
import com.paycanvas.api.model.LoginResponse;
import com.paycanvas.api.model.UserSummary;
import com.paycanvas.api.repository.CompanyFeatureRepository;
import com.paycanvas.api.repository.FeatureRepository;
import com.paycanvas.api.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユーザー認証を担当するサービスクラスです。
 * ログイン、ログアウト、トークン更新の機能を提供し、
 * JWTトークンベースの認証システムを管理します。
 */
@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompanyFeatureRepository companyFeatureRepository;
  private final FeatureRepository featureRepository;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  /**
   * AuthServiceのコンストラクタです。
   *
   * @param userRepository ユーザー情報のリポジトリ
   * @param passwordEncoder パスワードエンコーダー
   * @param companyFeatureRepository 会社機能設定のリポジトリ
   * @param featureRepository 機能マスターのリポジトリ
   * @param jwtService JWTトークン管理サービス
   * @param refreshTokenService リフレッシュトークン管理サービス
   */
  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      CompanyFeatureRepository companyFeatureRepository,
      FeatureRepository featureRepository,
      JwtService jwtService,
      RefreshTokenService refreshTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.companyFeatureRepository = companyFeatureRepository;
    this.featureRepository = featureRepository;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
  }

  /**
   * ユーザーのログイン処理を実行します。
   * メールアドレスとパスワードを検証し、成功時はJWTトークンと
   * リフレッシュトークンを含むレスポンスを返します。
   *
   * @param email ユーザーのメールアドレス
   * @param rawPassword パスワード（平文）
   * @return ログイン成功時の認証情報、失敗時はnull
   */
  @Transactional
  public LoginResponse login(String email, String rawPassword) {
    UserAccount user =
        userRepository
            .findByEmail(email)
            .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
            .orElse(null);
    if (user == null) {
      return null;
    }

    String roleKey =
        user.getRoles().stream()
            .map(userRole -> userRole.getRole().getRoleKey())
            .findFirst()
            .orElse("STAFF");

    List<String> enabledFeatures = resolveFeatures(user, roleKey);

    UserSummary summary = buildSummary(user, roleKey, enabledFeatures);
    JwtService.JwtToken accessToken = jwtService.generateAccessToken(user, roleKey, enabledFeatures);
    RefreshToken refreshToken = refreshTokenService.create(user);

    return new LoginResponse(
        accessToken.token(), refreshToken.getToken(), accessToken.expiresAt(), summary);
  }

  /**
   * リフレッシュトークンを使用してアクセストークンを更新します。
   * 有効なリフレッシュトークンを検証し、新しいアクセストークンと
   * リフレッシュトークンを発行します。
   *
   * @param refreshTokenValue リフレッシュトークンの値
   * @return 新しい認証情報
   * @throws IllegalArgumentException リフレッシュトークンが無効な場合
   */
  @Transactional
  public LoginResponse refresh(String refreshTokenValue) {
    RefreshToken existing = refreshTokenService.validate(refreshTokenValue);
    UserAccount user = existing.getUser();
    String roleKey = jwtService.extractRole(user);
    List<String> enabledFeatures = resolveFeatures(user, roleKey);
    UserSummary summary = buildSummary(user, roleKey, enabledFeatures);

    JwtService.JwtToken accessToken = jwtService.generateAccessToken(user, roleKey, enabledFeatures);
    RefreshToken rotated = refreshTokenService.rotate(existing);

    return new LoginResponse(
        accessToken.token(), rotated.getToken(), accessToken.expiresAt(), summary);
  }

  /**
   * ユーザーの利用可能機能を解決します。
   * SUPER_ADMINの場合は全機能、それ以外は会社に設定された機能のみを返します。
   *
   * @param user ユーザーアカウント
   * @param roleKey ユーザーの役割キー
   * @return 利用可能な機能のキーリスト
   */
  private List<String> resolveFeatures(UserAccount user, String roleKey) {
    if ("SUPER_ADMIN".equals(roleKey)) {
      return featureRepository.findAll().stream()
          .map(feature -> feature.getFeatureKey())
          .collect(Collectors.toList());
    }

    Company company = user.getCompany();
    if (company == null) {
      return List.of();
    }

    List<CompanyFeature> toggles =
        companyFeatureRepository.findByCompany_IdAndEnabledTrue(company.getId());

    return toggles.stream().map(cf -> cf.getFeature().getFeatureKey()).collect(Collectors.toList());
  }

  /**
   * ユーザーサマリー情報を構築します。
   * JWTトークンに含めるユーザーの基本情報を作成します。
   *
   * @param user ユーザーアカウント
   * @param roleKey ユーザーの役割キー
   * @param enabledFeatures 利用可能な機能リスト
   * @return ユーザーサマリー情報
   */
  private UserSummary buildSummary(UserAccount user, String roleKey, List<String> enabledFeatures) {
    Company company = user.getCompany();
    long companyId = company != null ? company.getId() : 0L;
    String companyName = company != null ? company.getName() : "";

    return new UserSummary(
        user.getId().longValue(),
        companyId,
        companyName,
        roleKey,
        enabledFeatures,
        user.getDisplayName());
  }
}
