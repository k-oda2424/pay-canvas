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
import { StaffManagementPage } from './pages/StaffManagementPage';
import { FeatureTogglePage } from './pages/FeatureTogglePage';
import { RoleGuard } from './components/RoleGuard';
import { SuperAdminUsersPage } from './pages/SuperAdminUsersPage';

const routesByRole = {
  SUPER_ADMIN: ['/dashboard', '/feature-toggles', '/super/users'],
  COMPANY_ADMIN: ['/dashboard', '/daily-metrics', '/payroll', '/payslips', '/staff'],
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
          <Route
            path="staff"
            element={
              <RoleGuard allow={['COMPANY_ADMIN']}>
                <StaffManagementPage />
              </RoleGuard>
            }
          />
          <Route
            path="feature-toggles"
            element={
              <RoleGuard allow={['SUPER_ADMIN']}>
                <FeatureTogglePage />
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
