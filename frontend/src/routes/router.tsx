import type { ComponentType } from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import FullPageSpinner from '@/components/FullPageSpinner';
import AppLayout from '@/layouts/AppLayout';
import NotFoundPage from '@/pages/NotFoundPage';
import GuestOnly from './GuestOnly';
import RequireAuth from './RequireAuth';
import { paths } from './paths';

/**
 * Pages load lazily, so the sign-in screen doesn't download Recharts.
 *
 * React Router's `lazy` expects the module to export `Component`, not a default
 * export. This adapter lets every page keep `export default`.
 */
function page(load: () => Promise<{ default: ComponentType }>) {
  return async () => ({ Component: (await load()).default });
}

export const router = createBrowserRouter([
  {
    // A pathless root, only so there is somewhere to put the fallback shown
    // while the first lazy page's code downloads.
    HydrateFallback: FullPageSpinner,
    children: [
      // No landing page yet. RequireAuth sends signed-out visitors on to /login.
      { index: true, element: <Navigate to={paths.portfolio} replace /> },

      {
        element: <GuestOnly />,
        children: [
          { path: 'login', lazy: page(() => import('@/pages/auth/LoginPage')) },
          { path: 'register', lazy: page(() => import('@/pages/auth/RegisterPage')) },
        ],
      },

      {
        // one guard for the whole app, then the shell with the top bar
        element: <RequireAuth />,
        children: [
          {
            element: <AppLayout />,
            children: [
              { path: 'portfolio', lazy: page(() => import('@/pages/portfolio/PortfolioPage')) },
              { path: 'budget', lazy: page(() => import('@/pages/budget/BudgetPage')) },
              {
                path: 'budget/dashboard',
                lazy: page(() => import('@/pages/budget/BudgetDashboardPage')),
              },
            ],
          },
        ],
      },

      // the plan and the actuals used to be two pages; keep old links working
      { path: 'budget/setup', element: <Navigate to={paths.budget()} replace /> },

      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
