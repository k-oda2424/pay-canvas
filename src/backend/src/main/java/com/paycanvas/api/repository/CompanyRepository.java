package com.paycanvas.api.repository;

import com.paycanvas.api.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {}
