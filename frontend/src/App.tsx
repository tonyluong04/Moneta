import { RouterProvider, createBrowserRouter, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import PortfolioPage from './pages/PortfolioPage';
import ProtectedRoute from './components/routing/ProtectedRoute';
import GuestRoute from './components/routing/GuestRoute';
import BudgetSetupPage from './pages/BudgetSetupPage';

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
    path: '/budget/setup',
    element: (
      <ProtectedRoute>
        <BudgetSetupPage/>
      </ProtectedRoute>
    ),
  },
  {
    path: '*',
    element: <Navigate to="/portfolio" replace />,
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}
