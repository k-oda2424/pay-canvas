package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.UserAccount;
import com.paycanvas.api.entity.UserRole;
import com.paycanvas.api.model.superadmin.AdminUserRequest;
import com.paycanvas.api.model.superadmin.AdminUserResponse;
import com.paycanvas.api.model.superadmin.CompanyCreateRequest;
import com.paycanvas.api.model.superadmin.CompanyCreateResponse;
import com.paycanvas.api.model.superadmin.CompanySummaryResponse;
import com.paycanvas.api.model.superadmin.CompanyUpdateRequest;
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

/**
 * スーパー管理者向けのドメインロジックを提供するサービスです。利用企業や会社管理者の登録・更新を扱います。
 */
@Service
public class SuperAdminService {
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * SuperAdminServiceのコンストラクタです。
   *
   * @param companyRepository 会社情報のリポジトリ
   * @param userRepository ユーザー情報のリポジトリ
   * @param roleRepository ロール情報のリポジトリ
   * @param userRoleRepository ユーザーロール関連のリポジトリ
   * @param passwordEncoder パスワードエンコーダー
   */
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

  /**
   * 登録されている利用企業の一覧を取得します。
   *
   * @return 利用企業のサマリー情報リスト
   */
  @Transactional(readOnly = true)
  public List<CompanySummaryResponse> listCompanies() {
    return companyRepository.findAll().stream()
        .map(
            company ->
                new CompanySummaryResponse(
                    company.getId().longValue(),
                    company.getName(),
                    company.getStatus(),
                    company.getPostalCode(),
                    company.getAddress(),
                    company.getPhone(),
                    company.getContactName(),
                    company.getContactKana(),
                    company.getContactEmail()))
        .toList();
  }

  /**
   * 指定された会社に紐づく会社管理者ユーザーを新規登録します。
   * メールアドレスの重複チェックを行い、COMPANY_ADMINロールを付与します。
   *
   * @param request 会社管理者登録リクエスト（メールアドレス、表示名、パスワード、会社IDを含む）
   * @return 登録された会社管理者の情報
   * @throws EntityExistsException メールアドレスが既に登録済みの場合
   * @throws EntityNotFoundException 指定された会社またはロールが見つからない場合
   */
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

  /**
   * 利用企業を新規登録します。
   * 会社のステータスは自動的にACTIVEに設定されます。
   *
   * @param request 利用企業登録リクエスト（会社名、郵便番号、住所、電話番号、担当者情報を含む）
   * @return 登録された会社情報
   */
  @Transactional
  public CompanyCreateResponse createCompany(CompanyCreateRequest request) {
    Company company = new Company();
    company.setName(request.name());
    company.setStatus("ACTIVE");
    company.setPostalCode(request.postalCode());
    company.setAddress(request.address());
    company.setPhone(request.phone());
    company.setContactName(request.contactName());
    company.setContactKana(request.contactKana());
    company.setContactEmail(request.contactEmail());
    Company saved = companyRepository.save(company);
    return new CompanyCreateResponse(
        saved.getId().longValue(),
        saved.getName(),
        saved.getStatus(),
        saved.getPostalCode(),
        saved.getAddress(),
        saved.getPhone(),
        saved.getContactName(),
        saved.getContactKana(),
        saved.getContactEmail());
  }

  /**
   * 既存の利用企業情報を更新します。
   * 指定されたIDの会社が存在しない場合は例外をスローします。
   *
   * @param id 更新対象の会社ID
   * @param request 更新内容（会社名、郵便番号、住所、電話番号、担当者情報を含む）
   * @return 更新後の会社情報
   * @throws EntityNotFoundException 指定されたIDの会社が見つからない場合
   */
  @Transactional
  public CompanySummaryResponse updateCompany(Long id, CompanyUpdateRequest request) {
    Company company =
        companyRepository
            .findById(id.intValue())
            .orElseThrow(() -> new EntityNotFoundException("会社が見つかりません"));

    company.setName(request.name());
    company.setPostalCode(request.postalCode());
    company.setAddress(request.address());
    company.setPhone(request.phone());
    company.setContactName(request.contactName());
    company.setContactKana(request.contactKana());
    company.setContactEmail(request.contactEmail());

    Company saved = companyRepository.save(company);
    return new CompanySummaryResponse(
        saved.getId().longValue(),
        saved.getName(),
        saved.getStatus(),
        saved.getPostalCode(),
        saved.getAddress(),
        saved.getPhone(),
        saved.getContactName(),
        saved.getContactKana(),
        saved.getContactEmail());
  }
}
