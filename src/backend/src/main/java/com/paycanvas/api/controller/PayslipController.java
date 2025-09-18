package com.paycanvas.api.controller;

import com.paycanvas.api.model.Payslip;
import com.paycanvas.api.service.PayslipService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 給与明細管理コントローラー
 *
 * <p>従業員の給与明細情報を提供するRESTコントローラーです。
 * 指定された月の給与明細データを取得することができます。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/payslips")
public class PayslipController {
  private final PayslipService payslipService;

  /**
   * コンストラクタ
   *
   * @param payslipService 給与明細サービス
   */
  public PayslipController(PayslipService payslipService) {
    this.payslipService = payslipService;
  }

  /**
   * 給与明細一覧を取得
   *
   * <p>指定された月の給与明細データを取得します。
   * 月が指定されない場合は、デフォルトで2024年3月の明細を取得します。</p>
   *
   * @param targetMonth 対象月（YYYY-MM形式）、省略可能
   * @return 指定された月の給与明細リスト
   */
  @GetMapping
  public List<Payslip> list(@RequestParam(name = "targetMonth", required = false) String targetMonth) {
    String month = targetMonth != null ? targetMonth : "2024-03";
    return payslipService.fetchPayslips(month);
  }
}
