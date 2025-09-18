package com.paycanvas.api.controller;

import com.paycanvas.api.model.Announcement;
import com.paycanvas.api.model.PendingTask;
import com.paycanvas.api.model.SummaryMetric;
import com.paycanvas.api.service.DashboardService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ダッシュボード管理コントローラー
 *
 * <p>システムのダッシュボード表示に必要な情報を提供するRESTコントローラーです。
 * サマリメトリクス、保留中のタスク、アナウンスメントなどの情報を集約して提供します。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  /**
   * コンストラクタ
   *
   * @param dashboardService ダッシュボードサービス
   */
  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  /**
   * ダッシュボード用サマリ情報を取得
   *
   * <p>ダッシュボード画面に表示する各種情報を集約して取得します。
   * メトリクス、保留中のタスク、アナウンスメントを含みます。</p>
   *
   * @return サマリ情報のマップ（キー: metrics, tasks, announcements）
   */
  @GetMapping("/summary")
  public Map<String, List<?>> summary() {
    return Map.of(
        "metrics", dashboardService.fetchSummaryMetrics(),
        "tasks", dashboardService.fetchPendingTasks(),
        "announcements", dashboardService.fetchAnnouncements());
  }
}
