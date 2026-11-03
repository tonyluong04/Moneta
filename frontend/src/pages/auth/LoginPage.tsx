import AuthForm, { type AuthFormValues } from '@/components/AuthForm';
import { useAuth } from '@/contexts/AuthContext';

/** /login. Where you go after signing in is GuestOnly's job, not this page's. */
export default function LoginPage() {
  const { login } = useAuth();

  const handleSubmit = async ({ email, password }: AuthFormValues) => {
    await login({ email, password });
  };

  const handleDemoLogin = async () => {
    await login({ email: 'demo@moneta.app', password: 'demo1234' });
  };

  return <AuthForm mode="login" onSubmit={handleSubmit} onDemoLogin={handleDemoLogin} />;
}
