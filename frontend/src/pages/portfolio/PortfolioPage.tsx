import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Link } from 'react-router-dom';
import { paths } from '@/routes/paths';

export default function PortfolioPage() {
  const { user } = useAuth();

  return (
    <>
      <div className="mb-8">
        <h1 className="text-2xl font-semibold text-gray-900">Portfolio</h1>
        <p className="text-sm text-gray-500">Welcome, {user?.username}</p>
      </div>
      <div className="bg-white rounded-xl border border-gray-200 p-12 text-center">
        <p className="text-gray-400 mb-4">Portfolio dashboard coming soon — Phase 4</p>
        <Button asChild className="bg-teal-600 text-white rounded-xl hover:bg-teal-700">
          <Link to={paths.budget()}>Set up monthly budget</Link>
        </Button>
      </div>
    </>
  );
}
