package com.paycanvas.api.repository;

import com.paycanvas.api.entity.EmployeeStoreDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 従業員店舗距離マスタのリポジトリインターフェース。
 */
@Repository
public interface EmployeeStoreDistanceRepository extends JpaRepository<EmployeeStoreDistance, Integer> {

  /**
   * 企業IDに基づいて全距離データを取得します。
   *
   * @param companyId 企業ID
   * @return 従業員店舗距離のリスト
   */
  List<EmployeeStoreDistance> findByCompany_Id(Integer companyId);

  /**
   * 指定された従業員IDに紐づく全店舗への距離データを取得します。
   *
   * @param employeeId 従業員ID
   * @param companyId  企業ID
   * @return 従業員店舗距離のリスト
   */
  @Query("SELECT d FROM EmployeeStoreDistance d " +
         "WHERE d.employee.id = :employeeId AND d.company.id = :companyId")
  List<EmployeeStoreDistance> findByEmployeeId(@Param("employeeId") Integer employeeId, @Param("companyId") Integer companyId);

  /**
   * 指定された従業員と店舗の組み合わせに対する距離データを取得します。
   *
   * @param employeeId 従業員ID
   * @param storeId    店舗ID
   * @param companyId  企業ID
   * @return 従業員店舗距離
   */
  @Query("SELECT d FROM EmployeeStoreDistance d " +
         "WHERE d.employee.id = :employeeId AND d.store.id = :storeId AND d.company.id = :companyId")
  Optional<EmployeeStoreDistance> findByEmployeeIdAndStoreId(
      @Param("employeeId") Integer employeeId,
      @Param("storeId") Integer storeId,
      @Param("companyId") Integer companyId);

  /**
   * 指定された店舗IDに紐づく全従業員の距離データを取得します。
   *
   * @param storeId   店舗ID
   * @param companyId 企業ID
   * @return 従業員店舗距離のリスト
   */
  @Query("SELECT d FROM EmployeeStoreDistance d " +
         "WHERE d.store.id = :storeId AND d.company.id = :companyId")
  List<EmployeeStoreDistance> findByStoreId(@Param("storeId") Integer storeId, @Param("companyId") Integer companyId);
}
