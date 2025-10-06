package com.paycanvas.api.service;

import com.paycanvas.api.entity.Employee;
import com.paycanvas.api.entity.EmployeeGrade;
import com.paycanvas.api.entity.Store;
import com.paycanvas.api.entity.WorkPattern;
import com.paycanvas.api.model.EmployeeMaster;
import com.paycanvas.api.model.EmployeeMasterRequest;
import com.paycanvas.api.repository.EmployeeGradeRepository;
import com.paycanvas.api.repository.EmployeeRepository;
import com.paycanvas.api.repository.StoreRepository;
import com.paycanvas.api.repository.WorkPatternRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService extends CustomService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeGradeRepository employeeGradeRepository;
  private final WorkPatternRepository workPatternRepository;
  private final StoreRepository storeRepository;

  public EmployeeService(
      EmployeeRepository employeeRepository,
      EmployeeGradeRepository employeeGradeRepository,
      WorkPatternRepository workPatternRepository,
      StoreRepository storeRepository) {
    this.employeeRepository = employeeRepository;
    this.employeeGradeRepository = employeeGradeRepository;
    this.workPatternRepository = workPatternRepository;
    this.storeRepository = storeRepository;
  }

  /**
   * 従業員一覧を取得します（ログイン企業のみ）。
   *
   * @return 従業員マスタのリスト
   */
  @Transactional(readOnly = true)
  public List<EmployeeMaster> listEmployees() {
    return findAll(employeeRepository).stream().map(this::toMaster).toList();
  }

  @Transactional
  public EmployeeMaster createEmployee(EmployeeMasterRequest request) {
    Employee employee = new Employee();
    applyRequest(employee, request);
    Employee saved = save(employeeRepository, employee); // CustomServiceのsaveを使用（自動で企業設定）
    return employeeRepository
        .findByIdWithRelations(saved.getId())
        .map(this::toMaster)
        .orElseGet(() -> toMaster(saved));
  }

  @Transactional
  public EmployeeMaster updateEmployee(Integer id, EmployeeMasterRequest request) {
    Employee employee =
        findById(employeeRepository, id) // CustomServiceのfindByIdを使用（自動で企業チェック）
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
    applyRequest(employee, request);
    Employee saved = save(employeeRepository, employee); // CustomServiceのsaveを使用（自動で企業検証）
    return employeeRepository
        .findByIdWithRelations(saved.getId())
        .map(this::toMaster)
        .orElseGet(() -> toMaster(saved));
  }

  /**
   * 従業員を論理削除（退職処理）します。
   *
   * @param id 従業員ID
   * @throws ResponseStatusException 従業員が見つからない場合、または権限がない場合
   */
  @Transactional
  public void deleteEmployee(Integer id) {
    Employee employee =
        findById(employeeRepository, id) // CustomServiceのfindByIdを使用（自動で企業チェック）
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
    // 論理削除：退職日を今日の日付に設定
    employee.setResignationDate(java.time.LocalDate.now());
    save(employeeRepository, employee); // CustomServiceのsaveを使用（自動で企業検証）
  }

  /**
   * 従業員を物理削除します。
   * 警告: この操作は元に戻せません。
   *
   * @param id 従業員ID
   * @throws ResponseStatusException 従業員が見つからない場合、または権限がない場合
   */
  @Transactional
  public void permanentlyDeleteEmployee(Integer id) {
    deleteById(employeeRepository, id); // CustomServiceのdeleteByIdを使用（自動で企業検証して削除）
  }

  /**
   * 従業員を退職処理します（論理削除）。
   *
   * @param id 従業員ID
   * @param resignationDate 退職日
   * @throws ResponseStatusException 従業員が見つからない場合、または権限がない場合
   */
  @Transactional
  public void resignEmployee(Integer id, java.time.LocalDate resignationDate) {
    Employee employee =
        findById(employeeRepository, id) // CustomServiceのfindByIdを使用（自動で企業チェック）
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
    employee.setResignationDate(resignationDate);
    save(employeeRepository, employee); // CustomServiceのsaveを使用（自動で企業検証）
  }

  private void applyRequest(Employee employee, EmployeeMasterRequest request) {
    if (request.displayId() != null) {
      employee.setDisplayId(request.displayId());
    }
    employee.setName(request.name().trim());
    employee.setEmploymentType(request.employmentType().trim());
    if (request.gradeId() != null) {
      EmployeeGrade grade =
          findById(employeeGradeRepository, request.gradeId()) // CustomServiceのfindByIdを使用（自動で企業チェック）
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
      employee.setGrade(grade);
    } else {
      employee.setGrade(null);
    }
    if (request.workPatternId() != null) {
      WorkPattern workPattern =
          findById(workPatternRepository, request.workPatternId()) // CustomServiceのfindByIdを使用（自動で企業チェック）
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "勤務パターンが見つかりません"));
      employee.setWorkPattern(workPattern);
    } else {
      employee.setWorkPattern(null);
    }
    if (request.storeId() != null) {
      Store store =
          findById(storeRepository, request.storeId()) // CustomServiceのfindByIdを使用（自動で企業チェック）
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
      employee.setStore(store);
    } else {
      employee.setStore(null);
    }
    employee.setGuaranteedMinimumSalary(request.guaranteedMinimumSalary());
    employee.setManagerAllowance(request.managerAllowance());
    employee.setIsBoardMember(request.isBoardMember() != null ? request.isBoardMember() : false);
    employee.setBoardCompensation(request.boardCompensation());
    employee.setHireDate(request.hireDate());
  }

  private EmployeeMaster toMaster(Employee employee) {
    return new EmployeeMaster(
        employee.getId(),
        employee.getDisplayId(),
        employee.getName(),
        employee.getGrade() != null ? employee.getGrade().getId() : null,
        employee.getGrade() != null ? employee.getGrade().getGradeName() : null,
        employee.getEmploymentType(),
        employee.getWorkPattern() != null ? employee.getWorkPattern().getId() : null,
        employee.getWorkPattern() != null ? employee.getWorkPattern().getPatternName() : null,
        employee.getStore() != null ? employee.getStore().getId() : null,
        employee.getStore() != null ? employee.getStore().getName() : null,
        employee.getGuaranteedMinimumSalary(),
        employee.getManagerAllowance(),
        employee.getIsBoardMember(),
        employee.getBoardCompensation(),
        employee.getHireDate());
  }
}
