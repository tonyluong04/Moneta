const API_BASE = 'http://localhost:8080';

let accessToken: string | null = null;
let onTokenRefreshed: ((token: string) => void) | null = null;
let onAuthFailed: (() => void) | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function getAccessToken(): string | null {
  return accessToken;
}

// Called by AuthProvider to wire up callbacks
export function setAuthCallbacks(callbacks: {
  onRefreshed: (token: string) => void;
  onFailed: () => void;
}) {
  onTokenRefreshed = callbacks.onRefreshed;
  onAuthFailed = callbacks.onFailed;
}

async function refreshAccessToken(): Promise<string | null> {
  try {
    const res = await fetch(`${API_BASE}/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
    });

    if (!res.ok) return null;

    const data = await res.json();
    const newToken = data.accessToken as string;
    setAccessToken(newToken);
    onTokenRefreshed?.(newToken);
    return newToken;
  } catch {
    return null;
  }
}

export async function apiFetch<T = unknown>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const headers = new Headers(options.headers);

  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`);
  }

  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  // On 401, attempt one silent refresh then retry
  if (res.status === 401 && accessToken) {
    const newToken = await refreshAccessToken();

    if (newToken) {
      headers.set('Authorization', `Bearer ${newToken}`);
      const retry = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers,
        credentials: 'include',
      });

      if (!retry.ok) {
        const error = await retry.json().catch(() => ({ error: 'Request failed' }));
        throw new ApiError(retry.status, error.error ?? 'Request failed');
      }
      return retry.json() as Promise<T>;
    }

    // Refresh failed — session expired
    onAuthFailed?.();
    throw new ApiError(401, 'Session expired');
  }

  if (!res.ok) {
    const error = await res.json().catch(() => ({ error: 'Request failed' }));
    throw new ApiError(res.status, error.error ?? 'Request failed');
  }

  // Handle 204 No Content
  if (res.status === 204) return undefined as T;

  return res.json() as Promise<T>;
}

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
    this.name = 'ApiError';
  }
}
