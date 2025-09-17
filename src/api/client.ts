import type { AuthState } from '../hooks/useAuth';
import { clearAuthState, persistAuthState } from '../hooks/useAuth';

const API_BASE_URL = 'http://localhost:8080';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

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

export const apiGet = <T>(path: string) => request<T>('GET', path);

export const apiPost = <T>(path: string, body: unknown) => request<T>('POST', path, body);

export const apiPut = <T>(path: string, body: unknown) => request<T>('PUT', path, body);

export const apiPatch = <T>(path: string, body: unknown) => request<T>('PATCH', path, body);

export const apiDelete = <T>(path: string) => request<T>('DELETE', path);

const request = async <T>(method: HttpMethod, path: string, body?: unknown, retry = true): Promise<T> => {
  const token = sessionStorage.getItem('paycanvas_token');
  const headers: Record<string, string> = {};
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  const url = path.startsWith('http') ? path : `${API_BASE_URL}${path}`;
  const response = await fetch(url, {
    method,
    credentials: 'include',
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined
  });

  if (response.ok) {
    if (method === 'DELETE' || response.status === 204) {
      return undefined as T;
    }
    const text = await response.text();
    return text ? (JSON.parse(text) as T) : (undefined as T);
  }

  if (response.status === 401 && retry && !path.startsWith('/api/auth/')) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      return request<T>(method, path, body, false);
    }
  }

  if (response.status === 401) {
    clearAuthState();
    if (!path.startsWith('/api/auth/')) {
      window.location.href = '/login';
    }
  }

  const message = await response.text();
  throw new Error(`API request failed: ${response.status} ${message}`);
};

const refreshAccessToken = async (): Promise<boolean> => {
  const refreshToken = sessionStorage.getItem('paycanvas_refresh');
  if (!refreshToken) {
    return false;
  }
  const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ refreshToken })
  });
  if (!response.ok) {
    return false;
  }
  const data = (await response.json()) as LoginResponse;
  const authState = mapLoginResponse(data);
  persistAuthState(authState);
  return true;
};

const mapLoginResponse = (response: LoginResponse): AuthState => ({
  isAuthenticated: true,
  token: response.accessToken,
  refreshToken: response.refreshToken,
  expiresAt: response.expiresAt,
  user: {
    id: response.user.userId,
    companyId: response.user.companyId,
    companyName: response.user.companyName,
    role: response.user.role,
    enabledFeatures: response.user.enabledFeatures,
    name: response.user.name
  }
});
