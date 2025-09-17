package com.paycanvas.api.repository;

import com.paycanvas.api.entity.Feature;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {
  Optional<Feature> findByFeatureKey(String featureKey);
}
