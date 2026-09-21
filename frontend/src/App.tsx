import { RouterProvider, createBrowserRouter, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import PortfolioPage from './pages/PortfolioPage';
import ProtectedRoute from './components/routing/ProtectedRoute';
import GuestRoute from './components/routing/GuestRoute';
import BudgetPage from './pages/BudgetPage';

const router = createBrowserRouter([
  {
    path: '/login',
    element: (
      <GuestRoute>
        <LoginPage />
      </GuestRoute>
    ),
  },
  {
    path: '/portfolio',
    element: (
      <ProtectedRoute>
        <PortfolioPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/budget',
    element: (
      <ProtectedRoute>
        <BudgetPage />
      </ProtectedRoute>
    ),
  },
  {
    // the plan and the actuals used to be two pages; keep old links working
    path: '/budget/setup',
    element: <Navigate to="/budget" replace />,
  },
  {
    path: '*',
    element: <Navigate to="/portfolio" replace />,
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
