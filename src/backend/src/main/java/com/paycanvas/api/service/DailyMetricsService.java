package com.paycanvas.api.service;

import com.paycanvas.api.model.DailyAttendance;
import com.paycanvas.api.model.PersonalMetric;
import com.paycanvas.api.model.StoreMetric;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyMetricsService {
  private final JdbcTemplate jdbcTemplate;

  public DailyMetricsService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<DailyAttendance> fetchAttendances() {
    String sql =
        "SELECT a.id, a.work_date, e.name AS staff_name, a.store_name, a.check_in, a.check_out,"
            + " a.work_hours, a.tardy_minutes, a.status"
            + " FROM t_daily_attendances a"
            + " JOIN m_employees e ON e.id = a.employee_id"
            + " ORDER BY a.work_date DESC";
    return jdbcTemplate.query(sql, dailyAttendanceMapper());
  }

  @Transactional(readOnly = true)
  public List<StoreMetric> fetchStoreMetrics() {
    String sql =
        "SELECT id, metric_date, store_name, gross_sales, discount_total, total_hours"
            + " FROM t_daily_store_metrics"
            + " ORDER BY metric_date DESC";
    return jdbcTemplate.query(sql, storeMetricMapper());
  }

  @Transactional(readOnly = true)
  public List<PersonalMetric> fetchPersonalMetrics() {
    String sql =
        "SELECT p.id, p.metric_date, e.name AS staff_name, p.service_sales, p.product_sales"
            + " FROM t_daily_personal_metrics p"
            + " JOIN m_employees e ON e.id = p.employee_id"
            + " ORDER BY p.metric_date DESC";
    return jdbcTemplate.query(sql, personalMetricMapper());
  }

  private RowMapper<DailyAttendance> dailyAttendanceMapper() {
    return new RowMapper<>() {
      @Override
      public DailyAttendance mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DailyAttendance(
            String.valueOf(rs.getInt("id")),
            rs.getDate("work_date").toString(),
            rs.getString("staff_name"),
            rs.getString("store_name"),
            rs.getTime("check_in") != null ? rs.getTime("check_in").toString() : "-",
            rs.getTime("check_out") != null ? rs.getTime("check_out").toString() : "-",
            rs.getInt("work_hours"),
            rs.getInt("tardy_minutes"),
            rs.getString("status"));
      }
    };
  }

  private RowMapper<StoreMetric> storeMetricMapper() {
    return new RowMapper<>() {
      @Override
      public StoreMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StoreMetric(
            String.valueOf(rs.getInt("id")),
            rs.getDate("metric_date").toString(),
            rs.getString("store_name"),
            rs.getInt("gross_sales"),
            rs.getInt("discount_total"),
            rs.getInt("total_hours"));
      }
    };
  }

  private RowMapper<PersonalMetric> personalMetricMapper() {
    return new RowMapper<>() {
      @Override
      public PersonalMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PersonalMetric(
            String.valueOf(rs.getInt("id")),
            rs.getDate("metric_date").toString(),
            rs.getString("staff_name"),
            rs.getInt("service_sales"),
            rs.getInt("product_sales"));
      }
    };
  }
}
