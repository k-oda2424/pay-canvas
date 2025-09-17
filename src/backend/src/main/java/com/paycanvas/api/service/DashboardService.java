package com.paycanvas.api.service;

import com.paycanvas.api.model.Announcement;
import com.paycanvas.api.model.PendingTask;
import com.paycanvas.api.model.SummaryMetric;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
  private final JdbcTemplate jdbcTemplate;
  private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.JAPAN);

  public DashboardService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<SummaryMetric> fetchSummaryMetrics() {
    BigDecimal laborCostValue =
        defaultDecimal(
            jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(gross_pay),0) FROM t_monthly_payrolls", BigDecimal.class));
    BigDecimal totalSalesValue =
        defaultDecimal(
            jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(gross_sales),0) FROM t_daily_store_metrics", BigDecimal.class));
    BigDecimal productRatioValue =
        defaultDecimal(
            jdbcTemplate.queryForObject(
                "SELECT CASE WHEN SUM(service_sales)+SUM(product_sales) = 0 THEN 0"
                    + " ELSE ROUND(SUM(product_sales)::numeric * 100 / (SUM(service_sales)+SUM(product_sales)), 1)"
                    + " END FROM t_daily_personal_metrics",
                BigDecimal.class));

    String totalLaborCost = currencyFormat.format(laborCostValue);
    String totalSales = currencyFormat.format(totalSalesValue);
    String productRatio = productRatioValue + "%";
    long pendingAttendance =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM t_daily_attendances WHERE status <> '承認済'",
            Long.class);

    return List.of(
        new SummaryMetric("laborCost", "総人件費 (今月)", totalLaborCost, "+5.2% vs 先月", false),
        new SummaryMetric("sales", "総売上 (今月)", totalSales, "+8.4% vs 先月", true),
        new SummaryMetric("product", "商品販売率", productRatio, "+1.2pt", true),
        new SummaryMetric("attendance", "勤怠未承認", pendingAttendance + "件", "-3件", pendingAttendance == 0));
  }

  @Transactional(readOnly = true)
  public List<PendingTask> fetchPendingTasks() {
    return List.of(
        new PendingTask(
            "t1",
            "4月度勤怠未入力の確認",
            "要確認の勤怠が" + pendingAttendanceCount() + "件あります",
            Optional.of(LocalDate.of(2024, 4, 25))),
        new PendingTask(
            "t2",
            "給与テーブル改定の承認",
            "新しい週休3日プランの承認が必要です",
            Optional.empty()));
  }

  @Transactional(readOnly = true)
  public List<Announcement> fetchAnnouncements() {
    return List.of(
        new Announcement("a1", "KING OF TIME API の仕様変更に伴う接続テストが予定されています", LocalDate.of(2024, 4, 8)),
        new Announcement("a2", "弥生給与向けCSVフォーマットを4月15日に更新予定", LocalDate.of(2024, 4, 5)));
  }

  private long pendingAttendanceCount() {
    return jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM t_daily_attendances WHERE status <> '承認済'", Long.class);
  }

  private BigDecimal defaultDecimal(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
  }
}
