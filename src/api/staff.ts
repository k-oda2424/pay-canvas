import { apiGet, apiPost, apiPut, apiDelete } from './client';
import { EmployeeMaster } from '../types/models';

export const fetchEmployees = () => apiGet<EmployeeMaster[]>('/api/staff');

export type EmployeePayload = {
  name: string;
  employmentType: string;
  gradeId?: number | null;
  salaryTierId?: number | null;
  storeId?: number | null;
  guaranteedMinimumSalary?: number | null;
  managerAllowance?: number | null;
};

export const createEmployee = (payload: EmployeePayload) => apiPost<EmployeeMaster>('/api/staff', payload);

export const updateEmployee = (id: number, payload: EmployeePayload) =>
  apiPut<EmployeeMaster>(`/api/staff/${id}`, payload);

export const deleteEmployee = (id: number) => apiDelete<void>(`/api/staff/${id}`);
