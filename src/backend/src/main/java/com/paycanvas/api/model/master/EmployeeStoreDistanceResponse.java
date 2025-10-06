package com.paycanvas.api.model.master;

import java.math.BigDecimal;

/**
 * 従業員店舗距離マスタのレスポンスモデル。
 */
public record EmployeeStoreDistanceResponse(
    Integer id,
    Integer employeeId,
    String employeeName,
    Integer storeId,
    String storeName,
    BigDecimal distanceKm,
    Integer commuteMethodId,
    String commuteMethodName) {}
