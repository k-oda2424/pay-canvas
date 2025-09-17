package com.paycanvas.api.security;

import com.paycanvas.api.entity.UserAccount;
import com.paycanvas.api.service.JwtService;
import com.paycanvas.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public UserPrincipalDetailsService(UserRepository userRepository, JwtService jwtService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserAccount user =
        userRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが存在しません"));
    String role = jwtService.extractRole(user);
    return new UserPrincipal(user, role);
  }
}
