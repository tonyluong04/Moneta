import { Link, NavLink, Outlet } from 'react-router-dom';
import { TrendingUp } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { paths } from '@/routes/paths';

/**
 * The signed-in app shell (task 4.4): a top bar with the logo and section nav,
 * the user and Sign out on the right, and the page container every page renders
 * into. Pages render only their content.
 *
 * A top bar rather than a sidebar, per PRD §6: a sidebar costs width the budget
 * table needs, and three sections don't warrant one.
 */
const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  cn(
    'px-3 py-2 rounded-lg text-sm font-medium transition-colors',
    isActive ? 'bg-teal-50 text-teal-700' : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100',
  );

export default function AppLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b border-gray-200">
        <div className="max-w-5xl mx-auto px-4 sm:px-8 h-16 flex items-center gap-6">
          <Link to={paths.portfolio} className="flex items-center gap-2 shrink-0">
            <span className="w-8 h-8 bg-teal-600 rounded-lg flex items-center justify-center">
              <TrendingUp className="w-5 h-5 text-white" />
            </span>
            <span className="hidden sm:inline text-lg font-semibold text-teal-700">Moneta</span>
          </Link>

          <nav className="flex items-center gap-1">
            <NavLink to={paths.portfolio} className={navLinkClass}>
              Portfolio
            </NavLink>
            {/* not `end`, so it stays active on /budget/dashboard too */}
            <NavLink to={paths.budget()} className={navLinkClass}>
              Budget
            </NavLink>
            {/* placeholder until Phase 5 brings live prices */}
            <span
              className="px-3 py-2 text-sm font-medium text-gray-300 cursor-not-allowed"
              title="Coming in Phase 5"
              aria-disabled="true"
            >
              Stocks
            </span>
          </nav>

          <div className="ml-auto flex items-center gap-3">
            <span className="hidden md:inline text-sm text-gray-500">{user?.username}</span>
            <Button variant="outline" size="sm" onClick={logout}>
              Sign out
            </Button>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 sm:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}
