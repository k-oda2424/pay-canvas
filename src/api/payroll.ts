import { apiGet, apiPost } from './client';
import { PayrollJob } from '../types/models';

export const listPayrollJobs = () => apiGet<PayrollJob[]>('/api/payroll/jobs');

export const executePayroll = (targetMonth: string) =>
  apiPost<PayrollJob>('/api/payroll/execute', { targetMonth });
