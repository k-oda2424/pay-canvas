package com.paycanvas.api.controller;

import com.paycanvas.api.model.Announcement;
import com.paycanvas.api.model.PendingTask;
import com.paycanvas.api.model.SummaryMetric;
import com.paycanvas.api.service.DashboardService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/summary")
  public Map<String, List<?>> summary() {
    return Map.of(
        "metrics", dashboardService.fetchSummaryMetrics(),
        "tasks", dashboardService.fetchPendingTasks(),
        "announcements", dashboardService.fetchAnnouncements());
  }
}
