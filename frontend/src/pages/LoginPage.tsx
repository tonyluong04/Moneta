import AuthForm from '../components/AuthForm';
import { useAuth } from '@/contexts/AuthContext';

export default function LoginPage() {
  const { login, register } = useAuth();

  const handleSubmit = async (data: { name?: string; email: string; password: string; mode: string }) => {
    if (data.mode === 'register') {
      await register({ username: data.name!, email: data.email, password: data.password });
    } else {
      await login({ email: data.email, password: data.password });
    }
  };

  const handleDemoLogin = async () => {
    await login({ email: 'demo@moneta.app', password: 'demo1234' });
  };

  return <AuthForm onSubmit={handleSubmit} onDemoLogin={handleDemoLogin} />;
}
