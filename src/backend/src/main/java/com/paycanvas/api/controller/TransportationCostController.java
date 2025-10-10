package com.paycanvas.api.controller;

import com.paycanvas.api.model.TransportationCostRequest;
import com.paycanvas.api.model.TransportationCostResponse;
import com.paycanvas.api.service.TransportationCostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 交通費マスター管理コントローラー。
 *
 * <p>従業員の交通費情報に関するREST APIを提供します。</p>
 *
 * <p>提供するエンドポイント：</p>
 * <ul>
 *   <li>GET /api/transportation-costs - 企業の交通費一覧取得</li>
 *   <li>GET /api/transportation-costs/employee/{employeeId} - 従業員の交通費履歴取得</li>
 *   <li>GET /api/transportation-costs/employee/{employeeId}/active - 有効な交通費取得</li>
 *   <li>GET /api/transportation-costs/{id} - 交通費詳細取得</li>
 *   <li>POST /api/transportation-costs - 交通費新規登録</li>
 *   <li>PUT /api/transportation-costs/{id} - 交通費更新</li>
 *   <li>DELETE /api/transportation-costs/{id} - 交通費削除</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/transportation-costs")
public class TransportationCostController {

  private final TransportationCostService transportationCostService;

  public TransportationCostController(TransportationCostService transportationCostService) {
    this.transportationCostService = transportationCostService;
  }

  /**
   * 現在ログイン中の企業の交通費一覧を取得します。
   *
   * <p>サービス層で認証コンテキストから企業IDを自動的に取得します。</p>
   *
   * @return 交通費レスポンスのリスト
   */
  @GetMapping
  public ResponseEntity<List<TransportationCostResponse>> list() {
    List<TransportationCostResponse> costs = transportationCostService.listByCompanyId();
    return ResponseEntity.ok(costs);
  }

  /**
   * 従業員の交通費履歴を取得します。
   *
   * @param employeeId 従業員ID
   * @return 交通費レスポンスのリスト
   */
  @GetMapping("/employee/{employeeId}")
  public ResponseEntity<List<TransportationCostResponse>> listByEmployee(
      @PathVariable Integer employeeId) {
    List<TransportationCostResponse> costs = transportationCostService.listByEmployeeId(employeeId);
    return ResponseEntity.ok(costs);
  }

  /**
   * 特定日付時点で有効な交通費を取得します。
   *
   * @param employeeId 従業員ID
   * @param date 対象日付（省略時は今日）
   * @return 交通費レスポンス
   */
  @GetMapping("/employee/{employeeId}/active")
  public ResponseEntity<TransportationCostResponse> getActive(
      @PathVariable Integer employeeId,
      @RequestParam(required = false) LocalDate date) {
    LocalDate targetDate = date != null ? date : LocalDate.now();
    TransportationCostResponse cost = transportationCostService.getActiveByDate(employeeId, targetDate);
    if (cost == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(cost);
  }

  /**
   * 交通費詳細を取得します。
   *
   * @param id 交通費ID
   * @return 交通費レスポンス
   */
  @GetMapping("/{id}")
  public ResponseEntity<TransportationCostResponse> getById(@PathVariable Integer id) {
    TransportationCostResponse cost = transportationCostService.getById(id);
    return ResponseEntity.ok(cost);
  }

  /**
   * 交通費を新規登録します。
   *
   * @param request 交通費登録リクエスト
   * @return 登録された交通費レスポンス
   */
  @PostMapping
  public ResponseEntity<TransportationCostResponse> create(
      @Valid @RequestBody TransportationCostRequest request) {
    TransportationCostResponse created = transportationCostService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * 交通費を更新します。
   *
   * @param id 交通費ID
   * @param request 交通費更新リクエスト
   * @return 更新された交通費レスポンス
   */
  @PutMapping("/{id}")
  public ResponseEntity<TransportationCostResponse> update(
      @PathVariable Integer id,
      @Valid @RequestBody TransportationCostRequest request) {
    TransportationCostResponse updated = transportationCostService.update(id, request);
    return ResponseEntity.ok(updated);
  }

  /**
   * 交通費を削除します。
   *
   * @param id 交通費ID
   * @return レスポンスなし
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    transportationCostService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
