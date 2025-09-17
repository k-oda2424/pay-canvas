package com.paycanvas.api.repository;

import com.paycanvas.api.entity.EmployeeGrade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeGradeRepository extends JpaRepository<EmployeeGrade, Integer> {
  List<EmployeeGrade> findByCompany_Id(Integer companyId);
}
