package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_features")
public class Feature {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "feature_key", nullable = false, unique = true)
  private String featureKey;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  public Integer getId() {
    return id;
  }

  public String getFeatureKey() {
    return featureKey;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
