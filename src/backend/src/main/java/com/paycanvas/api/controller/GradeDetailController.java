package com.paycanvas.api.controller;

import com.paycanvas.api.model.master.GradeDetailRequest;
import com.paycanvas.api.model.master.GradeDetailResponse;
import com.paycanvas.api.service.GradeDetailService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 等級詳細設定のRESTコントローラー
 *
 * <p>等級に紐づく詳細設定（雇用区分・勤務パターン別の給与設定）のCRUD操作を提供します。
 * Phase2の等級マスター拡張に対応し、ページネーションや詳細検証に対応しています。</p>
 *
 * @author Pay Canvas Team
 * @since Phase 2
 */
@RestController
@RequestMapping("/api/grade-details")
public class GradeDetailController {

    private final GradeDetailService gradeDetailService;

    public GradeDetailController(GradeDetailService gradeDetailService) {
        this.gradeDetailService = gradeDetailService;
    }

    public record GradeDetailListResponse(List<GradeDetailResponse> items) {}

    /**
     * 等級詳細設定一覧の取得（ページネーション対応）
     *
     * @param pageable ページネーション情報（デフォルト: page=0, size=20）
     * @return ページ化された等級詳細設定のリスト
     */
    @GetMapping
    public ResponseEntity<GradeDetailListResponse> findAll(
            @RequestParam(name = "gradeId", required = false) Integer gradeId) {
        List<GradeDetailResponse> gradeDetails = gradeDetailService.findAll(gradeId);
        return ResponseEntity.ok(new GradeDetailListResponse(gradeDetails));
    }

    /**
     * 等級・雇用区分・勤務パターンに基づく推奨設定を取得
     */
    @GetMapping("/recommendation")
    public ResponseEntity<GradeDetailResponse> findRecommendation(
            @RequestParam Integer gradeId,
            @RequestParam(required = false) Integer workPatternId,
            @RequestParam String employmentType) {
        return gradeDetailService
                .findRecommendation(gradeId, workPatternId, employmentType)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "該当する等級詳細が見つかりません"));
    }

    /**
     * IDによる等級詳細設定の取得
     *
     * @param id 等級詳細設定ID
     * @return 等級詳細設定情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<GradeDetailResponse> findById(@PathVariable Integer id) {
        GradeDetailResponse gradeDetail = gradeDetailService.findById(id);
        return ResponseEntity.ok(gradeDetail);
    }

    /**
     * 新規等級詳細設定の作成
     *
     * <p>雇用区分に応じた必須フィールドチェックと一意制約チェックを実施します。</p>
     *
     * @param request 等級詳細設定作成リクエスト
     * @return 作成された等級詳細設定情報
     */
    @PostMapping
    public ResponseEntity<GradeDetailResponse> create(
            @Valid @RequestBody GradeDetailRequest request) {
        GradeDetailResponse gradeDetail = gradeDetailService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeDetail);
    }

    /**
     * 既存等級詳細設定の更新
     *
     * <p>更新時も作成時と同様の検証を実施します。</p>
     *
     * @param id 更新対象の等級詳細設定ID
     * @param request 等級詳細設定更新リクエスト
     * @return 更新された等級詳細設定情報
     */
    @PutMapping("/{id}")
    public ResponseEntity<GradeDetailResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody GradeDetailRequest request) {
        GradeDetailResponse gradeDetail = gradeDetailService.update(id, request);
        return ResponseEntity.ok(gradeDetail);
    }

    /**
     * 等級詳細設定の削除
     *
     * @param id 削除対象の等級詳細設定ID
     * @return 削除完了レスポンス（204 No Content）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        gradeDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
