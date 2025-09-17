package com.paycanvas.api.controller;

import com.paycanvas.api.model.master.GradeRequest;
import com.paycanvas.api.model.master.GradeResponse;
import com.paycanvas.api.model.master.SalaryTierRequest;
import com.paycanvas.api.model.master.SalaryTierResponse;
import com.paycanvas.api.model.master.StoreRequest;
import com.paycanvas.api.model.master.StoreResponse;
import com.paycanvas.api.service.MasterDataService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/masters")
public class MasterDataController {
  private final MasterDataService masterDataService;

  public MasterDataController(MasterDataService masterDataService) {
    this.masterDataService = masterDataService;
  }

  // Stores
  @GetMapping("/stores")
  public List<StoreResponse> listStores() {
    return masterDataService.listStores();
  }

  @PostMapping("/stores")
  public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request) {
    return ResponseEntity.ok(masterDataService.createStore(request));
  }

  @PutMapping("/stores/{id}")
  public ResponseEntity<StoreResponse> updateStore(
      @PathVariable Integer id, @Valid @RequestBody StoreRequest request) {
    return ResponseEntity.ok(masterDataService.updateStore(id, request));
  }

  @DeleteMapping("/stores/{id}")
  public ResponseEntity<Void> deleteStore(@PathVariable Integer id) {
    masterDataService.deleteStore(id);
    return ResponseEntity.noContent().build();
  }

  // Grades
  @GetMapping("/grades")
  public List<GradeResponse> listGrades() {
    return masterDataService.listGrades();
  }

  @PostMapping("/grades")
  public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody GradeRequest request) {
    return ResponseEntity.ok(masterDataService.createGrade(request));
  }

  @PutMapping("/grades/{id}")
  public ResponseEntity<GradeResponse> updateGrade(
      @PathVariable Integer id, @Valid @RequestBody GradeRequest request) {
    return ResponseEntity.ok(masterDataService.updateGrade(id, request));
  }

  @DeleteMapping("/grades/{id}")
  public ResponseEntity<Void> deleteGrade(@PathVariable Integer id) {
    masterDataService.deleteGrade(id);
    return ResponseEntity.noContent().build();
  }

  // Salary tiers
  @GetMapping("/salary-tiers")
  public List<SalaryTierResponse> listSalaryTiers() {
    return masterDataService.listSalaryTiers();
  }

  @PostMapping("/salary-tiers")
  public ResponseEntity<SalaryTierResponse> createSalaryTier(
      @Valid @RequestBody SalaryTierRequest request) {
    return ResponseEntity.ok(masterDataService.createSalaryTier(request));
  }

  @PutMapping("/salary-tiers/{id}")
  public ResponseEntity<SalaryTierResponse> updateSalaryTier(
      @PathVariable Integer id, @Valid @RequestBody SalaryTierRequest request) {
    return ResponseEntity.ok(masterDataService.updateSalaryTier(id, request));
  }

  @DeleteMapping("/salary-tiers/{id}")
  public ResponseEntity<Void> deleteSalaryTier(@PathVariable Integer id) {
    masterDataService.deleteSalaryTier(id);
    return ResponseEntity.noContent().build();
  }
}
