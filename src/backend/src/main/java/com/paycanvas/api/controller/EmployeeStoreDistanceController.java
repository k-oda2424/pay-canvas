package com.paycanvas.api.controller;

import com.paycanvas.api.model.master.EmployeeStoreDistanceRequest;
import com.paycanvas.api.model.master.EmployeeStoreDistanceResponse;
import com.paycanvas.api.service.EmployeeStoreDistanceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 従業員店舗距離マスタのRESTコントローラークラス。
 *
 * <p>このコントローラーは従業員店舗距離マスタの CRUD 操作を提供します。</p>
 *
 * <p>主なエンドポイント：</p>
 * <ul>
 *   <li>GET /api/masters/employee-store-distances - 全距離データ取得</li>
 *   <li>GET /api/masters/employee-store-distances/employee/{employeeId} - 従業員別距離データ取得</li>
 *   <li>POST /api/masters/employee-store-distances - 距離データ登録</li>
 *   <li>PUT /api/masters/employee-store-distances/{id} - 距離データ更新</li>
 *   <li>DELETE /api/masters/employee-store-distances/{id} - 距離データ削除</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/masters/employee-store-distances")
public class EmployeeStoreDistanceController {

  private final EmployeeStoreDistanceService employeeStoreDistanceService;

  public EmployeeStoreDistanceController(
      EmployeeStoreDistanceService employeeStoreDistanceService) {
    this.employeeStoreDistanceService = employeeStoreDistanceService;
  }

  /**
   * 企業内の全従業員店舗距離データを取得します。
   *
   * @return 従業員店舗距離のレスポンスリスト
   */
  @GetMapping
  public ResponseEntity<List<EmployeeStoreDistanceResponse>> listAll() {
    return ResponseEntity.ok(employeeStoreDistanceService.listAll());
  }

  /**
   * 指定された従業員の全店舗への距離データを取得します。
   *
   * @param employeeId 従業員ID
   * @return 従業員店舗距離のレスポンスリスト
   */
  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<List<EmployeeStoreDistanceResponse>> listByEmployee(
      @PathVariable Integer employeeId) {
    return ResponseEntity.ok(employeeStoreDistanceService.listByEmployee(employeeId));
  }

  /**
   * 従業員店舗距離を新規登録します。
   *
   * @param request リクエストデータ
   * @return 登録された従業員店舗距離のレスポンス
   */
  @PostMapping
  public ResponseEntity<EmployeeStoreDistanceResponse> create(
      @Valid @RequestBody EmployeeStoreDistanceRequest request) {
    return ResponseEntity.ok(employeeStoreDistanceService.create(request));
  }

  /**
   * 従業員店舗距離を更新します。
   *
   * @param id      距離データID
   * @param request リクエストデータ
   * @return 更新された従業員店舗距離のレスポンス
   */
  @PutMapping("/{id}")
  public ResponseEntity<EmployeeStoreDistanceResponse> update(
      @PathVariable Integer id, @Valid @RequestBody EmployeeStoreDistanceRequest request) {
    return ResponseEntity.ok(employeeStoreDistanceService.update(id, request));
  }

  /**
   * 従業員店舗距離を削除します。
   *
   * @param id 距離データID
   * @return 削除成功時は204 No Content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    employeeStoreDistanceService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
