import { apiFetch } from '@/lib/api';
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth';

/**
 * The /auth endpoints, wrapped the same way budgetApi.ts wraps /budget.
 *
 * AuthContext used to call apiFetch directly, which made auth the one feature
 * reaching past the API-module layer. Note these are not under /api/v1 — the
 * refresh cookie is scoped to /auth.
 */

/** POST /auth/login — exchanges credentials for an access token. */
export function login(body: LoginRequest): Promise<AuthResponse> {
  return apiFetch<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

/** POST /auth/register — creates the account and signs it in. */
export function register(body: RegisterRequest): Promise<AuthResponse> {
  return apiFetch<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(body),
  });
}

/**
 * POST /auth/refresh — restores a session from the HttpOnly cookie.
 * Throws when there is no valid cookie, which is the normal "not signed in" case.
 */
export function refresh(): Promise<AuthResponse> {
  return apiFetch<AuthResponse>('/auth/refresh', { method: 'POST' });
}

/**
 * POST /auth/logout — revokes the refresh token and clears the cookie (204).
 * Without it, signing out left the cookie behind and a reload signed you back in.
 */
export function logout(): Promise<void> {
  return apiFetch<void>('/auth/logout', { method: 'POST' });
}
