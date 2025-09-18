package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.EmployeeGrade;
import com.paycanvas.api.entity.SalaryTier;
import com.paycanvas.api.entity.Store;
import com.paycanvas.api.model.master.GradeRequest;
import com.paycanvas.api.model.master.GradeResponse;
import com.paycanvas.api.model.master.SalaryTierRequest;
import com.paycanvas.api.model.master.SalaryTierResponse;
import com.paycanvas.api.model.master.StoreRequest;
import com.paycanvas.api.model.master.StoreResponse;
import com.paycanvas.api.repository.CompanyRepository;
import com.paycanvas.api.repository.EmployeeGradeRepository;
import com.paycanvas.api.repository.SalaryTierRepository;
import com.paycanvas.api.repository.StoreRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * マスターデータの管理を担当するサービスクラスです。
 * 店舗、等級、給与階層などの基本データのCRUD操作を提供します。
 */
@Service
public class MasterDataService {
  /** デフォルト会社のID */
  private static final int DEFAULT_COMPANY_ID = 1;

  private final CompanyRepository companyRepository;
  private final StoreRepository storeRepository;
  private final EmployeeGradeRepository employeeGradeRepository;
  private final SalaryTierRepository salaryTierRepository;

  /**
   * MasterDataServiceのコンストラクタです。
   *
   * @param companyRepository 会社情報のリポジトリ
   * @param storeRepository 店舗情報のリポジトリ
   * @param employeeGradeRepository 従業員等級のリポジトリ
   * @param salaryTierRepository 給与階層のリポジトリ
   */
  public MasterDataService(
      CompanyRepository companyRepository,
      StoreRepository storeRepository,
      EmployeeGradeRepository employeeGradeRepository,
      SalaryTierRepository salaryTierRepository) {
    this.companyRepository = companyRepository;
    this.storeRepository = storeRepository;
    this.employeeGradeRepository = employeeGradeRepository;
    this.salaryTierRepository = salaryTierRepository;
  }

