package com.paycanvas.api.repository;

import com.paycanvas.api.entity.TransportationCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 交通費マスターのリポジトリインターフェース。
 *
 * <p>従業員の交通費情報に対するデータベース操作を提供します。</p>
 */
@Repository
public interface TransportationCostRepository extends JpaRepository<TransportationCost, Integer> {

  /**
   * 従業員IDで交通費情報を検索します。
   *
   * @param employeeId 従業員ID
   * @return 交通費情報のリスト
   */
  List<TransportationCost> findByEmployee_IdOrderByEffectiveFromDesc(Integer employeeId);

  /**
   * 特定の日付時点で有効な交通費情報を取得します。
   *
   * @param employeeId 従業員ID
   * @param targetDate 対象日付
   * @return 有効な交通費情報
   */
  @Query("SELECT tc FROM TransportationCost tc WHERE tc.employee.id = :employeeId " +
         "AND tc.effectiveFrom <= :targetDate " +
         "AND (tc.effectiveTo IS NULL OR tc.effectiveTo >= :targetDate)")
  Optional<TransportationCost> findActiveByEmployeeIdAndDate(
      @Param("employeeId") Integer employeeId,
      @Param("targetDate") LocalDate targetDate);

  /**
   * 企業IDで交通費情報を検索します。
   *
   * @param companyId 企業ID
   * @return 交通費情報のリスト
   */
  @Query("SELECT tc FROM TransportationCost tc WHERE tc.employee.company.id = :companyId " +
         "ORDER BY tc.employee.id, tc.effectiveFrom DESC")
  List<TransportationCost> findByCompanyId(@Param("companyId") Integer companyId);
}
