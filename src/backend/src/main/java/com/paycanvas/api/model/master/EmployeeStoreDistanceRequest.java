package com.paycanvas.api.model.master;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 従業員店舗距離マスタの登録・更新リクエストモデル。
 */
public record EmployeeStoreDistanceRequest(
    @NotNull(message = "従業員IDは必須です") Integer employeeId,
    @NotNull(message = "店舗IDは必須です") Integer storeId,
    @NotNull(message = "距離は必須です") @Min(value = 0, message = "距離は0以上である必要があります") BigDecimal distanceKm,
    @NotNull(message = "通勤手段IDは必須です") Integer commuteMethodId) {}
