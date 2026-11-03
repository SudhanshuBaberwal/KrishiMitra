
"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff, Leaf, Loader2, ArrowRight } from "lucide-react";
import { useAuth } from "@/components/providers/auth-provider";

const loginSchema = z.object({
  email: z.string().email("Enter a valid email address"),
  password: z.string().min(1, "Password is required"),
});

const registerSchema = z.object({
  name: z.string().min(2, "Name must contain at least 2 characters"),
  email: z.string().email("Enter a valid email address"),
  password: z.string().min(8, "Password must contain at least 8 characters"),
});

type FormValues = {
  name?: string;
  email: string;
  password: string;
};

type AuthFormProps = {
  mode: "login" | "register";
};

export default function AuthForm({ mode }: AuthFormProps) {
  const isRegister = mode === "register";
  const { login, register: registerUser } = useAuth();
  const router = useRouter();

  const [showPassword, setShowPassword] = useState(false);
  const [serverError, setServerError] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver: zodResolver(isRegister ? registerSchema : loginSchema),
  });

  const onSubmit = async (values: FormValues) => {
    setServerError("");

    try {
      if (isRegister) {
        await registerUser({
          name: values.name!,
          email: values.email,
          password: values.password,
        });
      } else {
        await login({
          email: values.email,
          password: values.password,
        });
      }

      router.replace("/");
    } catch (error: unknown) {
      const message =
        typeof error === "object" &&
        error !== null &&
        "response" in error
          ? (error as {
              response?: { data?: { message?: string } };
            }).response?.data?.message
          : undefined;

      setServerError(message || "Something went wrong. Please try again.");
    }
  };

  const inputClass =
    "mt-2 h-12 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] px-4 text-sm outline-none transition placeholder:text-[var(--muted)] focus:border-[var(--primary)] focus:ring-2 focus:ring-[var(--primary)]/10";

  return (
    <div className="w-full max-w-md">
      <Link href="/" className="mb-9 inline-flex items-center gap-3">
        <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-[var(--primary)] text-white">
          <Leaf size={23} />
        </span>
        <span className="text-xl font-bold">
          Krishi<span className="text-[var(--primary)]">Mitra</span>
        </span>
      </Link>

      <p className="text-xs font-bold uppercase tracking-[2px] text-[var(--primary)]">
        {isRegister ? "Join our farming community" : "Welcome back"}
      </p>

      <h1 className="mt-3 text-3xl font-bold tracking-tight">
        {isRegister ? "Create your account" : "Sign in to KrishiMitra"}
      </h1>

      <p className="mt-2 text-sm leading-6 muted-text">
        {isRegister
          ? "Create an account to access smarter farming insights."
          : "Access your farm dashboard and personalized agricultural insights."}
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-8 space-y-5">
        {isRegister && (
          <div>
            <label className="text-xs font-semibold">Full name</label>
            <input
              {...register("name")}
              placeholder="Enter your full name"
              className={inputClass}
            />
            {errors.name && (
              <p className="mt-1 text-xs text-red-500">
                {errors.name.message}
              </p>
            )}
          </div>
        )}

        <div>
          <label className="text-xs font-semibold">Email address</label>
          <input
            {...register("email")}
            type="email"
            autoComplete="email"
            placeholder="you@example.com"
            className={inputClass}
          />
          {errors.email && (
            <p className="mt-1 text-xs text-red-500">
              {errors.email.message}
            </p>
          )}
        </div>

        <div>
          <label className="text-xs font-semibold">Password</label>
          <div className="relative">
            <input
              {...register("password")}
              type={showPassword ? "text" : "password"}
              autoComplete={isRegister ? "new-password" : "current-password"}
              placeholder="Enter your password"
              className={`${inputClass} pr-12`}
            />
            <button
              type="button"
              aria-label={showPassword ? "Hide password" : "Show password"}
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 muted-text"
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {errors.password && (
            <p className="mt-1 text-xs text-red-500">
              {errors.password.message}
            </p>
          )}
        </div>

        {serverError && (
          <p role="alert" className="rounded-xl bg-red-50 p-3 text-xs text-red-600">
            {serverError}
          </p>
        )}

        <button
          disabled={isSubmitting}
          type="submit"
          className="flex h-12 w-full items-center justify-center gap-2 rounded-xl bg-[var(--primary)] text-sm font-bold text-white transition hover:bg-[var(--primary-dark)] disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting ? (
            <>
              <Loader2 size={17} className="animate-spin" />
              Please wait...
            </>
          ) : (
            <>
              {isRegister ? "Create account" : "Sign in"}
              <ArrowRight size={17} />
            </>
          )}
        </button>
      </form>

      <p className="mt-7 text-center text-sm muted-text">
        {isRegister ? "Already have an account?" : "New to KrishiMitra?"}{" "}
        <Link
          href={isRegister ? "/login" : "/register"}
          className="font-bold text-[var(--primary)] hover:underline"
        >
          {isRegister ? "Sign in" : "Create account"}
        </Link>
      </p>

      <p className="mt-8 text-center text-[10px] muted-text">
        By continuing, you agree to use KrishiMitra responsibly.
      </p>
    </div>
  );
}
