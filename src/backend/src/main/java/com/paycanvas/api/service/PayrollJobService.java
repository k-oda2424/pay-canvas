package com.paycanvas.api.service;

import com.paycanvas.api.model.PayrollJob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollJobService {
  private final JdbcTemplate jdbcTemplate;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public PayrollJobService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<PayrollJob> listJobs() {
    String sql =
        "SELECT job_key, target_month, status, progress, started_at"
            + " FROM t_payroll_jobs ORDER BY started_at DESC";
    return jdbcTemplate.query(sql, jobMapper());
  }

  @Transactional
  public PayrollJob startJob(String targetMonth) {
    String jobKey = targetMonth;
    jdbcTemplate.update(
        "INSERT INTO t_payroll_jobs (job_key, target_month, status, progress, started_at)"
            + " VALUES (?, ?, 'RUNNING', 30, ?)"
            + " ON CONFLICT (job_key) DO UPDATE"
            + " SET status = EXCLUDED.status, progress = EXCLUDED.progress, started_at = EXCLUDED.started_at",
        jobKey,
        targetMonth,
        LocalDateTime.now());
    return jdbcTemplate.queryForObject(
        "SELECT job_key, target_month, status, progress, started_at FROM t_payroll_jobs WHERE job_key = ?",
        jobMapper(),
        jobKey);
  }

  private RowMapper<PayrollJob> jobMapper() {
    return new RowMapper<>() {
      @Override
      public PayrollJob mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDateTime started = rs.getTimestamp("started_at").toLocalDateTime();
        return new PayrollJob(
            rs.getString("job_key"),
            rs.getString("target_month"),
            rs.getString("status"),
            rs.getInt("progress"),
            started.format(formatter));
      }
    };
  }
}
