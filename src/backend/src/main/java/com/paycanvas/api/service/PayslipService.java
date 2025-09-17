package com.paycanvas.api.service;

import com.paycanvas.api.model.Payslip;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayslipService {
  private final JdbcTemplate jdbcTemplate;

  public PayslipService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional(readOnly = true)
  public List<Payslip> fetchPayslips(String targetMonth) {
    String sql =
        "SELECT p.employee_id, e.name, COALESCE(g.grade_name, e.employment_type) AS role,"
            + " p.base_salary, p.allowance_total,"
            + " p.deduction_total, p.net_pay, p.status"
            + " FROM t_monthly_payrolls p"
            + " JOIN m_employees e ON e.id = p.employee_id"
            + " LEFT JOIN m_employee_grades g ON g.id = e.grade_id"
            + " WHERE p.target_year_month = ?"
            + " ORDER BY e.name";
    return jdbcTemplate.query(sql, payslipMapper(), targetMonth);
  }

  private RowMapper<Payslip> payslipMapper() {
    return new RowMapper<>() {
      @Override
      public Payslip mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Payslip(
            String.valueOf(rs.getInt("employee_id")),
            rs.getString("name"),
            rs.getString("role"),
            rs.getInt("base_salary"),
            rs.getInt("allowance_total"),
            rs.getInt("deduction_total"),
            rs.getInt("net_pay"),
            rs.getString("status").equalsIgnoreCase("CONFIRMED") ? "確定" : "ステージング");
      }
    };
  }
}
