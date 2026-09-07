import { useState } from 'react';
import { Eye, EyeOff, TrendingUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type AuthMode = 'login' | 'register';

interface AuthFormProps {
  onSubmit?: (data: { name?: string; email: string; password: string; mode: AuthMode }) => Promise<void>;
  onDemoLogin?: () => Promise<void>;
}

export default function AuthForm({ onSubmit, onDemoLogin }: AuthFormProps) {
  const [mode, setMode] = useState<AuthMode>('login');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await onSubmit?.({ name: mode === 'register' ? name : undefined, email, password, mode });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="min-h-screen flex"
      style={{ background: 'linear-gradient(135deg, #F0FDFA 0%, #CCFBF1 50%, #99F6E4 100%)' }}
    >
      {/* Left panel – branding */}
      <div
        className="hidden lg:flex flex-col justify-center items-center w-1/2 p-16"
        style={{ background: 'linear-gradient(160deg, #0D9488 0%, #0F766E 100%)' }}
      >
        <div className="max-w-sm text-center">
          <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center mx-auto mb-6">
            <TrendingUp className="w-9 h-9 text-white" />
          </div>
          <h1 className="text-4xl text-white mb-4" style={{ fontWeight: 300, letterSpacing: '-0.02em' }}>
            Moneta
          </h1>
          <p className="text-teal-100 text-lg leading-relaxed">
            Your complete financial picture in one place. Track assets, investments, and everyday spending.
          </p>
          <div className="mt-10 grid grid-cols-2 gap-4 text-left">
            {[
              { title: 'Net Worth Tracking', desc: 'Full portfolio view across all asset classes' },
              { title: 'Stock Portfolio', desc: 'Live prices with P&L tracking' },
              { title: 'Budget Management', desc: 'Income & expense analysis' },
              { title: 'FIRE Ready', desc: 'Exclude PPOR for accurate calculations' },
            ].map(({ title, desc }) => (
              <div key={title} className="bg-white/10 rounded-xl p-4">
                <p className="text-white text-sm font-semibold">{title}</p>
                <p className="text-teal-200 text-xs mt-1">{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right panel – form */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          {/* Logo (mobile) */}
          <div className="flex items-center gap-2 mb-10 lg:hidden">
            <div className="w-8 h-8 bg-teal-600 rounded-lg flex items-center justify-center">
              <TrendingUp className="w-5 h-5 text-white" />
            </div>
            <span className="text-xl font-semibold" style={{ color: '#0D9488' }}>Moneta</span>
          </div>

          <h2 className="text-3xl text-gray-900 mb-1 font-semibold">
            {mode === 'login' ? 'Welcome back' : 'Create account'}
          </h2>
          <p className="text-gray-500 text-sm mb-8">
            {mode === 'login'
              ? 'Sign in to your Moneta account'
              : 'Start tracking your financial journey'}
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            {mode === 'register' && (
              <div className="space-y-1.5">
                <Label htmlFor="name" className="text-sm text-gray-700">Full Name</Label>
                <Input
                  id="name"
                  type="text"
                  className="rounded-xl px-4 py-3 h-auto border-gray-200 bg-white text-sm"
                  placeholder="John Smith"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>
            )}
            <div className="space-y-1.5">
              <Label htmlFor="email" className="text-sm text-gray-700">Email</Label>
              <Input
                id="email"
                type="email"
                className="rounded-xl px-4 py-3 h-auto border-gray-200 bg-white text-sm"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="password" className="text-sm text-gray-700">
                Password
                {mode === 'register' && (
                  <span className="text-gray-400 font-normal"> — at least 8 characters</span>
                )}
              </Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  className="rounded-xl px-4 py-3 pr-11 h-auto border-gray-200 bg-white text-sm"
                  minLength={mode === 'register' ? 8 : undefined}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm">
                {error}
              </div>
            )}

            <Button
              type="submit"
              disabled={loading}
              className="w-full bg-teal-600 text-white py-3 h-auto rounded-xl hover:bg-teal-700 mt-2 font-semibold"
            >
              {loading
                ? 'Please wait...'
                : mode === 'login' ? 'Sign In' : 'Create Account'}
            </Button>
          </form>

          {mode === 'login' && (
            <Button
              variant="outline"
              onClick={async () => {
                setError('');
                setLoading(true);
                try {
                  await onDemoLogin?.();
                } catch (err) {
                  setError(err instanceof Error ? err.message : 'Something went wrong. Please try again.');
                } finally {
                  setLoading(false);
                }
              }}
              disabled={loading}
              className="w-full mt-3 py-3 h-auto rounded-xl border-teal-200 text-teal-700 hover:bg-teal-50 text-sm"
            >
              Try with Demo Account
            </Button>
          )}

          <p className="text-center text-sm text-gray-500 mt-6">
            {mode === 'login' ? "Don't have an account? " : 'Already have an account? '}
            <button
              onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}
              className="text-teal-600 hover:underline font-semibold cursor-pointer"
            >
              {mode === 'login' ? 'Sign up' : 'Sign in'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
