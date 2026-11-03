import { Navigate, Outlet, useLocation, type Location } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import FullPageSpinner from '@/components/FullPageSpinner';
import { paths } from './paths';

/**
 * The opposite of RequireAuth: wraps the sign-in screens and sends anyone
 * already signed in into the app.
 *
 * This is also what moves you on after a successful sign-in: login() sets the
 * user, this re-renders as authenticated, and redirects. So it goes back to the
 * page RequireAuth sent you away from, or to the portfolio if there wasn't one.
 * The login and register pages never navigate themselves.
 */
export interface ReturnToState {
  from?: Location;
}

export default function GuestOnly() {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();
  const from = (location.state as ReturnToState | null)?.from;

  if (loading) return <FullPageSpinner />;

  if (isAuthenticated) return <Navigate to={from ?? paths.portfolio} replace />;

  return <Outlet />;
}
