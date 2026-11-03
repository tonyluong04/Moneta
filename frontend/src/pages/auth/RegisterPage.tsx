import AuthForm, { type AuthFormValues } from '@/components/AuthForm';
import { useAuth } from '@/contexts/AuthContext';

/** /register — its own URL, so a "Get started" button can link straight to it. */
export default function RegisterPage() {
  const { register } = useAuth();

  const handleSubmit = async ({ name, email, password }: AuthFormValues) => {
    // the form marks the name field required in register mode
    await register({ username: name ?? '', email, password });
  };

  return <AuthForm mode="register" onSubmit={handleSubmit} />;
}
