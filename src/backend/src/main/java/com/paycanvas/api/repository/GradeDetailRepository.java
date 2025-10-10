package com.paycanvas.api.repository;

import com.paycanvas.api.entity.GradeDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 等級詳細マスタのリポジトリ。
 */
public interface GradeDetailRepository extends JpaRepository<GradeDetail, Integer> {

    List<GradeDetail> findByGrade_Company_IdOrderByGrade_IdAsc(Integer companyId);

    List<GradeDetail> findByGrade_IdOrderByWorkPattern_IdAsc(Integer gradeId);

    List<GradeDetail> findByGrade_Id(Integer gradeId);

    Optional<GradeDetail> findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPattern_Id(
        Integer gradeId,
        String employmentType,
        Integer workPatternId);

    Optional<GradeDetail> findFirstByGrade_IdAndEmploymentTypeIgnoreCaseAndWorkPatternIsNull(
        Integer gradeId,
        String employmentType);
}
