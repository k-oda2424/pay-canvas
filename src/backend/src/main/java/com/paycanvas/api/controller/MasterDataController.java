package com.paycanvas.api.controller;

import com.paycanvas.api.model.master.GradeRequest;
import com.paycanvas.api.model.master.GradeResponse;
import com.paycanvas.api.model.master.SalaryTierRequest;
import com.paycanvas.api.model.master.SalaryTierResponse;
import com.paycanvas.api.model.master.StoreRequest;
import com.paycanvas.api.model.master.StoreResponse;
import com.paycanvas.api.service.MasterDataService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * マスタデータ管理コントローラー
 *
 * <p>システムのマスタデータ（店舗、等級、給与ティア）の管理を行うRESTコントローラーです。
 * 各マスタデータの参照、作成、更新、削除機能を提供します。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/masters")
public class MasterDataController {
  private final MasterDataService masterDataService;

  /**
   * コンストラクタ
   *
   * @param masterDataService マスタデータサービス
   */
  public MasterDataController(MasterDataService masterDataService) {
    this.masterDataService = masterDataService;
  }

  // Stores
  /**
   * 店舗マスタ一覧を取得
   *
   * <p>システムに登録されている全ての店舗情報を取得します。</p>
   *
   * @return 店舗情報のリスト
   */
  @GetMapping("/stores")
  public List<StoreResponse> listStores() {
    return masterDataService.listStores();
  }

  /**
   * 店舗マスタを新規作成
   *
   * <p>新しい店舗情報を作成します。</p>
   *
   * @param request 店舗作成リクエスト
   * @return 作成された店舗情報
   */
  @PostMapping("/stores")
  public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request) {
    return ResponseEntity.ok(masterDataService.createStore(request));
  }

  /**
   * 店舗マスタを更新
   *
   * <p>指定されたIDの店舗情報を更新します。</p>
   *
   * @param id 更新対象の店舗ID
   * @param request 店舗更新リクエスト
   * @return 更新された店舗情報
   */
  @PutMapping("/stores/{id}")
  public ResponseEntity<StoreResponse> updateStore(
      @PathVariable Integer id, @Valid @RequestBody StoreRequest request) {
    return ResponseEntity.ok(masterDataService.updateStore(id, request));
  }

  /**
   * 店舗マスタを削除
   *
   * <p>指定されたIDの店舗情報を削除します。</p>
   *
   * @param id 削除対象の店舗ID
   * @return 削除完了レスポンス（204 No Content）
   */
  @DeleteMapping("/stores/{id}")
  public ResponseEntity<Void> deleteStore(@PathVariable Integer id) {
    masterDataService.deleteStore(id);
    return ResponseEntity.noContent().build();
  }

  // Grades
  /**
   * 等級マスタ一覧を取得
   *
   * <p>システムに登録されている全ての等級情報を取得します。</p>
   *
   * @return 等級情報のリスト
   */
  @GetMapping("/grades")
  public List<GradeResponse> listGrades() {
    return masterDataService.listGrades();
  }

  /**
   * 等級マスタを新規作成
   *
   * <p>新しい等級情報を作成します。</p>
   *
   * @param request 等級作成リクエスト
   * @return 作成された等級情報
   */
  @PostMapping("/grades")
  public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody GradeRequest request) {
    return ResponseEntity.ok(masterDataService.createGrade(request));
  }

  /**
   * 等級マスタを更新
   *
   * <p>指定されたIDの等級情報を更新します。</p>
   *
   * @param id 更新対象の等級ID
   * @param request 等級更新リクエスト
   * @return 更新された等級情報
   */
  @PutMapping("/grades/{id}")
  public ResponseEntity<GradeResponse> updateGrade(
      @PathVariable Integer id, @Valid @RequestBody GradeRequest request) {
    return ResponseEntity.ok(masterDataService.updateGrade(id, request));
  }

  /**
   * 等級マスタを削除
   *
   * <p>指定されたIDの等級情報を削除します。</p>
   *
   * @param id 削除対象の等級ID
   * @return 削除完了レスポンス（204 No Content）
   */
  @DeleteMapping("/grades/{id}")
  public ResponseEntity<Void> deleteGrade(@PathVariable Integer id) {
    masterDataService.deleteGrade(id);
    return ResponseEntity.noContent().build();
  }

  // Salary tiers
  /**
   * 給与ティアマスタ一覧を取得
   *
   * <p>システムに登録されている全ての給与ティア情報を取得します。</p>
   *
   * @return 給与ティア情報のリスト
   */
  @GetMapping("/salary-tiers")
  public List<SalaryTierResponse> listSalaryTiers() {
    return masterDataService.listSalaryTiers();
  }

  /**
   * 給与ティアマスタを新規作成
   *
   * <p>新しい給与ティア情報を作成します。</p>
   *
   * @param request 給与ティア作成リクエスト
   * @return 作成された給与ティア情報
   */
  @PostMapping("/salary-tiers")
  public ResponseEntity<SalaryTierResponse> createSalaryTier(
      @Valid @RequestBody SalaryTierRequest request) {
    return ResponseEntity.ok(masterDataService.createSalaryTier(request));
  }

  /**
   * 給与ティアマスタを更新
   *
   * <p>指定されたIDの給与ティア情報を更新します。</p>
   *
   * @param id 更新対象の給与ティアID
   * @param request 給与ティア更新リクエスト
   * @return 更新された給与ティア情報
   */
  @PutMapping("/salary-tiers/{id}")
  public ResponseEntity<SalaryTierResponse> updateSalaryTier(
      @PathVariable Integer id, @Valid @RequestBody SalaryTierRequest request) {
    return ResponseEntity.ok(masterDataService.updateSalaryTier(id, request));
  }

  /**
   * 給与ティアマスタを削除
   *
   * <p>指定されたIDの給与ティア情報を削除します。</p>
   *
   * @param id 削除対象の給与ティアID
   * @return 削除完了レスポンス（204 No Content）
   */
  @DeleteMapping("/salary-tiers/{id}")
  public ResponseEntity<Void> deleteSalaryTier(@PathVariable Integer id) {
    masterDataService.deleteSalaryTier(id);
    return ResponseEntity.noContent().build();
  }
}
