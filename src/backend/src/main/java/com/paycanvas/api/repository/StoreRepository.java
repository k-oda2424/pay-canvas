package com.paycanvas.api.repository;

import com.paycanvas.api.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
  List<Store> findByCompany_Id(Integer companyId);
}
