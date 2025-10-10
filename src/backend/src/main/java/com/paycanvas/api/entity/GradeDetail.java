package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
/**
 * 等級詳細設定を表すエンティティ。
 *
 * <p>等級と勤務パターン、雇用区分の組み合わせごとに基本給係数や歩合率などの
 * 詳細パラメータを保持します。美容室特有の給与計算ロジックにおける
 * 重要なマスタ情報となります。</p>
 */
@Data
@Entity
@Table(name = "m_grade_details")
public class GradeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private EmployeeGrade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_pattern_id")
    private WorkPattern workPattern;

    @Column(name = "employment_type", nullable = false, length = 20)
    private String employmentType;

    @Column(name = "base_salary")
    private Integer baseSalary;

    @Column(name = "base_hourly_wage")
    private Integer baseHourlyWage;

    @Column(name = "late_start_deduction")
    private Integer lateStartDeduction;

    @Column(name = "saturday_bonus")
    private Integer saturdayBonus;

    @Column(name = "sunday_holiday_bonus")
    private Integer sundayHolidayBonus;

    @Column(name = "commission_rate")
    private BigDecimal commissionRate;

    @Column(name = "personal_bonus_rate")
    private BigDecimal personalBonusRate;

    @Column(name = "store_bonus_rate")
    private BigDecimal storeBonusRate;

    @Column(name = "min_productivity")
    private Integer minProductivity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public EmployeeGrade getGrade() {
        return grade;
    }

    public void setGrade(EmployeeGrade grade) {
        this.grade = grade;
    }

    public WorkPattern getWorkPattern() {
        return workPattern;
    }

    public void setWorkPattern(WorkPattern workPattern) {
        this.workPattern = workPattern;
    }

    public String getEmploymentType() {
       return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public Integer getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Integer baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Integer getBaseHourlyWage() {
        return baseHourlyWage;
    }

    public void setBaseHourlyWage(Integer baseHourlyWage) {
        this.baseHourlyWage = baseHourlyWage;
    }

    public Integer getLateStartDeduction() {
        return lateStartDeduction;
    }

    public void setLateStartDeduction(Integer lateStartDeduction) {
        this.lateStartDeduction = lateStartDeduction;
    }

    public Integer getSaturdayBonus() {
        return saturdayBonus;
    }

    public void setSaturdayBonus(Integer saturdayBonus) {
        this.saturdayBonus = saturdayBonus;
    }

    public Integer getSundayHolidayBonus() {
        return sundayHolidayBonus;
    }

    public void setSundayHolidayBonus(Integer sundayHolidayBonus) {
        this.sundayHolidayBonus = sundayHolidayBonus;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getPersonalBonusRate() {
        return personalBonusRate;
    }

    public void setPersonalBonusRate(BigDecimal personalBonusRate) {
        this.personalBonusRate = personalBonusRate;
    }

    public BigDecimal getStoreBonusRate() {
        return storeBonusRate;
    }

    public void setStoreBonusRate(BigDecimal storeBonusRate) {
        this.storeBonusRate = storeBonusRate;
    }

    public Integer getMinProductivity() {
        return minProductivity;
    }

    public void setMinProductivity(Integer minProductivity) {
        this.minProductivity = minProductivity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
