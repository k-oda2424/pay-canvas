package com.paycanvas.api.service;

import com.paycanvas.api.entity.Employee;
import com.paycanvas.api.entity.TransportationCost;
import com.paycanvas.api.model.TransportationCostRequest;
import com.paycanvas.api.model.TransportationCostResponse;
import com.paycanvas.api.repository.EmployeeRepository;
import com.paycanvas.api.repository.TransportationCostRepository;
import com.paycanvas.api.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * 交通費マスター管理サービス。
 *
 * <p>従業員の交通費情報の登録、更新、削除、検索を行います。
 * 交通費は従業員を通じて企業スコープで管理されます。</p>
 */
@Service
public class TransportationCostService {

  private final TransportationCostRepository transportationCostRepository;
  private final EmployeeRepository employeeRepository;

  public TransportationCostService(
      TransportationCostRepository transportationCostRepository,
      EmployeeRepository employeeRepository) {
    this.transportationCostRepository = transportationCostRepository;
    this.employeeRepository = employeeRepository;
  }

  /**
   * 現在ログイン中のユーザーの企業IDを取得します。
   *
   * @return ログイン中のユーザーの企業ID
   */
  private Integer getCurrentCompanyId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "認証情報が見つかりません");
    }
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    Integer companyId = principal.getCompanyId();
    if (companyId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "企業情報が見つかりません");
    }
    return companyId;
  }

  /**
   * 現在ログイン中の企業の交通費一覧を取得します。
   *
   * <p>認証コンテキストから企業IDを取得し、その企業に所属する従業員の交通費のみを返します。</p>
   *
   * @return 交通費レスポンスのリスト
   */
  public List<TransportationCostResponse> listByCompanyId() {
    Integer companyId = getCurrentCompanyId();
    return transportationCostRepository.findByCompanyId(companyId).stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * 従業員IDで交通費一覧を取得します。
   *
   * @param employeeId 従業員ID
   * @return 交通費レスポンスのリスト
   */
  public List<TransportationCostResponse> listByEmployeeId(Integer employeeId) {
    return transportationCostRepository.findByEmployee_IdOrderByEffectiveFromDesc(employeeId).stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * 特定日付時点で有効な交通費を取得します。
   *
   * @param employeeId 従業員ID
   * @param targetDate 対象日付
   * @return 交通費レスポンス（存在しない場合はnull）
   */
  public TransportationCostResponse getActiveByDate(Integer employeeId, LocalDate targetDate) {
    return transportationCostRepository.findActiveByEmployeeIdAndDate(employeeId, targetDate)
        .map(this::toResponse)
        .orElse(null);
  }

  /**
   * 交通費IDで取得します。
   *
   * <p>交通費が現在ログイン中の企業に所属する従業員のものか検証します。</p>
   *
   * @param id 交通費ID
   * @return 交通費レスポンス
   */
  public TransportationCostResponse getById(Integer id) {
    TransportationCost cost = transportationCostRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "交通費情報が見つかりません: ID=" + id));

    // 従業員の企業IDをチェック
    Integer companyId = getCurrentCompanyId();
    if (cost.getEmployee().getCompany() == null ||
        !cost.getEmployee().getCompany().getId().equals(companyId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この交通費情報にアクセスする権限がありません");
    }

    return toResponse(cost);
  }

  /**
   * 交通費情報を新規登録します。
   *
   * <p>指定された従業員が現在ログイン中の企業に所属しているか検証します。</p>
   *
   * @param request 交通費登録リクエスト
   * @return 登録された交通費レスポンス
   */
  @Transactional
  public TransportationCostResponse create(TransportationCostRequest request) {
    Integer companyId = getCurrentCompanyId();
    Employee employee = employeeRepository.findById(request.getEmployeeId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "従業員が見つかりません: ID=" + request.getEmployeeId()));

    // 従業員が現在の企業に所属しているか検証
    if (employee.getCompany() == null || !employee.getCompany().getId().equals(companyId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この従業員にアクセスする権限がありません");
    }

    TransportationCost cost = new TransportationCost();
    cost.setEmployee(employee);
    cost.setMonthlyAmount(request.getMonthlyAmount());
    cost.setRoute(request.getRoute());
    cost.setEffectiveFrom(request.getEffectiveFrom());
    cost.setEffectiveTo(request.getEffectiveTo());

    TransportationCost saved = transportationCostRepository.save(cost);
    return toResponse(saved);
  }

  /**
   * 交通費情報を更新します。
   *
   * <p>交通費が現在ログイン中の企業に所属する従業員のものか検証します。</p>
   *
   * @param id 交通費ID
   * @param request 交通費更新リクエスト
   * @return 更新された交通費レスポンス
   */
  @Transactional
  public TransportationCostResponse update(Integer id, TransportationCostRequest request) {
    Integer companyId = getCurrentCompanyId();
    TransportationCost cost = transportationCostRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "交通費情報が見つかりません: ID=" + id));

    // 従業員の企業IDをチェック
    if (cost.getEmployee().getCompany() == null ||
        !cost.getEmployee().getCompany().getId().equals(companyId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この交通費情報を更新する権限がありません");
    }

    cost.setMonthlyAmount(request.getMonthlyAmount());
    cost.setRoute(request.getRoute());
    cost.setEffectiveFrom(request.getEffectiveFrom());
    cost.setEffectiveTo(request.getEffectiveTo());

    TransportationCost updated = transportationCostRepository.save(cost);
    return toResponse(updated);
  }

  /**
   * 交通費情報を削除します。
   *
   * <p>交通費が現在ログイン中の企業に所属する従業員のものか検証します。</p>
   *
   * @param id 交通費ID
   */
  @Transactional
  public void delete(Integer id) {
    Integer companyId = getCurrentCompanyId();
    TransportationCost cost = transportationCostRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "交通費情報が見つかりません: ID=" + id));

    // 従業員の企業IDをチェック
    if (cost.getEmployee().getCompany() == null ||
        !cost.getEmployee().getCompany().getId().equals(companyId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この交通費情報を削除する権限がありません");
    }

    transportationCostRepository.deleteById(id);
  }

  /**
   * エンティティをレスポンスに変換します。
   *
   * @param cost 交通費エンティティ
   * @return 交通費レスポンス
   */
  private TransportationCostResponse toResponse(TransportationCost cost) {
    TransportationCostResponse response = new TransportationCostResponse();
    response.setId(cost.getId());
    response.setEmployeeId(cost.getEmployee().getId());
    response.setEmployeeName(cost.getEmployee().getName());
    response.setMonthlyAmount(cost.getMonthlyAmount());
    response.setRoute(cost.getRoute());
    response.setEffectiveFrom(cost.getEffectiveFrom());
    response.setEffectiveTo(cost.getEffectiveTo());
    return response;
  }
}
