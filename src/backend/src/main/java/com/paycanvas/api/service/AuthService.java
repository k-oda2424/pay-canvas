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

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompanyFeatureRepository companyFeatureRepository;
  private final FeatureRepository featureRepository;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

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
