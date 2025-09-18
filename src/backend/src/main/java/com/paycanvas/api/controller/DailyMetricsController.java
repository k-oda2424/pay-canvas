package com.paycanvas.api.controller;

import com.paycanvas.api.model.DailyAttendance;
import com.paycanvas.api.model.PersonalMetric;
import com.paycanvas.api.model.StoreMetric;
import com.paycanvas.api.service.DailyMetricsService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日次メトリクス管理コントローラー
 *
 * <p>日次の勤怠データやメトリクス情報を提供するRESTコントローラーです。
 * 出勤情報、店舗メトリクス、個人メトリクスなどの日次データを取得できます。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/daily")
public class DailyMetricsController {
  private final DailyMetricsService dailyMetricsService;

  /**
   * コンストラクタ
   *
   * @param dailyMetricsService 日次メトリクスサービス
   */
  public DailyMetricsController(DailyMetricsService dailyMetricsService) {
    this.dailyMetricsService = dailyMetricsService;
  }

  /**
   * 日次メトリクス情報を取得
   *
   * <p>勤怠情報、店舗メトリクス、個人メトリクスなどの日次データを集約して取得します。</p>
   *
   * @return 日次メトリクス情報のマップ（キー: attendances, storeMetrics, personalMetrics）
   */
  @GetMapping
  public Map<String, List<?>> list() {
    return Map.of(
        "attendances", dailyMetricsService.fetchAttendances(),
        "storeMetrics", dailyMetricsService.fetchStoreMetrics(),
        "personalMetrics", dailyMetricsService.fetchPersonalMetrics());
  }
}
