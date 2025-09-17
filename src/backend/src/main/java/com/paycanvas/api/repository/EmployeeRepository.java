package com.paycanvas.api.repository;

import com.paycanvas.api.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
  @Query(
      "select distinct e from Employee e "
          + "left join fetch e.grade "
          + "left join fetch e.salaryTier "
          + "left join fetch e.company")
  List<Employee> findAllWithRelations();
}
