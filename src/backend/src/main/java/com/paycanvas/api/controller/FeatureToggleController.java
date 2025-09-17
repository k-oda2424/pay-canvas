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

@RestController
@RequestMapping("/api/features")
public class FeatureToggleController {
  private final FeatureToggleService featureToggleService;

  public FeatureToggleController(FeatureToggleService featureToggleService) {
    this.featureToggleService = featureToggleService;
  }

  @GetMapping
  public List<FeatureToggle> list() {
    return featureToggleService.listFeatureToggles();
  }

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
