import { createContext, useContext } from 'react';
import { UserRole } from '../types/user';

type UserInfo = {
  id: number;
  name: string;
  companyId: number;
  companyName: string;
  role: UserRole;
  enabledFeatures: string[];
};

export type AuthState = {
  isAuthenticated: boolean;
  token?: string;
  refreshToken?: string;
  expiresAt?: string;
  user?: UserInfo;
};

export const defaultAuthState: AuthState = {
  isAuthenticated: false
};

type AuthContextValue = {
  auth: AuthState;
  onLogin: (state: AuthState) => void;
  onLogout: () => void;
};

export const AuthContext = createContext<AuthContextValue>({
  auth: defaultAuthState,
  onLogin: () => undefined,
  onLogout: () => undefined
});

export const useAuth = () => useContext(AuthContext);

export const AUTH_EVENT = 'paycanvas:auth';

export const persistAuthState = (state: AuthState) => {
  if (state.token) {
    sessionStorage.setItem('paycanvas_token', state.token);
  } else {
    sessionStorage.removeItem('paycanvas_token');
  }
  if (state.refreshToken) {
    sessionStorage.setItem('paycanvas_refresh', state.refreshToken);
  } else {
    sessionStorage.removeItem('paycanvas_refresh');
  }
  if (state.expiresAt) {
    sessionStorage.setItem('paycanvas_token_expires', state.expiresAt);
  } else {
    sessionStorage.removeItem('paycanvas_token_expires');
  }
  if (state.user) {
    sessionStorage.setItem('paycanvas_user', JSON.stringify(state.user));
  } else {
    sessionStorage.removeItem('paycanvas_user');
  }
  window.dispatchEvent(new CustomEvent<AuthState>(AUTH_EVENT, { detail: state }));
};

export const clearAuthState = () => {
  sessionStorage.removeItem('paycanvas_token');
  sessionStorage.removeItem('paycanvas_refresh');
  sessionStorage.removeItem('paycanvas_token_expires');
  sessionStorage.removeItem('paycanvas_user');
  window.dispatchEvent(new CustomEvent<AuthState>(AUTH_EVENT, { detail: defaultAuthState }));
};
