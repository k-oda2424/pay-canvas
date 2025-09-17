import { apiGet } from './client';
import { DailyAttendance, StoreMetric, PersonalMetric } from '../types/models';

type DailyResponse = {
  attendances: DailyAttendance[];
  storeMetrics: StoreMetric[];
  personalMetrics: PersonalMetric[];
};

export const fetchDailyData = () => apiGet<DailyResponse>('/api/daily');
