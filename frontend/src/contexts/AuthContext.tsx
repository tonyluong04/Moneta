import { createContext, useContext, useEffect, useState, useCallback, type ReactNode } from 'react';
import type { AuthUser, AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth';
import { apiFetch, setAccessToken, setAuthCallbacks } from '@/lib/api';

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);

  const clearAuth = useCallback(() => {
    setAccessToken(null);
    setUser(null);
  }, []);

  const applyAuth = useCallback((res: AuthResponse) => {
    setAccessToken(res.accessToken);
    setUser({ username: res.username, email: res.email });
  }, []);

  // Wire API client callbacks so it can notify us of refresh/failure
  useEffect(() => {
    setAuthCallbacks({
      onRefreshed: (token) => setAccessToken(token),
      onFailed: () => clearAuth(),
    });
  }, [clearAuth]);

  // On mount: attempt silent refresh to restore session from cookie
  useEffect(() => {
    let cancelled = false;

    async function silentRefresh() {
      try {
        const res = await apiFetch<AuthResponse>('/auth/refresh', {
          method: 'POST',
        });
        if (!cancelled) applyAuth(res);
      } catch {
        // No valid refresh cookie — user is not logged in
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    silentRefresh();
    return () => { cancelled = true; };
  }, [applyAuth]);

  const login = useCallback(async (data: LoginRequest) => {
    const res = await apiFetch<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    applyAuth(res);
  }, [applyAuth]);

  const register = useCallback(async (data: RegisterRequest) => {
    const res = await apiFetch<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    applyAuth(res);
  }, [applyAuth]);

  const logout = useCallback(() => {
    clearAuth();
  }, [clearAuth]);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: user !== null,
        loading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
