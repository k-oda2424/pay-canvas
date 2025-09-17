import { PropsWithChildren } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { UserRole } from '../types/user';
import './AppLayout.css';

type AppLayoutProps = {
  role: UserRole;
  userName: string;
  companyId: number;
  companyName: string;
  onLogout: () => void;
};

const navigationByRole: Record<UserRole, { to: string; label: string }[]> = {
  SUPER_ADMIN: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/feature-toggles', label: '機能設定' },
    { to: '/super/users', label: '管理者登録' }
  ],
  COMPANY_ADMIN: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/daily-metrics', label: '日次実績' },
    { to: '/payroll', label: '給与計算' },
    { to: '/payslips', label: '給与明細' },
    { to: '/staff', label: 'マスタ管理' }
  ],
  STAFF: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/payslips', label: '給与明細' }
  ]
};

export const AppLayout = ({ role, userName, companyId, companyName, onLogout }: PropsWithChildren<AppLayoutProps>) => {
  const location = useLocation();
  const items = navigationByRole[role];

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="brand">payCanvas</div>
        <div className="profile-card">
          <span className="profile-name">{userName}</span>
          <span className="profile-role">{role}</span>
          <span className="profile-company">{companyName || (companyId ? `Company #${companyId}` : '所属なし')}</span>
        </div>
        <nav>
          {items.map((item) => (
            <NavLink key={item.to} to={item.to} className={({ isActive }) => (isActive ? 'nav-item active' : 'nav-item')}>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="current-path">{location.pathname}</span>
          <button className="logout-button" onClick={onLogout}>
            ログアウト
          </button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
};
