import { PropsWithChildren, useState } from 'react';
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
    { to: '/super/users', label: '管理者登録' },
    { to: '/super/companies', label: '利用企業管理' },
    { to: '/super/features', label: '企業機能管理' },
    {
      to: '/super/masters',
      label: 'マスタ管理',
      children: [
        { to: '/super/legal-masters', label: '法定マスタ管理' }
      ]
    }
  ],
  COMPANY_ADMIN: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/daily-metrics', label: '日次実績' },
    { to: '/payroll', label: '給与計算' },
    { to: '/payslips', label: '給与明細' },
    {
      to: '/staff/masters',
      label: 'マスタ管理',
      children: [
        { to: '/staff/employees', label: '従業員マスタ' },
        { to: '/staff/stores', label: '店舗マスタ' },
        { to: '/staff/grades', label: '等級マスタ' },
        { to: '/staff/work-patterns', label: '勤務パターン' },
        { to: '/staff/store-distances', label: '店舗間距離' },
        { to: '/staff/commute-methods', label: '通勤手段' },
        { to: '/staff/business-trip-allowances', label: '出張手当' },
        { to: '/staff/employee-store-distances', label: '従業員店舗距離' }
      ]
    }
  ],
  STAFF: [
    { to: '/dashboard', label: 'ダッシュボード' },
    { to: '/payslips', label: '給与明細' }
  ]
};

const isActive = (current: string, item: NavItem) => {
  if (item.children) {
    return item.children.some((child) => current.startsWith(child.to));
  }
  return current === item.to;
};

export const AppLayout = ({ role, userName, companyId, companyName, onLogout }: PropsWithChildren<AppLayoutProps>) => {
  const location = useLocation();
  const items = navigationByRole[role];
  const [expandedMenus, setExpandedMenus] = useState<Record<string, boolean>>(() => {
    // 初期状態で現在のパスに該当する親メニューを展開
    const initial: Record<string, boolean> = {};
    items.forEach((item) => {
      if (item.children && isActive(location.pathname, item)) {
        initial[item.to] = true;
      }
    });
    return initial;
  });

  const toggleMenu = (itemTo: string) => {
    setExpandedMenus((prev) => ({
      ...prev,
      [itemTo]: !prev[itemTo]
    }));
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="brand">payCanvas</div>
        <div className="profile-card">
          <span className="profile-name">{userName}</span>
          <span className="profile-role">{role}</span>
          <span className="profile-company">{companyName || (companyId ? `Company #${companyId}` : '所属なし')}</span>
        </div>
        <button className="logout-button" onClick={onLogout}>
          ログアウト
        </button>
        <nav>
          {items.map((item) => (
            <div key={item.to} className={isActive(location.pathname, item) ? 'nav-item-wrap active' : 'nav-item-wrap'}>
              {item.children ? (
                // 親メニュー（サブメニューあり）: クリックでトグルのみ
                <>
                  <button
                    className={isActive(location.pathname, item) ? 'nav-item active' : 'nav-item'}
                    onClick={() => toggleMenu(item.to)}
                  >
                    {item.label}
                  </button>
                  {expandedMenus[item.to] && (
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
                  )}
                </>
              ) : (
                // 通常メニュー（サブメニューなし）: NavLinkで遷移
                <NavLink to={item.to} className={({ isActive: childActive }) => (childActive ? 'nav-item active' : 'nav-item')}>
                  {item.label}
                </NavLink>
              )}
            </div>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="current-path">{location.pathname}</span>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
};
