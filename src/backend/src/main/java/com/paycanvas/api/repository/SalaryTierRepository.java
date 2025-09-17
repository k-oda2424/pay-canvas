package com.paycanvas.api.repository;

import com.paycanvas.api.entity.SalaryTier;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryTierRepository extends JpaRepository<SalaryTier, Integer> {
  List<SalaryTier> findByCompany_Id(Integer companyId);
}
