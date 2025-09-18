import { UserRole } from './user';

export type SummaryMetric = {
  id: string;
  label: string;
  value: string;
  change: string;
  positive: boolean;
};

export type PendingTask = {
  id: string;
  title: string;
  description: string;
  dueDate?: string;
};

export type Announcement = {
  id: string;
  message: string;
  date: string;
};

export type DailyAttendance = {
  id: string;
  date: string;
  staffName: string;
  storeName: string;
  checkIn: string;
  checkOut: string;
  workHours: number;
  tardyMinutes: number;
  status: '承認済' | '要確認';
};

export type StoreMetric = {
  id: string;
  date: string;
  storeName: string;
  sales: number;
  discount: number;
  totalHours: number;
};

export type PersonalMetric = {
  id: string;
  date: string;
  staffName: string;
  sales: number;
  productSales: number;
};

export type PayrollJob = {
  id: string;
  targetMonth: string;
  status: 'QUEUED' | 'RUNNING' | 'COMPLETED';
  progress: number;
  startedAt: string;
};

export type Payslip = {
  id: string;
  employeeName: string;
  role: string;
  baseSalary: number;
  allowances: number;
  deductions: number;
  netPay: number;
  status: '確定' | 'ステージング';
};

export type EmployeeMaster = {
  id: number;
  name: string;
  gradeId?: number | null;
  grade: string | null;
  employmentType: string;
  salaryTierId?: number | null;
  salaryPlan: string | null;
  storeId?: number | null;
  storeName: string | null;
  guaranteedMinimumSalary?: number | null;
  managerAllowance?: number | null;
};

export type FeatureToggle = {
  id: string;
  name: string;
  description: string;
  enabledTenants: number;
  isEnabled: boolean;
};

export type UserPersona = {
  label: string;
  email: string;
  role: UserRole;
};

export type StoreMaster = {
  id: number;
  name: string;
  storeType?: string;
  address?: string;
};

export type GradeMaster = {
  id: number;
  gradeName: string;
  commissionRate: number;
};

export type SalaryTierMaster = {
  id: number;
  planName: string;
  monthlyDaysOff: number;
  baseSalary: number;
};
