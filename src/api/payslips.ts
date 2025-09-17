import { apiGet } from './client';
import { Payslip } from '../types/models';

export const fetchPayslips = (targetMonth: string) =>
  apiGet<Payslip[]>(`/api/payslips?targetMonth=${encodeURIComponent(targetMonth)}`);
