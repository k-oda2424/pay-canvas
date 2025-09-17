package com.paycanvas.api.security;

import com.paycanvas.api.entity.UserAccount;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
  private final Integer id;
  private final String email;
  private final String password;
  private final String roleKey;

  public UserPrincipal(UserAccount user, String roleKey) {
    this.id = user.getId();
    this.email = user.getEmail();
    this.password = user.getPasswordHash();
    this.roleKey = roleKey;
  }

  public Integer getId() {
    return id;
  }

  public String getRoleKey() {
    return roleKey;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + roleKey));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
