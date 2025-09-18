import { PropsWithChildren } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { UserRole } from '../types/user';
import './AppLayout.css';

type NavItem = {
  to: string;
  label: string;
  children?: NavItem[];
};

type AppLayoutProps = {
  role: UserRole;
  userName: string;
  companyId: number;
  companyName: string;
  onLogout: () => void;
};

const navigationByRole: Record<UserRole, NavItem[]> = {
  SUPER_ADMIN: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/feature-toggles', label: '機能設定' },
    { to: '/super/users', label: '管理者登録' },
    { to: '/super/companies', label: '利用企業管理' }
  ],
  COMPANY_ADMIN: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/daily-metrics', label: '日次実績' },
    { to: '/payroll', label: '給与計算' },
    { to: '/payslips', label: '給与明細' },
    {
      to: '/staff/employees',
      label: 'マスタ管理',
      children: [
        { to: '/staff/employees', label: '従業員' },
        { to: '/staff/stores', label: '店舗' },
        { to: '/staff/grades', label: '等級' },
        { to: '/staff/salary', label: '給与プラン' }
      ]
    }
  ],
  STAFF: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/payslips', label: '給与明細' }
  ]
};

const isActive = (current: string, item: NavItem) => {
  if (current === item.to) {
    return true;
  }
  if (item.children) {
    return item.children.some((child) => current.startsWith(child.to));
  }
  return false;
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
            <div key={item.to} className={isActive(location.pathname, item) ? 'nav-item-wrap active' : 'nav-item-wrap'}>
              <NavLink to={item.to} className={({ isActive: childActive }) => (childActive ? 'nav-item active' : 'nav-item')}>
                {item.label}
              </NavLink>
              {item.children ? (
                <div className="nav-submenu">
                  {item.children.map((child) => (
                    <NavLink
                      key={child.to}
                      to={child.to}
                      className={({ isActive: childIsActive }) =>
                        childIsActive ? 'nav-subitem active' : 'nav-subitem'
                      }
                    >
                      {child.label}
                    </NavLink>
                  ))}
                </div>
              ) : null}
            </div>
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
