package com.paycanvas.api.service;

import com.paycanvas.api.entity.CommuteMethod;
import com.paycanvas.api.entity.Employee;
import com.paycanvas.api.entity.EmployeeStoreDistance;
import com.paycanvas.api.entity.Store;
import com.paycanvas.api.model.master.EmployeeStoreDistanceRequest;
import com.paycanvas.api.model.master.EmployeeStoreDistanceResponse;
import com.paycanvas.api.repository.CommuteMethodRepository;
import com.paycanvas.api.repository.EmployeeRepository;
import com.paycanvas.api.repository.EmployeeStoreDistanceRepository;
import com.paycanvas.api.repository.StoreRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 従業員店舗距離マスタサービスクラス。
 *
 * <p>CustomServiceを継承し、マルチテナント分離を自動的に行います。
 * CRUD操作はCustomServiceのメソッドを使用することで、他企業のデータへのアクセスを防ぎます。</p>
 */
@Service
public class EmployeeStoreDistanceService extends CustomService {

  private final EmployeeStoreDistanceRepository employeeStoreDistanceRepository;
  private final EmployeeRepository employeeRepository;
  private final StoreRepository storeRepository;
  private final CommuteMethodRepository commuteMethodRepository;

  public EmployeeStoreDistanceService(
      EmployeeStoreDistanceRepository employeeStoreDistanceRepository,
      EmployeeRepository employeeRepository,
      StoreRepository storeRepository,
      CommuteMethodRepository commuteMethodRepository) {
    this.employeeStoreDistanceRepository = employeeStoreDistanceRepository;
    this.employeeRepository = employeeRepository;
    this.storeRepository = storeRepository;
    this.commuteMethodRepository = commuteMethodRepository;
  }

  /**
   * 指定された従業員IDに紐づく全店舗への距離データを取得します。
   *
   * <p>CustomServiceのgetCurrentCompanyId()を使用して、ログイン企業のデータのみを取得します。</p>
   *
   * @param employeeId 従業員ID
   * @return 従業員店舗距離のレスポンスリスト
   */
  @Transactional(readOnly = true)
  public List<EmployeeStoreDistanceResponse> listByEmployee(Integer employeeId) {
    Integer companyId = getCurrentCompanyId();
    return employeeStoreDistanceRepository.findByEmployeeId(employeeId, companyId).stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * 企業内の全従業員店舗距離データを取得します。
   *
   * <p>CustomServiceのfindAll()を使用して、ログイン企業のデータのみを取得します。</p>
   *
   * @return 従業員店舗距離のレスポンスリスト
   */
  @Transactional(readOnly = true)
  public List<EmployeeStoreDistanceResponse> listAll() {
    return findAll(employeeStoreDistanceRepository).stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * 従業員店舗距離を新規登録します。
   *
   * <p>CustomServiceのsave()を使用して、自動的にログイン企業を設定します。</p>
   *
   * @param request リクエストデータ
   * @return 登録された従業員店舗距離のレスポンス
   */
  @Transactional
  public EmployeeStoreDistanceResponse create(EmployeeStoreDistanceRequest request) {
    Integer companyId = getCurrentCompanyId();

    // 既存チェック
    employeeStoreDistanceRepository
        .findByEmployeeIdAndStoreId(request.employeeId(), request.storeId(), companyId)
        .ifPresent(
            existing -> {
              throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST, "この従業員と店舗の組み合わせは既に登録されています");
            });

    Employee employee =
        findById(employeeRepository, request.employeeId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));

    Store store =
        findById(storeRepository, request.storeId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));

    CommuteMethod commuteMethod =
        findById(commuteMethodRepository, request.commuteMethodId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通勤手段が見つかりません"));

    EmployeeStoreDistance distance = new EmployeeStoreDistance();
    distance.setEmployee(employee);
    distance.setStore(store);
    distance.setDistanceKm(request.distanceKm());
    distance.setCommuteMethod(commuteMethod);

    EmployeeStoreDistance saved = save(employeeStoreDistanceRepository, distance);
    return toResponse(saved);
  }

  /**
   * 従業員店舗距離を更新します。
   *
   * <p>CustomServiceのfindById()とsave()を使用して、ログイン企業のデータのみを更新します。</p>
   *
   * @param id      距離データID
   * @param request リクエストデータ
   * @return 更新された従業員店舗距離のレスポンス
   */
  @Transactional
  public EmployeeStoreDistanceResponse update(Integer id, EmployeeStoreDistanceRequest request) {
    EmployeeStoreDistance distance =
        findById(employeeStoreDistanceRepository, id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "距離データが見つかりません"));

    // 従業員・店舗の変更があれば、重複チェック
    if (!distance.getEmployee().getId().equals(request.employeeId())
        || !distance.getStore().getId().equals(request.storeId())) {
      Integer companyId = getCurrentCompanyId();
      employeeStoreDistanceRepository
          .findByEmployeeIdAndStoreId(request.employeeId(), request.storeId(), companyId)
          .ifPresent(
              existing -> {
                if (!existing.getId().equals(id)) {
                  throw new ResponseStatusException(
                      HttpStatus.BAD_REQUEST, "この従業員と店舗の組み合わせは既に登録されています");
                }
              });
    }

    if (!distance.getEmployee().getId().equals(request.employeeId())) {
      Employee employee =
          findById(employeeRepository, request.employeeId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
      distance.setEmployee(employee);
    }

    if (!distance.getStore().getId().equals(request.storeId())) {
      Store store =
          findById(storeRepository, request.storeId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
      distance.setStore(store);
    }

    if (!distance.getCommuteMethod().getId().equals(request.commuteMethodId())) {
      CommuteMethod commuteMethod =
          findById(commuteMethodRepository, request.commuteMethodId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通勤手段が見つかりません"));
      distance.setCommuteMethod(commuteMethod);
    }

    distance.setDistanceKm(request.distanceKm());

    EmployeeStoreDistance saved = save(employeeStoreDistanceRepository, distance);
    return toResponse(saved);
  }

  /**
   * 従業員店舗距離を削除します。
   *
   * <p>CustomServiceのdeleteById()を使用して、ログイン企業のデータのみを削除します。</p>
   *
   * @param id 距離データID
   */
  @Transactional
  public void delete(Integer id) {
    deleteById(employeeStoreDistanceRepository, id);
  }

  /**
   * エンティティをレスポンスモデルに変換します。
   *
   * @param distance エンティティ
   * @return レスポンスモデル
   */
  private EmployeeStoreDistanceResponse toResponse(EmployeeStoreDistance distance) {
    return new EmployeeStoreDistanceResponse(
        distance.getId(),
        distance.getEmployee().getId(),
        distance.getEmployee().getName(),
        distance.getStore().getId(),
        distance.getStore().getName(),
        distance.getDistanceKm(),
        distance.getCommuteMethod().getId(),
        distance.getCommuteMethod().getMethodName());
  }
}
