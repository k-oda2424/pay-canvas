package com.paycanvas.api.repository;

import com.paycanvas.api.entity.CompanyFeature;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyFeatureRepository extends JpaRepository<CompanyFeature, Integer> {
  Optional<CompanyFeature> findByCompany_IdAndFeature_FeatureKey(Integer companyId, String featureKey);

  long countByFeature_FeatureKeyAndEnabledTrue(String featureKey);

  List<CompanyFeature> findByCompany_IdAndEnabledTrue(Integer companyId);
}
