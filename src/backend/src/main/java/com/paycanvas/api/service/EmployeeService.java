package com.paycanvas.api.service;

import com.paycanvas.api.entity.Employee;
import com.paycanvas.api.model.EmployeeMaster;
import com.paycanvas.api.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Transactional(readOnly = true)
  public List<EmployeeMaster> listEmployees() {
    return employeeRepository.findAllWithRelations().stream()
        .map(
            employee ->
                new EmployeeMaster(
                    employee.getId(),
                    employee.getName(),
                    employee.getGrade() != null ? employee.getGrade().getGradeName() : "-",
                    employee.getEmploymentType(),
                    employee.getSalaryTier() != null ? employee.getSalaryTier().getPlanName() : "-",
                    employee.getStoreName() != null ? employee.getStoreName() : "-"))
        .toList();
  }
}
