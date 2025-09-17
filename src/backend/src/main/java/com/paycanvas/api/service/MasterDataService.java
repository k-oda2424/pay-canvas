package com.paycanvas.api.service;

import com.paycanvas.api.entity.Company;
import com.paycanvas.api.entity.EmployeeGrade;
import com.paycanvas.api.entity.SalaryTier;
import com.paycanvas.api.entity.Store;
import com.paycanvas.api.model.master.GradeRequest;
import com.paycanvas.api.model.master.GradeResponse;
import com.paycanvas.api.model.master.SalaryTierRequest;
import com.paycanvas.api.model.master.SalaryTierResponse;
import com.paycanvas.api.model.master.StoreRequest;
import com.paycanvas.api.model.master.StoreResponse;
import com.paycanvas.api.repository.CompanyRepository;
import com.paycanvas.api.repository.EmployeeGradeRepository;
import com.paycanvas.api.repository.SalaryTierRepository;
import com.paycanvas.api.repository.StoreRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class MasterDataService {
  private static final int DEFAULT_COMPANY_ID = 1;

  private final CompanyRepository companyRepository;
  private final StoreRepository storeRepository;
  private final EmployeeGradeRepository employeeGradeRepository;
  private final SalaryTierRepository salaryTierRepository;

  public MasterDataService(
      CompanyRepository companyRepository,
      StoreRepository storeRepository,
      EmployeeGradeRepository employeeGradeRepository,
      SalaryTierRepository salaryTierRepository) {
    this.companyRepository = companyRepository;
    this.storeRepository = storeRepository;
    this.employeeGradeRepository = employeeGradeRepository;
    this.salaryTierRepository = salaryTierRepository;
  }

  private Company defaultCompany() {
    return companyRepository
        .findById(DEFAULT_COMPANY_ID)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "デフォルトの会社が存在しません"));
  }

  // --- Stores ---
  @Transactional(readOnly = true)
  public List<StoreResponse> listStores() {
    return storeRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(store -> new StoreResponse(store.getId(), store.getName(), store.getStoreType(), store.getAddress()))
        .sorted(Comparator.comparing(StoreResponse::id))
        .toList();
  }

  @Transactional
  public StoreResponse createStore(StoreRequest request) {
    Store store = new Store();
    store.setCompany(defaultCompany());
    store.setName(request.name());
    store.setStoreType(request.storeType());
    store.setAddress(request.address());
    Store saved = storeRepository.save(store);
    return new StoreResponse(saved.getId(), saved.getName(), saved.getStoreType(), saved.getAddress());
  }

  @Transactional
  public StoreResponse updateStore(Integer id, StoreRequest request) {
    Store store =
        storeRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません"));
    store.setName(request.name());
    store.setStoreType(request.storeType());
    store.setAddress(request.address());
    Store saved = storeRepository.save(store);
    return new StoreResponse(saved.getId(), saved.getName(), saved.getStoreType(), saved.getAddress());
  }

  @Transactional
  public void deleteStore(Integer id) {
    if (!storeRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "店舗が見つかりません");
    }
    storeRepository.deleteById(id);
  }

  // --- Grades ---
  @Transactional(readOnly = true)
  public List<GradeResponse> listGrades() {
    return employeeGradeRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(grade -> new GradeResponse(grade.getId(), grade.getGradeName(), grade.getCommissionRate()))
        .toList();
  }

  @Transactional
  public GradeResponse createGrade(GradeRequest request) {
    EmployeeGrade grade = new EmployeeGrade();
    grade.setCompany(defaultCompany());
    grade.setGradeName(request.gradeName());
    grade.setCommissionRate(percentToDecimal(request.commissionRatePercent()));
    EmployeeGrade saved = employeeGradeRepository.save(grade);
    return new GradeResponse(saved.getId(), saved.getGradeName(), saved.getCommissionRate());
  }

  @Transactional
  public GradeResponse updateGrade(Integer id, GradeRequest request) {
    EmployeeGrade grade =
        employeeGradeRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません"));
    grade.setGradeName(request.gradeName());
    grade.setCommissionRate(percentToDecimal(request.commissionRatePercent()));
    EmployeeGrade saved = employeeGradeRepository.save(grade);
    return new GradeResponse(saved.getId(), saved.getGradeName(), saved.getCommissionRate());
  }

  @Transactional
  public void deleteGrade(Integer id) {
    if (!employeeGradeRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "等級が見つかりません");
    }
    employeeGradeRepository.deleteById(id);
  }

  // --- Salary Tiers ---
  @Transactional(readOnly = true)
  public List<SalaryTierResponse> listSalaryTiers() {
    return salaryTierRepository.findByCompany_Id(DEFAULT_COMPANY_ID).stream()
        .map(tier -> new SalaryTierResponse(tier.getId(), tier.getPlanName(), tier.getMonthlyDaysOff(), tier.getBaseSalary()))
        .toList();
  }

  @Transactional
  public SalaryTierResponse createSalaryTier(SalaryTierRequest request) {
    SalaryTier tier = new SalaryTier();
    tier.setCompany(defaultCompany());
    tier.setPlanName(request.planName());
    tier.setMonthlyDaysOff(request.monthlyDaysOff());
    tier.setBaseSalary(request.baseSalary());
    SalaryTier saved = salaryTierRepository.save(tier);
    return new SalaryTierResponse(saved.getId(), saved.getPlanName(), saved.getMonthlyDaysOff(), saved.getBaseSalary());
  }

  @Transactional
  public SalaryTierResponse updateSalaryTier(Integer id, SalaryTierRequest request) {
    SalaryTier tier =
        salaryTierRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "給与プランが見つかりません"));
    tier.setPlanName(request.planName());
    tier.setMonthlyDaysOff(request.monthlyDaysOff());
    tier.setBaseSalary(request.baseSalary());
    SalaryTier saved = salaryTierRepository.save(tier);
    return new SalaryTierResponse(saved.getId(), saved.getPlanName(), saved.getMonthlyDaysOff(), saved.getBaseSalary());
  }

  @Transactional
  public void deleteSalaryTier(Integer id) {
    if (!salaryTierRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "給与プランが見つかりません");
    }
    salaryTierRepository.deleteById(id);
  }

  private double percentToDecimal(BigDecimal percent) {
    return percent.divide(BigDecimal.valueOf(100)).doubleValue();
  }
}
