package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.UserAccount;
import com.paycanvas.api.entity.UserRole;
import com.paycanvas.api.model.superadmin.AdminUserRequest;
import com.paycanvas.api.model.superadmin.AdminUserResponse;
import com.paycanvas.api.model.superadmin.CompanySummaryResponse;
import com.paycanvas.api.repository.CompanyRepository;
import com.paycanvas.api.repository.RoleRepository;
import com.paycanvas.api.repository.UserRepository;
import com.paycanvas.api.repository.UserRoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuperAdminService {
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  public SuperAdminService(
      CompanyRepository companyRepository,
      UserRepository userRepository,
      RoleRepository roleRepository,
      UserRoleRepository userRoleRepository,
      PasswordEncoder passwordEncoder) {
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.userRoleRepository = userRoleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<CompanySummaryResponse> listCompanies() {
    return companyRepository.findAll().stream()
        .map(company -> new CompanySummaryResponse(company.getId().longValue(), company.getName(), company.getStatus()))
        .toList();
  }

  @Transactional
  public AdminUserResponse createCompanyAdmin(AdminUserRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EntityExistsException("既に登録済みのメールアドレスです");
    }

    Company company =
        companyRepository
            .findById(request.companyId().intValue())
            .orElseThrow(() -> new EntityNotFoundException("会社が見つかりません"));

    var role =
        roleRepository
            .findByRoleKey("COMPANY_ADMIN")
            .orElseThrow(() -> new EntityNotFoundException("ロールが見つかりません"));

    UserAccount user = new UserAccount();
    user.setCompany(company);
    user.setEmail(request.email());
    user.setDisplayName(request.displayName());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setStatus("ACTIVE");
    UserAccount saved = userRepository.save(user);

    UserRole userRole = new UserRole();
    userRole.setUser(saved);
    userRole.setRole(role);
    userRoleRepository.save(userRole);

    return new AdminUserResponse(
        saved.getId().longValue(),
        saved.getEmail(),
        saved.getDisplayName(),
        company.getId().longValue(),
        company.getName());
  }
}
