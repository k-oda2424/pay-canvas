package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "role_key", nullable = false, unique = true)
  private String roleKey;

  @Column(nullable = false)
  private String name;

  public Integer getId() {
    return id;
  }

  public String getRoleKey() {
    return roleKey;
  }

  public String getName() {
    return name;
  }
}
