import { apiGet } from './client';
import { Announcement, PendingTask, SummaryMetric } from '../types/models';

type DashboardResponse = {
  metrics: SummaryMetric[];
  tasks: PendingTask[];
  announcements: Announcement[];
};

export const fetchDashboardSummary = () => apiGet<DashboardResponse>('/api/dashboard/summary');
