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
 */
@Service
public class EmployeeStoreDistanceService {

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
   * @param employeeId 従業員ID
   * @return 従業員店舗距離のレスポンスリスト
   */
  @Transactional(readOnly = true)
  public List<EmployeeStoreDistanceResponse> listByEmployee(Integer employeeId) {
    // TODO: 企業IDでフィルタリング
    Integer companyId = 1;
    return employeeStoreDistanceRepository.findByEmployeeId(employeeId, companyId).stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * 企業内の全従業員店舗距離データを取得します。
   *
   * @return 従業員店舗距離のレスポンスリスト
   */
  @Transactional(readOnly = true)
  public List<EmployeeStoreDistanceResponse> listAll() {
    // TODO: 企業IDでフィルタリング（SecurityContextから取得）
    return employeeStoreDistanceRepository.findAll().stream().map(this::toResponse).toList();
  }

  /**
   * 従業員店舗距離を新規登録します。
   *
   * @param request リクエストデータ
   * @return 登録された従業員店舗距離のレスポンス
   */
  @Transactional
  public EmployeeStoreDistanceResponse create(EmployeeStoreDistanceRequest request) {
    // TODO: 企業IDでフィルタリング（SecurityContextから取得）
    Integer companyId = 1; // 仮置き

    // 既存チェック
    employeeStoreDistanceRepository
        .findByEmployeeIdAndStoreId(request.employeeId(), request.storeId(), companyId)
        .ifPresent(
            existing -> {
              throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST, "この従業員と店舗の組み合わせは既に登録されています");
            });

    Employee employee =
        employeeRepository.findById(request.employeeId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));

    Store store =
        storeRepository.findById(request.storeId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));

    CommuteMethod commuteMethod =
        commuteMethodRepository.findById(request.commuteMethodId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通勤手段が見つかりません"));

    EmployeeStoreDistance distance = new EmployeeStoreDistance();
    distance.setCompany(employee.getCompany()); // 従業員の企業を設定
    distance.setEmployee(employee);
    distance.setStore(store);
    distance.setDistanceKm(request.distanceKm());
    distance.setCommuteMethod(commuteMethod);

    EmployeeStoreDistance saved = employeeStoreDistanceRepository.save(distance);
    return toResponse(saved);
  }

  /**
   * 従業員店舗距離を更新します。
   *
   * @param id      距離データID
   * @param request リクエストデータ
   * @return 更新された従業員店舗距離のレスポンス
   */
  @Transactional
  public EmployeeStoreDistanceResponse update(Integer id, EmployeeStoreDistanceRequest request) {
    EmployeeStoreDistance distance =
        employeeStoreDistanceRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "距離データが見つかりません"));

    // 従業員・店舗の変更があれば、重複チェック
    if (!distance.getEmployee().getId().equals(request.employeeId())
        || !distance.getStore().getId().equals(request.storeId())) {
      // TODO: 企業IDでフィルタリング
      Integer companyId = 1;
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
          employeeRepository.findById(request.employeeId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
      distance.setEmployee(employee);
    }

    if (!distance.getStore().getId().equals(request.storeId())) {
      Store store =
          storeRepository.findById(request.storeId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
      distance.setStore(store);
    }

    if (!distance.getCommuteMethod().getId().equals(request.commuteMethodId())) {
      CommuteMethod commuteMethod =
          commuteMethodRepository.findById(request.commuteMethodId())
              .orElseThrow(
                  () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通勤手段が見つかりません"));
      distance.setCommuteMethod(commuteMethod);
    }

    distance.setDistanceKm(request.distanceKm());

    EmployeeStoreDistance saved = employeeStoreDistanceRepository.save(distance);
    return toResponse(saved);
  }

  /**
   * 従業員店舗距離を削除します。
   *
   * @param id 距離データID
   */
  @Transactional
  public void delete(Integer id) {
    employeeStoreDistanceRepository.deleteById(id);
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
