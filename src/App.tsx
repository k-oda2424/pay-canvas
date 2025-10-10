import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './layouts/AppLayout';
import {
  AuthContext,
  AuthState,
  AUTH_EVENT,
  clearAuthState,
  defaultAuthState,
  persistAuthState
} from './hooks/useAuth';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { DailyMetricsPage } from './pages/DailyMetricsPage';
import { PayrollExecutionPage } from './pages/PayrollExecutionPage';
import { PayslipListPage } from './pages/PayslipListPage';
import {
  EmployeeMasterPage,
  GradeMasterPage,
  StoreMasterPage,
  WorkPatternMasterPage
} from './pages/StaffManagementPage';
import { StoreDistancePage } from './pages/StoreDistancePage';
import { CommuteMethodPage } from './pages/CommuteMethodPage';
import { BusinessTripAllowancePage } from './pages/BusinessTripAllowancePage';
import { EmployeeStoreDistancePage } from './pages/EmployeeStoreDistancePage';
import { RoleGuard } from './components/RoleGuard';
import { SuperAdminUsersPage } from './pages/SuperAdminUsersPage';
import { SuperAdminCompaniesPage } from './pages/SuperAdminCompaniesPage';
import { CompanyFeatureManagementPage } from './pages/CompanyFeatureManagementPage';
import { LegalMastersPage } from './pages/LegalMastersPage';

const routesByRole = {
  SUPER_ADMIN: ['/dashboard', '/super/users', '/super/companies'],
  COMPANY_ADMIN: ['/dashboard', '/daily-metrics', '/payroll', '/payslips', '/staff/employees'],
  STAFF: ['/dashboard', '/payslips']
} as const;

const App = () => {
  const [auth, setAuth] = useState<AuthState>(() => loadPersistedAuth());

  useEffect(() => {
    const handler = (event: Event) => {
      const custom = event as CustomEvent<AuthState>;
      setAuth(custom.detail);
    };
    window.addEventListener(AUTH_EVENT, handler as EventListener);
    return () => window.removeEventListener(AUTH_EVENT, handler as EventListener);
  }, []);

  const handleLogin = (state: AuthState) => {
    persistAuthState(state);
    setAuth(state);
  };

  const handleLogout = () => {
    clearAuthState();
    setAuth(defaultAuthState);
  };

  const isAuthenticated = auth.isAuthenticated;
  const role = auth.user?.role ?? 'STAFF';

  return (
    <AuthContext.Provider value={{ auth, onLogin: handleLogin, onLogout: handleLogout }}>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/"
          element={
            isAuthenticated ? (
              <AppLayout
                role={role}
                onLogout={handleLogout}
                userName={auth.user?.name ?? ''}
                companyId={auth.user?.companyId ?? 0}
                companyName={auth.user?.companyName ?? ''}
              />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route
            path="dashboard"
            element={
              <RoleGuard allow={['SUPER_ADMIN', 'COMPANY_ADMIN', 'STAFF']}>
                <DashboardPage />
              </RoleGuard>
            }
          />
          <Route
            path="daily-metrics"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <DailyMetricsPage />
              </RoleGuard>
            }
          />
          <Route
            path="payroll"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <PayrollExecutionPage />
              </RoleGuard>
            }
          />
          <Route
            path="payslips"
            element={
              <RoleGuard allow={['COMPANY_ADMIN', 'STAFF']}>
                <PayslipListPage />
              </RoleGuard>
            }
          />
          <Route path="staff" element={<Navigate to="/staff/employees" replace />} />
          <Route
            path="staff/employees"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <EmployeeMasterPage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/stores"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <StoreMasterPage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/grades"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <GradeMasterPage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/work-patterns"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <WorkPatternMasterPage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/store-distances"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <StoreDistancePage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/commute-methods"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <CommuteMethodPage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/business-trip-allowances"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <BusinessTripAllowancePage />
              </RoleGuard>
            }
          />
          <Route
            path="staff/employee-store-distances"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <EmployeeStoreDistancePage />
              </RoleGuard>
            }
          />
          <Route
            path="super/users"
            element={
              <RoleGuard allow={['SUPER_ADMIN']}>
                <SuperAdminUsersPage />
              </RoleGuard>
            }
          />
          <Route
            path="super/companies"
            element={
              <RoleGuard allow={['SUPER_ADMIN']}>
                <SuperAdminCompaniesPage />
              </RoleGuard>
            }
          />
          <Route
            path="super/features"
            element={
              <RoleGuard allow={['SUPER_ADMIN']}>
                <CompanyFeatureManagementPage />
              </RoleGuard>
            }
          />
          <Route
            path="super/legal-masters"
            element={
              <RoleGuard allow={['SUPER_ADMIN']}>
                <LegalMastersPage />
              </RoleGuard>
            }
          />
        </Route>
        <Route path="*" element={<Navigate to={isAuthenticated ? routesByRole[role][0] : '/login'} replace />} />
      </Routes>
    </AuthContext.Provider>
  );
};

export default App;

function loadPersistedAuth(): AuthState {
  const token = sessionStorage.getItem('paycanvas_token') ?? undefined;
  const refreshToken = sessionStorage.getItem('paycanvas_refresh') ?? undefined;
  const expiresAt = sessionStorage.getItem('paycanvas_token_expires') ?? undefined;
  const userJson = sessionStorage.getItem('paycanvas_user');
  if (token && refreshToken && userJson) {
    try {
      const parsed = JSON.parse(userJson);
      const user = {
        ...parsed,
        companyName: parsed.companyName ?? ''
      };
      return {
        isAuthenticated: true,
        token,
        refreshToken,
        expiresAt,
        user
      };
    } catch (error) {
      sessionStorage.removeItem('paycanvas_user');
    }
  }
  return defaultAuthState;
}
