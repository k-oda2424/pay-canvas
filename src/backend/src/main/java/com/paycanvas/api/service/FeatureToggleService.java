package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.CompanyFeature;
import com.paycanvas.api.entity.Feature;
import com.paycanvas.api.model.FeatureToggle;
import com.paycanvas.api.repository.CompanyFeatureRepository;
import com.paycanvas.api.repository.CompanyRepository;
import com.paycanvas.api.repository.FeatureRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeatureToggleService {
  private static final int DEFAULT_COMPANY_ID = 1;

  private final FeatureRepository featureRepository;
  private final CompanyFeatureRepository companyFeatureRepository;
  private final CompanyRepository companyRepository;

  public FeatureToggleService(
      FeatureRepository featureRepository,
      CompanyFeatureRepository companyFeatureRepository,
      CompanyRepository companyRepository) {
    this.featureRepository = featureRepository;
    this.companyFeatureRepository = companyFeatureRepository;
    this.companyRepository = companyRepository;
  }

  @Transactional(readOnly = true)
  public List<FeatureToggle> listFeatureToggles() {
    return featureRepository.findAll().stream()
        .map(
            feature -> {
              long enabledTenants =
                  companyFeatureRepository.countByFeature_FeatureKeyAndEnabledTrue(
                      feature.getFeatureKey());
              boolean enabledForDefaultCompany =
                  companyFeatureRepository
                      .findByCompany_IdAndFeature_FeatureKey(DEFAULT_COMPANY_ID, feature.getFeatureKey())
                      .map(CompanyFeature::isEnabled)
                      .orElse(false);
              return new FeatureToggle(
                  feature.getFeatureKey(),
                  feature.getName(),
                  feature.getDescription(),
                  (int) enabledTenants,
                  enabledForDefaultCompany);
            })
        .toList();
  }

  @Transactional
  public FeatureToggle updateFeatureToggle(String featureKey, boolean isEnabled) {
    Feature feature =
        featureRepository
            .findByFeatureKey(featureKey)
            .orElseThrow(() -> new IllegalArgumentException("機能が見つかりません: " + featureKey));
    Company company =
        companyRepository
            .findById(DEFAULT_COMPANY_ID)
            .orElseThrow(() -> new IllegalStateException("デフォルトの会社情報が存在しません"));

    CompanyFeature companyFeature =
        companyFeatureRepository
            .findByCompany_IdAndFeature_FeatureKey(DEFAULT_COMPANY_ID, featureKey)
            .orElseGet(
                () -> {
                  CompanyFeature entity = new CompanyFeature();
                  return entity;
                });

    companyFeature.setCompany(company);
    companyFeature.setFeature(feature);
    companyFeature.setEnabled(isEnabled);

    companyFeatureRepository.save(companyFeature);

    long enabledTenants =
        companyFeatureRepository.countByFeature_FeatureKeyAndEnabledTrue(feature.getFeatureKey());

    return new FeatureToggle(
        feature.getFeatureKey(),
        feature.getName(),
        feature.getDescription(),
        (int) enabledTenants,
        isEnabled);
  }
}
