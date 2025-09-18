package com.paycanvas.api.controller;

import com.paycanvas.api.model.FeatureToggle;
import com.paycanvas.api.service.FeatureToggleService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 機能トグル管理コントローラー
 *
 * <p>システム内の機能フラグ（フィーチャートグル）の取得と更新を行うRESTコントローラーです。
 * 機能の有効/無効を動的に切り替えることができます。</p>
 *
 * @author Pay Canvas Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/features")
public class FeatureToggleController {
  private final FeatureToggleService featureToggleService;

  /**
   * コンストラクタ
   *
   * @param featureToggleService 機能トグルサービス
   */
  public FeatureToggleController(FeatureToggleService featureToggleService) {
    this.featureToggleService = featureToggleService;
  }

  /**
   * 全ての機能トグル一覧を取得
   *
   * <p>システム内の全ての機能フラグとその現在の状態を取得します。</p>
   *
   * @return 機能トグルのリスト
   */
  @GetMapping
  public List<FeatureToggle> list() {
    return featureToggleService.listFeatureToggles();
  }

  /**
   * 機能トグルの状態を更新
   *
   * <p>指定されたIDの機能トグルの有効/無効状態を更新します。</p>
   *
   * @param id 更新対象の機能トグルID
   * @param payload 更新内容（isEnabledフィールドを含むマップ）
   * @return 更新された機能トグル情報、またはエラーレスポンス
   */
  @PatchMapping("/{id}")
  public ResponseEntity<FeatureToggle> update(@PathVariable String id, @RequestBody Map<String, Boolean> payload) {
    Boolean isEnabled = payload.get("isEnabled");
    if (isEnabled == null) {
      return ResponseEntity.badRequest().build();
    }
    try {
      return ResponseEntity.ok(featureToggleService.updateFeatureToggle(id, isEnabled));
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
