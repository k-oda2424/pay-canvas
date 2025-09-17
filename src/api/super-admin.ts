import { apiGet, apiPost } from './client';

export type CompanySummary = {
  id: number;
  name: string;
  status: string;
};

export type AdminUserPayload = {
  companyId: number;
  email: string;
  displayName: string;
  password: string;
};

export type AdminUserResponse = {
  id: number;
  email: string;
  displayName: string;
  companyId: number;
  companyName: string;
};

export const fetchCompanies = () => apiGet<CompanySummary[]>('http://localhost:8080/api/super/companies');

export const createCompanyAdmin = (payload: AdminUserPayload) =>
  apiPost<AdminUserResponse>('http://localhost:8080/api/super/users', payload);
