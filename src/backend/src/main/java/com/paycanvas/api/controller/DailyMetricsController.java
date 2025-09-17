package com.paycanvas.api.controller;

import com.paycanvas.api.model.DailyAttendance;
import com.paycanvas.api.model.PersonalMetric;
import com.paycanvas.api.model.StoreMetric;
import com.paycanvas.api.service.DailyMetricsService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily")
public class DailyMetricsController {
  private final DailyMetricsService dailyMetricsService;

  public DailyMetricsController(DailyMetricsService dailyMetricsService) {
    this.dailyMetricsService = dailyMetricsService;
  }

  @GetMapping
  public Map<String, List<?>> list() {
    return Map.of(
        "attendances", dailyMetricsService.fetchAttendances(),
        "storeMetrics", dailyMetricsService.fetchStoreMetrics(),
        "personalMetrics", dailyMetricsService.fetchPersonalMetrics());
  }
}
