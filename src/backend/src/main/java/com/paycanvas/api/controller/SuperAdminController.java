package com.paycanvas.api.controller;

import com.paycanvas.api.model.superadmin.AdminUserRequest;
import com.paycanvas.api.model.superadmin.AdminUserResponse;
import com.paycanvas.api.model.superadmin.CompanyCreateRequest;
import com.paycanvas.api.model.superadmin.CompanyCreateResponse;
import com.paycanvas.api.model.superadmin.CompanySummaryResponse;
import com.paycanvas.api.model.superadmin.CompanyUpdateRequest;
import com.paycanvas.api.service.SuperAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * スーパー管理者向けのRESTコントローラです。利用企業や会社管理者に関する操作を提供します。
 */
@RestController
@RequestMapping("/api/super")
public class SuperAdminController {
  private final SuperAdminService superAdminService;

  public SuperAdminController(SuperAdminService superAdminService) {
    this.superAdminService = superAdminService;
  }

  /**
   * 登録済みの利用企業一覧を取得します。
   *
   * @return 利用企業サマリーのリスト
   */
  @GetMapping("/companies")
  public List<CompanySummaryResponse> listCompanies() {
    return superAdminService.listCompanies();
  }

  /**
   * 指定された情報で会社管理者ユーザーを登録します。
   *
   * @param request 管理者登録リクエスト
   * @return 登録された管理者情報
   */
  @PostMapping("/users")
  public ResponseEntity<AdminUserResponse> createCompanyAdmin(@Valid @RequestBody AdminUserRequest request) {
    AdminUserResponse response = superAdminService.createCompanyAdmin(request);
    return ResponseEntity.status(201).body(response);
  }

  /**
   * 新しい利用企業を登録します。
   *
   * @param request 利用企業登録リクエスト
   * @return 登録された会社情報
   */
  @PostMapping("/companies")
  public ResponseEntity<CompanyCreateResponse> createCompany(@Valid @RequestBody CompanyCreateRequest request) {
    CompanyCreateResponse response = superAdminService.createCompany(request);
    return ResponseEntity.status(201).body(response);
  }

  /**
   * 既存の利用企業情報を更新します。
   *
   * @param id 更新対象の会社ID
   * @param request 更新内容
   * @return 更新後の会社情報
   */
  @PutMapping("/companies/{id}")
  public ResponseEntity<CompanySummaryResponse> updateCompany(
      @PathVariable Long id, @Valid @RequestBody CompanyUpdateRequest request) {
    CompanySummaryResponse response = superAdminService.updateCompany(id, request);
    return ResponseEntity.ok(response);
  }
}
