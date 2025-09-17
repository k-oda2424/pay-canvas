package com.paycanvas.api.controller;

import com.paycanvas.api.model.Payslip;
import com.paycanvas.api.service.PayslipService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {
  private final PayslipService payslipService;

  public PayslipController(PayslipService payslipService) {
    this.payslipService = payslipService;
  }

  @GetMapping
  public List<Payslip> list(@RequestParam(name = "targetMonth", required = false) String targetMonth) {
    String month = targetMonth != null ? targetMonth : "2024-03";
    return payslipService.fetchPayslips(month);
  }
}
