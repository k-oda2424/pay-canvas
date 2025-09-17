import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from './client';
import { StoreMaster, GradeMaster, SalaryTierMaster } from '../types/models';

export type StorePayload = {
  name: string;
  storeType?: string;
  address?: string;
};

export type GradePayload = {
  gradeName: string;
  commissionRatePercent: number; // 0-100
};

export type SalaryTierPayload = {
  planName: string;
  monthlyDaysOff: number;
  baseSalary: number;
};

export const fetchStores = () => apiGet<StoreMaster[]>('/api/masters/stores');
export const createStore = (payload: StorePayload) => apiPost<StoreMaster>('/api/masters/stores', payload);
export const updateStore = (id: number, payload: StorePayload) => apiPut<StoreMaster>(`/api/masters/stores/${id}`, payload);
export const deleteStore = (id: number) => apiDelete<void>(`/api/masters/stores/${id}`);

export const fetchGrades = () => apiGet<GradeMaster[]>('/api/masters/grades');
export const createGrade = (payload: GradePayload) => apiPost<GradeMaster>('/api/masters/grades', payload);
export const updateGrade = (id: number, payload: GradePayload) => apiPut<GradeMaster>(`/api/masters/grades/${id}`, payload);
export const deleteGrade = (id: number) => apiDelete<void>(`/api/masters/grades/${id}`);

export const fetchSalaryTiers = () => apiGet<SalaryTierMaster[]>('/api/masters/salary-tiers');
export const createSalaryTier = (payload: SalaryTierPayload) =>
  apiPost<SalaryTierMaster>('/api/masters/salary-tiers', payload);
export const updateSalaryTier = (id: number, payload: SalaryTierPayload) =>
  apiPut<SalaryTierMaster>(`/api/masters/salary-tiers/${id}`, payload);
export const deleteSalaryTier = (id: number) => apiDelete<void>(`/api/masters/salary-tiers/${id}`);
