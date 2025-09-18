package com.paycanvas.api.controller;

import com.paycanvas.api.model.EmployeeMaster;
import com.paycanvas.api.model.EmployeeMasterRequest;
import com.paycanvas.api.service.EmployeeService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 従業員管理コントローラー
 *
 * <p>従業員マスタデータの取得を行うRESTコントローラーです。
 * 従業員の基本情報やマスタデータの参照機能を提供します。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/staff")
public class EmployeeController {
  private final EmployeeService employeeService;

  /**
   * コンストラクタ
   *
   * @param employeeService 従業員サービス
   */
  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  /**
   * 従業員一覧を取得
   *
   * <p>システムに登録されている全ての従業員のマスタデータを取得します。</p>
   *
   * @return 従業員マスタのリスト
   */
  @GetMapping
  public List<EmployeeMaster> list() {
    return employeeService.listEmployees();
  }

  /** 従業員を新規登録します。 */
  @PostMapping
  public ResponseEntity<EmployeeMaster> createEmployee(@Valid @RequestBody EmployeeMasterRequest request) {
    EmployeeMaster response = employeeService.createEmployee(request);
    return ResponseEntity.status(201).body(response);
  }

  /** 従業員情報を更新します。 */
  @PutMapping("/{id}")
  public ResponseEntity<EmployeeMaster> updateEmployee(
      @PathVariable Integer id, @Valid @RequestBody EmployeeMasterRequest request) {
    EmployeeMaster response = employeeService.updateEmployee(id, request);
    return ResponseEntity.ok(response);
  }

  /** 従業員を削除します。 */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }
}
