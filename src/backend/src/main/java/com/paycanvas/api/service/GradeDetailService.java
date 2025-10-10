package com.paycanvas.api.service;

import com.paycanvas.api.entity.EmployeeGrade;
import com.paycanvas.api.entity.GradeDetail;
import com.paycanvas.api.entity.WorkPattern;
import com.paycanvas.api.exception.ValidationException;
import com.paycanvas.api.model.master.GradeDetailRequest;
import com.paycanvas.api.model.master.GradeDetailResponse;
import com.paycanvas.api.repository.EmployeeGradeRepository;
import com.paycanvas.api.repository.GradeDetailRepository;
import com.paycanvas.api.repository.WorkPatternRepository;
import com.paycanvas.api.security.UserPrincipal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 等級詳細設定のビジネスロジックを管理するサービスクラス。
 *
 * <p>等級詳細は等級エンティティを通じて企業スコープで管理されます。</p>
 */
@Service
@Transactional
public class GradeDetailService {

    private final GradeDetailRepository gradeDetailRepository;
    private final EmployeeGradeRepository employeeGradeRepository;
    private final WorkPatternRepository workPatternRepository;

    public GradeDetailService(
            GradeDetailRepository gradeDetailRepository,
            EmployeeGradeRepository employeeGradeRepository,
            WorkPatternRepository workPatternRepository) {
        this.gradeDetailRepository = gradeDetailRepository;
        this.employeeGradeRepository = employeeGradeRepository;
        this.workPatternRepository = workPatternRepository;
    }

