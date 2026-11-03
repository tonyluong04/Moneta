import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';

/** Unknown URLs land here instead of being silently redirected. */
export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-8">
      <div className="text-center">
        <p className="text-sm font-semibold text-teal-600">404</p>
        <h1 className="mt-2 text-2xl font-semibold text-gray-900">Page not found</h1>
        <p className="mt-2 text-sm text-gray-500">
          The page you were looking for doesn&apos;t exist or has moved.
        </p>
        <Button asChild className="mt-6 bg-teal-600 text-white rounded-xl hover:bg-teal-700">
          {/* "/" redirects to the app, or to sign-in if you're signed out */}
          <Link to="/">Go to Moneta</Link>
        </Button>
      </div>
    </div>
  );
}
