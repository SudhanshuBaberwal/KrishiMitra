
"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";

import {
  loginUser,
  registerUser,
  getCurrentUser,
  logoutUser,
} from "@/lib/api/auth";

import { LoginInput, RegisterInput, User } from "@/types/auth";

type AuthContextType = {
  user: User | null;
  loading: boolean;
  login: (input: LoginInput) => Promise<void>;
  register: (input: RegisterInput) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = "krishimitra_access_token";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const login = async (input: LoginInput) => {
    const data = await loginUser(input);

    sessionStorage.setItem(TOKEN_KEY, data.accessToken);
    setUser(data.user);
  };

  const register = async (input: RegisterInput) => {
    const data = await registerUser(input);

    sessionStorage.setItem(TOKEN_KEY, data.accessToken);
    setUser(data.user);
  };

  const logout = useCallback(async () => {
    try {
      if (sessionStorage.getItem(TOKEN_KEY)) {
        await logoutUser();
      }
    } finally {
      sessionStorage.removeItem(TOKEN_KEY);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    let active = true;

    const initializeAuth = async () => {
      const token = sessionStorage.getItem(TOKEN_KEY);

      if (!token) {
        if (active) setLoading(false);
        return;
      }

      try {
        const currentUser = await getCurrentUser();
        if (active) setUser(currentUser);
      } catch {
        sessionStorage.removeItem(TOKEN_KEY);
        if (active) setUser(null);
      } finally {
        if (active) setLoading(false);
      }
    };

    initializeAuth();

    return () => {
      active = false;
    };
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, loading, login, register, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