  /**
   * デフォルト会社の情報を取得します。
   *
   * @return デフォルトの会社エンティティ
   * @throws ResponseStatusException デフォルトの会社が見つからない場合
   */
  private Company defaultCompany() {
    return companyRepository
        .findById(DEFAULT_COMPANY_ID)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "デフォルトの会社が存在しません"));
  }

  // --- Stores ---
  /**
   * デフォルト会社の全店舗一覧を取得します。
   * 店舗はIDの昇順でソートされて返されます。
   *
   * @return 店舗レスポンスのリスト
   */
  @Transactional(readOnly = true)
  public List<StoreResponse> listStores() {
    return storeRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(store -> new StoreResponse(store.getId(), store.getName(), store.getStoreType(), store.getAddress()))
        .sorted(Comparator.comparing(StoreResponse::id))
        .toList();
  }

  /**
   * 新しい店舗を作成します。
   * デフォルト会社に関連付けられた店舗として作成されます。
   *
   * @param request 店舗作成リクエスト（店舗名、店舗タイプ、住所を含む）
   * @return 作成された店舗のレスポンス
   */
  @Transactional
  public StoreResponse createStore(StoreRequest request) {
    Store store = new Store();
    store.setCompany(defaultCompany());
    store.setName(request.name());
    store.setStoreType(request.storeType());
    store.setAddress(request.address());
    Store saved = storeRepository.save(store);
    return new StoreResponse(saved.getId(), saved.getName(), saved.getStoreType(), saved.getAddress());
  }

  /**
   * 既存の店舗情報を更新します。
   * 指定されたIDの店舗が存在しない場合は例外をスローします。
   *
   * @param id 更新対象の店舗ID
   * @param request 店舗更新リクエスト（店舗名、店舗タイプ、住所を含む）
   * @return 更新された店舗のレスポンス
   * @throws ResponseStatusException 指定されたIDの店舗が見つからない場合
   */
  @Transactional
  public StoreResponse updateStore(Integer id, StoreRequest request) {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
    store.setName(request.name());
    store.setStoreType(request.storeType());
    store.setAddress(request.address());
    Store saved = storeRepository.save(store);
    return new StoreResponse(saved.getId(), saved.getName(), saved.getStoreType(), saved.getAddress());
  }

  /**
   * 指定されたIDの店舗を削除します。
   * 指定されたIDの店舗が存在しない場合は例外をスローします。
   *
   * @param id 削除対象の店舗ID
   * @throws ResponseStatusException 指定されたIDの店舗が見つからない場合
   */
  @Transactional
  public void deleteStore(Integer id) {
    if (!storeRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません");
    }
    storeRepository.deleteById(id);
  }

  // --- Grades ---
  /**
   * デフォルト会社の全従業員等級一覧を取得します。
   *
   * @return 等級レスポンスのリスト
   */
  @Transactional(readOnly = true)
  public List<GradeResponse> listGrades() {
    return employeeGradeRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(grade -> new GradeResponse(grade.getId(), grade.getGradeName(), grade.getCommissionRate()))
        .toList();
  }

  /**
   * 新しい従業員等級を作成します。
   * デフォルト会社に関連付けられた等級として作成されます。
   * コミッション率はパーセント値から小数値に変換されます。
   *
   * @param request 等級作成リクエスト（等級名、コミッション率（パーセント）を含む）
   * @return 作成された等級のレスポンス
   */
  @Transactional
  public GradeResponse createGrade(GradeRequest request) {
    EmployeeGrade grade = new EmployeeGrade();
    grade.setCompany(defaultCompany());
    grade.setGradeName(request.gradeName());
    grade.setCommissionRate(percentToDecimal(request.commissionRatePercent()));
    EmployeeGrade saved = employeeGradeRepository.save(grade);
    return new GradeResponse(saved.getId(), saved.getGradeName(), saved.getCommissionRate());
  }

  /**
   * 既存の従業員等級情報を更新します。
   * 指定されたIDの等級が存在しない場合は例外をスローします。
   * コミッション率はパーセント値から小数値に変換されます。
   *
   * @param id 更新対象の等級ID
   * @param request 等級更新リクエスト（等級名、コミッション率（パーセント）を含む）
   * @return 更新された等級のレスポンス
   * @throws ResponseStatusException 指定されたIDの等級が見つからない場合
   */
  @Transactional
  public GradeResponse updateGrade(Integer id, GradeRequest request) {
    EmployeeGrade grade =
        employeeGradeRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
    grade.setGradeName(request.gradeName());
    grade.setCommissionRate(percentToDecimal(request.commissionRatePercent()));
    EmployeeGrade saved = employeeGradeRepository.save(grade);
    return new GradeResponse(saved.getId(), saved.getGradeName(), saved.getCommissionRate());
  }

  /**
   * 指定されたIDの従業員等級を削除します。
   * 指定されたIDの等級が存在しない場合は例外をスローします。
   *
   * @param id 削除対象の等級ID
   * @throws ResponseStatusException 指定されたIDの等級が見つからない場合
   */
  @Transactional
  public void deleteGrade(Integer id) {
    if (!employeeGradeRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません");
    }
    employeeGradeRepository.deleteById(id);
  }

  // --- Salary Tiers ---
  /**
   * デフォルト会社の全給与階層一覧を取得します。
   *
   * @return 給与階層レスポンスのリスト
   */
  @Transactional(readOnly = true)
  public List<SalaryTierResponse> listSalaryTiers() {
    return salaryTierRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(tier -> new SalaryTierResponse(tier.getId(), tier.getPlanName(), tier.getMonthlyDaysOff(), tier.getBaseSalary()))
        .toList();
  }

  /**
   * 新しい給与階層を作成します。
   * デフォルト会社に関連付けられた給与階層として作成されます。
   *
   * @param request 給与階層作成リクエスト（プラン名、月次休日数、基本給を含む）
   * @return 作成された給与階層のレスポンス
   */
  @Transactional
  public SalaryTierResponse createSalaryTier(SalaryTierRequest request) {
    SalaryTier tier = new SalaryTier();
    tier.setCompany(defaultCompany());
    tier.setPlanName(request.planName());
    tier.setMonthlyDaysOff(request.monthlyDaysOff());
    tier.setBaseSalary(request.baseSalary());
    SalaryTier saved = salaryTierRepository.save(tier);
    return new SalaryTierResponse(saved.getId(), saved.getPlanName(), saved.getMonthlyDaysOff(), saved.getBaseSalary());
  }

  /**
   * 既存の給与階層情報を更新します。
   * 指定されたIDの給与階層が存在しない場合は例外をスローします。
   *
   * @param id 更新対象の給与階層ID
   * @param request 給与階層更新リクエスト（プラン名、月次休日数、基本給を含む）
   * @return 更新された給与階層のレスポンス
   * @throws ResponseStatusException 指定されたIDの給与プランが見つからない場合
   */
  @Transactional
  public SalaryTierResponse updateSalaryTier(Integer id, SalaryTierRequest request) {
    SalaryTier tier =
        salaryTierRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "給与プランが見つかりません"));
    tier.setPlanName(request.planName());
    tier.setMonthlyDaysOff(request.monthlyDaysOff());
    tier.setBaseSalary(request.baseSalary());
    SalaryTier saved = salaryTierRepository.save(tier);
    return new SalaryTierResponse(saved.getId(), saved.getPlanName(), saved.getMonthlyDaysOff(), saved.getBaseSalary());
  }

  /**
   * 指定されたIDの給与階層を削除します。
   * 指定されたIDの給与階層が存在しない場合は例外をスローします。
   *
   * @param id 削除対象の給与階層ID
   * @throws ResponseStatusException 指定されたIDの給与プランが見つからない場合
   */
  @Transactional
  public void deleteSalaryTier(Integer id) {
    if (!salaryTierRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "給与プランが見つかりません");
    }
    salaryTierRepository.deleteById(id);
  }

  /**
   * パーセント値を小数値に変換します。
   * 例：5% → 0.05
   *
   * @param percent パーセント値（BigDecimal形式）
   * @return 小数値（double形式）
   */
  private double percentToDecimal(BigDecimal percent) {
    return percent.divide(BigDecimal.valueOf(100)).doubleValue();
  }
}
