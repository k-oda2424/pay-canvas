package com.paycanvas.api.controller;

import com.paycanvas.api.model.PayrollJob;
import com.paycanvas.api.service.PayrollJobService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
  private final PayrollJobService payrollJobService;

  public PayrollController(PayrollJobService payrollJobService) {
    this.payrollJobService = payrollJobService;
  }

  @GetMapping("/jobs")
  public List<PayrollJob> listJobs() {
    return payrollJobService.listJobs();
  }

  @PostMapping("/execute")
  public ResponseEntity<PayrollJob> execute(@RequestBody Map<String, String> payload) {
    String targetMonth = payload.getOrDefault("targetMonth", "");
    if (targetMonth.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(payrollJobService.startJob(targetMonth));
  }
}
