package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_user_roles")
public class UserRole {
  @EmbeddedId private UserRoleId id = new UserRoleId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private UserAccount user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("roleId")
  @JoinColumn(name = "role_id")
  private Role role;

  public UserRoleId getId() {
    return id;
  }

  public UserAccount getUser() {
    return user;
  }

  public Role getRole() {
    return role;
  }

  public void setUser(UserAccount user) {
    this.user = user;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
