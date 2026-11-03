import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import FullPageSpinner from '@/components/FullPageSpinner';
import { paths } from './paths';

/**
 * The one auth guard for the whole app. It is a pathless layout route: every
 * route nested under it in router.tsx is protected, so no page wraps itself.
 *
 * While `loading` is true the silent refresh hasn't answered yet, so we don't
 * know whether the user is signed in. Showing a spinner rather than guessing
 * stops a reload from flashing the login page.
 */
export default function RequireAuth() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return <FullPageSpinner />;

  if (!isAuthenticated) return <Navigate to={paths.login} replace />;

  return <Outlet />;
}
