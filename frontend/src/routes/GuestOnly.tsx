import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import FullPageSpinner from '@/components/FullPageSpinner';
import { paths } from './paths';

/**
 * The opposite of RequireAuth: wraps the sign-in screens and sends anyone
 * already signed in into the app.
 */
export default function GuestOnly() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) return <FullPageSpinner />;

  if (isAuthenticated) return <Navigate to={paths.portfolio} replace />;

  return <Outlet />;
}
