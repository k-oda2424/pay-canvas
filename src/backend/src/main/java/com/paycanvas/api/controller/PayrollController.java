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

/**
 * 給与計算管理コントローラー
 *
 * <p>給与計算ジョブの管理を行うRESTコントローラーです。
 * 給与計算ジョブの一覧取得や実行を行うことができます。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
  private final PayrollJobService payrollJobService;

  /**
   * コンストラクタ
   *
   * @param payrollJobService 給与計算ジョブサービス
   */
  public PayrollController(PayrollJobService payrollJobService) {
    this.payrollJobService = payrollJobService;
  }

  /**
   * 給与計算ジョブ一覧を取得
   *
   * <p>システムに登録されている給与計算ジョブの一覧を取得します。</p>
   *
   * @return 給与計算ジョブのリスト
   */
  @GetMapping("/jobs")
  public List<PayrollJob> listJobs() {
    return payrollJobService.listJobs();
  }

  /**
   * 給与計算ジョブを実行
   *
   * <p>指定された月の給与計算ジョブを開始します。
   * 対象月が指定されていない場合は、400エラーを返却します。</p>
   *
   * @param payload リクエストペイロード（targetMonthフィールドを含む）
   * @return 開始された給与計算ジョブ情報、または400エラー
   */
  @PostMapping("/execute")
  public ResponseEntity<PayrollJob> execute(@RequestBody Map<String, String> payload) {
    String targetMonth = payload.getOrDefault("targetMonth", "");
    if (targetMonth.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(payrollJobService.startJob(targetMonth));
  }
}
