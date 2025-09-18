package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.Employee;
import com.paycanvas.api.entity.EmployeeGrade;
import com.paycanvas.api.entity.SalaryTier;
import com.paycanvas.api.model.EmployeeMaster;
import com.paycanvas.api.model.EmployeeMasterRequest;
import com.paycanvas.api.repository.CompanyRepository;
import com.paycanvas.api.repository.EmployeeGradeRepository;
import com.paycanvas.api.repository.EmployeeRepository;
import com.paycanvas.api.repository.SalaryTierRepository;
import com.paycanvas.api.repository.StoreRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {
  private static final int DEFAULT_COMPANY_ID = 1;

  private final EmployeeRepository employeeRepository;
  private final CompanyRepository companyRepository;
  private final EmployeeGradeRepository employeeGradeRepository;
  private final SalaryTierRepository salaryTierRepository;
  private final StoreRepository storeRepository;

  public EmployeeService(
      EmployeeRepository employeeRepository,
      CompanyRepository companyRepository,
      EmployeeGradeRepository employeeGradeRepository,
      SalaryTierRepository salaryTierRepository,
      StoreRepository storeRepository) {
    this.employeeRepository = employeeRepository;
    this.companyRepository = companyRepository;
    this.employeeGradeRepository = employeeGradeRepository;
    this.salaryTierRepository = salaryTierRepository;
    this.storeRepository = storeRepository;
  }

  @Transactional(readOnly = true)
  public List<EmployeeMaster> listEmployees() {
    return employeeRepository.findAllWithRelations().stream().map(this::toMaster).toList();
  }

  @Transactional
  public EmployeeMaster createEmployee(EmployeeMasterRequest request) {
    Employee employee = new Employee();
    employee.setCompany(defaultCompany());
    applyRequest(employee, request);
    Employee saved = employeeRepository.save(employee);
    return employeeRepository
        .findByIdWithRelations(saved.getId())
        .map(this::toMaster)
        .orElseGet(() -> toMaster(saved));
  }

  @Transactional
  public EmployeeMaster updateEmployee(Integer id, EmployeeMasterRequest request) {
    Employee employee =
        employeeRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
    if (employee.getCompany() == null || employee.getCompany().getId() != DEFAULT_COMPANY_ID) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "従業員を更新する権限がありません");
    }
    applyRequest(employee, request);
    Employee saved = employeeRepository.save(employee);
    return employeeRepository
        .findByIdWithRelations(saved.getId())
        .map(this::toMaster)
        .orElseGet(() -> toMaster(saved));
  }

  @Transactional
  public void deleteEmployee(Integer id) {
    Employee employee =
        employeeRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "従業員が見つかりません"));
    if (employee.getCompany() == null || employee.getCompany().getId() != DEFAULT_COMPANY_ID) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "従業員を削除する権限がありません");
    }
    employeeRepository.delete(employee);
  }

  private void applyRequest(Employee employee, EmployeeMasterRequest request) {
    employee.setName(request.name().trim());
    employee.setEmploymentType(request.employmentType().trim());
    if (request.gradeId() != null) {
      EmployeeGrade grade =
          employeeGradeRepository
              .findById(request.gradeId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
      if (grade.getCompany() != null && grade.getCompany().getId() != DEFAULT_COMPANY_ID) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された等級を利用できません");
      }
      employee.setGrade(grade);
    } else {
      employee.setGrade(null);
    }
    if (request.salaryTierId() != null) {
      SalaryTier tier =
          salaryTierRepository
              .findById(request.salaryTierId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "給与プランが見つかりません"));
      if (tier.getCompany() != null && tier.getCompany().getId() != DEFAULT_COMPANY_ID) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された給与プランを利用できません");
      }
      employee.setSalaryTier(tier);
    } else {
      employee.setSalaryTier(null);
    }
    if (request.storeId() != null) {
      var store =
          storeRepository
              .findById(request.storeId())
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
      if (store.getCompany() != null && store.getCompany().getId() != DEFAULT_COMPANY_ID) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された店舗を利用できません");
      }
      employee.setStore(store);
    } else {
      employee.setStore(null);
    }
    employee.setGuaranteedMinimumSalary(request.guaranteedMinimumSalary());
    employee.setManagerAllowance(request.managerAllowance());
  }

  private EmployeeMaster toMaster(Employee employee) {
    return new EmployeeMaster(
        employee.getId(),
        employee.getName(),
        employee.getGrade() != null ? employee.getGrade().getId() : null,
        employee.getGrade() != null ? employee.getGrade().getGradeName() : null,
        employee.getEmploymentType(),
        employee.getSalaryTier() != null ? employee.getSalaryTier().getId() : null,
        employee.getSalaryTier() != null ? employee.getSalaryTier().getPlanName() : null,
        employee.getStore() != null ? employee.getStore().getId() : null,
        employee.getStore() != null ? employee.getStore().getName() : null,
        employee.getGuaranteedMinimumSalary(),
        employee.getManagerAllowance());
  }

  private Company defaultCompany() {
    return companyRepository
        .findById(DEFAULT_COMPANY_ID)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "デフォルトの会社が存在しません"));
  }
}
