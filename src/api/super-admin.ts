import { apiGet, apiPost, apiPut } from './client';

export type CompanySummary = {
  id: number;
  name: string;
  status: string;
  postalCode?: string;
  address?: string;
  phone?: string;
  contactName?: string;
  contactKana?: string;
  contactEmail?: string;
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

export type CompanyPayload = {
  name: string;
  postalCode: string;
  address: string;
  phone: string;
  contactName: string;
  contactKana: string;
  contactEmail: string;
};

export type CompanyCreateResponse = CompanySummary;

export const fetchCompanies = () => apiGet<CompanySummary[]>('/api/super/companies');

export const createCompanyAdmin = (payload: AdminUserPayload) =>
  apiPost<AdminUserResponse>('/api/super/users', payload);

export const createCompany = (payload: CompanyPayload) =>
  apiPost<CompanyCreateResponse>('/api/super/companies', payload);

export const updateCompany = (id: number, payload: CompanyPayload) =>
  apiPut<CompanySummary>(`/api/super/companies/${id}`, payload);