    /**
     * 現在ログイン中のユーザーの企業IDを取得します。
     *
     * @return ログイン中のユーザーの企業ID
     */
    private Integer getCurrentCompanyId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "認証情報が見つかりません");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Integer companyId = principal.getCompanyId();
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "企業情報が見つかりません");
        }
        return companyId;
    }

    /**
     * 等級詳細一覧を企業スコープで取得します。
     *
     * <p>現在ログイン中の企業の等級詳細のみを取得します。</p>
     *
     * @param gradeId 等級ID（nullの場合は全等級詳細を取得）
     * @return 等級詳細レスポンスのリスト
     */
    @Transactional(readOnly = true)
    public List<GradeDetailResponse> findAll(Integer gradeId) {
        Integer companyId = getCurrentCompanyId();
        if (gradeId != null) {
            // 等級が自企業のものか検証
            ensureGradeBelongsToCompany(gradeId);
            return gradeDetailRepository.findByGrade_IdOrderByWorkPattern_IdAsc(gradeId).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return gradeDetailRepository.findByGrade_Company_IdOrderByGrade_IdAsc(companyId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * IDによる等級詳細の取得。
     *
     * <p>等級詳細が現在ログイン中の企業のものか検証します。</p>
     *
     * @param id 等級詳細ID
     * @return 等級詳細レスポンス
     */
    public GradeDetailResponse findById(Integer id) {
        GradeDetail gradeDetail = gradeDetailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級詳細が見つかりません"));
        ensureGradeBelongsToCompany(gradeDetail.getGrade());
        return toResponse(gradeDetail);
    }

    /**
     * 新規等級詳細の作成。
     *
     * <p>等級が現在ログイン中の企業のものか検証します。</p>
     *
     * @param request 等級詳細リクエスト
     * @return 等級詳細レスポンス
     */
    public GradeDetailResponse create(GradeDetailRequest request) {
        validateRequest(request);
        validateUniqueness(request, null);

        GradeDetail gradeDetail = new GradeDetail();
        updateEntity(gradeDetail, request);

        GradeDetail saved = gradeDetailRepository.save(gradeDetail);
        return toResponse(saved);
    }

    /**
     * 既存等級詳細の更新。
     *
     * <p>等級詳細が現在ログイン中の企業のものか検証します。</p>
     *
     * @param id 等級詳細ID
     * @param request 等級詳細リクエスト
     * @return 等級詳細レスポンス
     */
    public GradeDetailResponse update(Integer id, GradeDetailRequest request) {
        GradeDetail gradeDetail = gradeDetailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級詳細が見つかりません"));
        ensureGradeBelongsToCompany(gradeDetail.getGrade());

        validateRequest(request);
        validateUniqueness(request, id);

        updateEntity(gradeDetail, request);

        GradeDetail saved = gradeDetailRepository.save(gradeDetail);
        return toResponse(saved);
    }

    /**
     * 等級詳細の削除。
     *
     * <p>等級詳細が現在ログイン中の企業のものか検証します。</p>
     *
     * @param id 等級詳細ID
     */
    public void delete(Integer id) {
        GradeDetail detail = gradeDetailRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級詳細が見つかりません"));
        ensureGradeBelongsToCompany(detail.getGrade());
        gradeDetailRepository.deleteById(id);
    }

    /**
     * リクエストの検証
     */
    private void validateRequest(GradeDetailRequest request) {
        // 雇用区分による必須フィールドチェック
        if ("FULL_TIME".equalsIgnoreCase(request.employmentType())) {
            if (request.baseSalary() == null || request.baseSalary() <= 0) {
                throw new ValidationException("基本給は正社員の場合必須です");
            }
        } else if ("PART_TIME".equalsIgnoreCase(request.employmentType())) {
            if (request.baseHourlyWage() == null || request.baseHourlyWage() <= 0) {
                throw new ValidationException("時給はパートタイムの場合必須です");
            }
        }

        // 歩合率の範囲チェック
        if (request.commissionRate() != null) {
            if (request.commissionRate().doubleValue() < 0 ||
                request.commissionRate().doubleValue() > 1) {
                throw new ValidationException("歩合率は0.000～1.000の範囲で指定してください");
            }
        }

        // 賞与率の範囲チェック
        if (request.personalBonusRate() != null) {
            if (request.personalBonusRate().doubleValue() < 0 ||
                request.personalBonusRate().doubleValue() > 1) {
                throw new ValidationException("個人賞与率は0.000～1.000の範囲で指定してください");
            }
        }

        if (request.storeBonusRate() != null) {
            if (request.storeBonusRate().doubleValue() < 0 ||
                request.storeBonusRate().doubleValue() > 1) {
                throw new ValidationException("店舗賞与率は0.000～1.000の範囲で指定してください");
            }
        }
    }

    /**
     * 一意性制約の検証
     */
    private void validateUniqueness(GradeDetailRequest request, Integer excludeId) {
        Optional<GradeDetail> existing;

        if (request.workPatternId() != null) {
            existing = gradeDetailRepository
                    .findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPattern_Id(
                            request.gradeId(),
                            request.employmentType(),
                            request.workPatternId());
        } else {
            existing = gradeDetailRepository
                    .findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPatternIsNull(
                            request.gradeId(),
                            request.employmentType());
        }

        if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
            throw new ValidationException(
                    "同一等級・雇用区分・勤務パターンの組み合わせは既に存在します");
        }
    }

    /**
     * エンティティの更新。
     *
     * <p>等級と勤務パターンが現在ログイン中の企業のものか検証します。</p>
     *
     * @param gradeDetail 更新対象の等級詳細エンティティ
     * @param request 等級詳細リクエスト
     */
    private void updateEntity(GradeDetail gradeDetail, GradeDetailRequest request) {
        EmployeeGrade grade = employeeGradeRepository.findById(request.gradeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
        ensureGradeBelongsToCompany(grade);
        gradeDetail.setGrade(grade);

        if (request.workPatternId() != null) {
            WorkPattern workPattern = workPatternRepository.findById(request.workPatternId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "勤務パターンが見つかりません"));
            ensureWorkPatternBelongsToCompany(workPattern);
            gradeDetail.setWorkPattern(workPattern);
        } else {
            gradeDetail.setWorkPattern(null);
        }

        gradeDetail.setEmploymentType(request.employmentType());
        gradeDetail.setBaseSalary(request.baseSalary());
        gradeDetail.setBaseHourlyWage(request.baseHourlyWage());
        gradeDetail.setCommissionRate(request.commissionRate());
        gradeDetail.setPersonalBonusRate(request.personalBonusRate());
        gradeDetail.setStoreBonusRate(request.storeBonusRate());
        gradeDetail.setLateStartDeduction(request.lateStartDeduction());
        gradeDetail.setSaturdayBonus(request.saturdayBonus());
        gradeDetail.setSundayHolidayBonus(request.sundayHolidayBonus());
        gradeDetail.setMinProductivity(request.minProductivity());
    }

    /**
     * エンティティからレスポンスDTOへの変換
     */
    private GradeDetailResponse toResponse(GradeDetail gradeDetail) {
        return new GradeDetailResponse(
                gradeDetail.getId(),
                gradeDetail.getGrade().getId(),
                gradeDetail.getGrade().getGradeName(),
                gradeDetail.getWorkPattern() != null ? gradeDetail.getWorkPattern().getId() : null,
                gradeDetail.getWorkPattern() != null ? gradeDetail.getWorkPattern().getPatternName() : null,
                gradeDetail.getEmploymentType(),
                gradeDetail.getBaseSalary(),
                gradeDetail.getBaseHourlyWage(),
                gradeDetail.getLateStartDeduction(),
                gradeDetail.getSaturdayBonus(),
                gradeDetail.getSundayHolidayBonus(),
                gradeDetail.getCommissionRate(),
                gradeDetail.getPersonalBonusRate(),
                gradeDetail.getStoreBonusRate(),
                gradeDetail.getMinProductivity()
        );
    }

    /**
     * 推奨される等級詳細を取得します。
     *
     * <p>等級ID、勤務パターンID、雇用区分に基づいて、最適な等級詳細を検索します。</p>
     *
     * @param gradeId 等級ID
     * @param workPatternId 勤務パターンID（省略可）
     * @param employmentType 雇用区分
     * @return 等級詳細レスポンス（存在しない場合はempty）
     */
    @Transactional(readOnly = true)
    public Optional<GradeDetailResponse> findRecommendation(Integer gradeId, Integer workPatternId, String employmentType) {
        if (gradeId == null || employmentType == null || employmentType.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "等級IDと雇用区分は必須です");
        }

        // 等級の存在と企業スコープを検証
        EmployeeGrade grade = employeeGradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
        ensureGradeBelongsToCompany(grade);

        Optional<GradeDetail> detail = Optional.empty();
        if (workPatternId != null) {
            detail = gradeDetailRepository
                    .findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPattern_Id(
                            gradeId,
                            employmentType,
                            workPatternId);
        }

        if (detail.isEmpty()) {
            detail = gradeDetailRepository
                    .findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPatternIsNull(
                            gradeId,
                            employmentType);
        }

        return detail.map(this::toResponse);
    }

    /**
     * 等級IDが現在ログイン中の企業に属するか検証します。
     *
     * @param gradeId 等級ID
     */
    private void ensureGradeBelongsToCompany(Integer gradeId) {
        Integer companyId = getCurrentCompanyId();
        employeeGradeRepository.findById(gradeId)
                .filter(grade -> grade.getCompany() != null && grade.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された等級を利用できません"));
    }

    /**
     * 等級が現在ログイン中の企業に属するか検証します。
     *
     * @param grade 等級エンティティ
     */
    private void ensureGradeBelongsToCompany(EmployeeGrade grade) {
        Integer companyId = getCurrentCompanyId();
        if (grade.getCompany() == null || !grade.getCompany().getId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された等級を利用できません");
        }
    }

    /**
     * 勤務パターンが現在ログイン中の企業に属するか検証します。
     *
     * @param workPattern 勤務パターンエンティティ
     */
    private void ensureWorkPatternBelongsToCompany(WorkPattern workPattern) {
        Integer companyId = getCurrentCompanyId();
        if (workPattern.getCompany() == null || !workPattern.getCompany().getId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "指定された勤務パターンを利用できません");
        }
    }
}
