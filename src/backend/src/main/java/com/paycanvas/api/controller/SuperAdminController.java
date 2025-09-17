package com.paycanvas.api.controller;

import com.paycanvas.api.model.superadmin.AdminUserRequest;
import com.paycanvas.api.model.superadmin.AdminUserResponse;
import com.paycanvas.api.model.superadmin.CompanySummaryResponse;
import com.paycanvas.api.service.SuperAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super")
public class SuperAdminController {
  private final SuperAdminService superAdminService;

  public SuperAdminController(SuperAdminService superAdminService) {
    this.superAdminService = superAdminService;
  }

  @GetMapping("/companies")
  public List<CompanySummaryResponse> listCompanies() {
    return superAdminService.listCompanies();
  }

  @PostMapping("/users")
  public ResponseEntity<AdminUserResponse> createCompanyAdmin(@Valid @RequestBody AdminUserRequest request) {
    AdminUserResponse response = superAdminService.createCompanyAdmin(request);
    return ResponseEntity.status(201).body(response);
  }
}
