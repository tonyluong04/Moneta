import { Loader2 } from 'lucide-react';

/**
 * Shown while we don't yet know who is signed in (the silent refresh is in
 * flight) and while a lazy route's code is downloading. Returning null instead
 * flashes a blank white page.
 */
export default function FullPageSpinner() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Loader2 className="w-6 h-6 text-teal-600 animate-spin" aria-label="Loading" />
    </div>
  );
}
