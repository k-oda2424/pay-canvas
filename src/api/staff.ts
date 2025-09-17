import { apiGet } from './client';
import { EmployeeMaster } from '../types/models';

export const fetchEmployees = () => apiGet<EmployeeMaster[]>('/api/staff');
