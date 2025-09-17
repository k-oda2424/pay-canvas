import { apiPost } from './client';
import { AuthState } from '../hooks/useAuth';

type LoginRequest = {
  email: string;
  password: string;
};

type LoginResponse = {
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  user: {
    userId: number;
    companyId: number;
    companyName: string;
    role: 'SUPER_ADMIN' | 'COMPANY_ADMIN' | 'STAFF';
    enabledFeatures: string[];
    name: string;
  };
};

export const loginApi = async (payload: LoginRequest): Promise<AuthState> => {
  const response = await apiPost<LoginResponse>('/api/auth/login', payload);
  return {
    isAuthenticated: true,
    token: response.accessToken,
    refreshToken: response.refreshToken,
    expiresAt: response.expiresAt,
    user: {
      id: response.user.userId,
      companyId: response.user.companyId,
      companyName: response.user.companyName,
      name: response.user.name,
      role: response.user.role,
      enabledFeatures: response.user.enabledFeatures
    }
  };
};

export const refreshTokenApi = async (refreshToken: string): Promise<AuthState> => {
  const response = await apiPost<LoginResponse>('/api/auth/refresh', { refreshToken });
  return {
    isAuthenticated: true,
    token: response.accessToken,
    refreshToken: response.refreshToken,
    expiresAt: response.expiresAt,
    user: {
      id: response.user.userId,
      companyId: response.user.companyId,
      companyName: response.user.companyName,
      name: response.user.name,
      role: response.user.role,
      enabledFeatures: response.user.enabledFeatures
    }
  };
};
