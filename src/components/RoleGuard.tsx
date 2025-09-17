import { PropsWithChildren } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { UserRole } from '../types/user';

type RoleGuardProps = {
  allow: UserRole[];
};

export const RoleGuard = ({ allow, children }: PropsWithChildren<RoleGuardProps>) => {
  const { auth } = useAuth();
  const location = useLocation();
  const role = auth.user?.role;

  if (!role) {
    return <Navigate to="/login" replace />;
  }

  if (!allow.includes(role)) {
    return <Navigate to="/dashboard" replace state={{ from: location.pathname }} />;
  }

  return <>{children}</>;
};
