package com.paycanvas.api.controller;

import com.paycanvas.api.model.LoginRequest;
import com.paycanvas.api.model.LoginResponse;
import com.paycanvas.api.model.RefreshTokenRequest;
import com.paycanvas.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request.email(), request.password());
    if (response == null) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
    try {
      LoginResponse response = authService.refresh(request.refreshToken());
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(401).build();
    }
  }
}
