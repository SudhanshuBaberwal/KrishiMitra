
import AuthForm from "@/components/auth/auth-form";

export default function RegisterPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-[var(--background)] px-5 py-12">
      <AuthForm mode="register" />
    </main>
  );
}
