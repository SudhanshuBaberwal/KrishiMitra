
"use client";

import { useState } from "react";
import { useTheme } from "next-themes";
import { useRouter } from "next/navigation";
import {
  Menu,
  Search,
  Bell,
  Sun,
  Moon,
  ChevronDown,
  Languages,
  LogOut,
} from "lucide-react";
import { useAuth } from "@/components/providers/auth-provider";

type TopbarProps = {
  onMenuClick: () => void;
};

export default function Topbar({ onMenuClick }: TopbarProps) {
  const { theme, setTheme } = useTheme();
  const { user, logout } = useAuth();
  const router = useRouter();

  const [language, setLanguage] = useState("EN");
  const [languageOpen, setLanguageOpen] = useState(false);
  const [loggingOut, setLoggingOut] = useState(false);

  const handleLogout = async () => {
    setLoggingOut(true);
    try {
      await logout();
    } finally {
      router.replace("/login");
    }
  };

  return (
    <header className="sticky top-0 z-30 flex h-[76px] items-center justify-between border-b border-[var(--border)] bg-[var(--surface)]/95 px-4 backdrop-blur-xl sm:px-6 lg:px-9">
      <div className="flex items-center gap-3">
        <button
          onClick={onMenuClick}
          aria-label="Open navigation"
          className="rounded-xl p-2 hover:bg-[var(--surface-muted)] lg:hidden"
        >
          <Menu size={21} />
        </button>

        <div className="relative hidden w-64 md:block xl:w-80">
          <Search
            size={17}
            className="absolute left-3 top-1/2 -translate-y-1/2 muted-text"
          />
          <input
            placeholder="Search anything..."
            className="h-10 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] pl-10 pr-4 text-xs outline-none focus:border-[var(--primary)]"
          />
        </div>
      </div>

      <div className="flex items-center gap-2 sm:gap-3">
        <div className="relative">
          <button
            onClick={() => setLanguageOpen(!languageOpen)}
            className="flex h-10 items-center gap-2 rounded-xl border border-[var(--border)] px-3 text-xs font-semibold"
          >
            <Languages size={16} />
            {language}
            <ChevronDown size={13} />
          </button>

          {languageOpen && (
            <div className="absolute right-0 top-12 z-50 w-36 rounded-xl border border-[var(--border)] bg-[var(--surface)] p-1 shadow-xl">
              {[
                ["EN", "English"],
                ["HI", "हिन्दी"],
                ["KN", "ಕನ್ನಡ"],
              ].map(([code, label]) => (
                <button
                  key={code}
                  onClick={() => {
                    setLanguage(code);
                    setLanguageOpen(false);
                  }}
                  className="w-full rounded-lg px-3 py-2 text-left text-xs hover:bg-[var(--surface-muted)]"
                >
                  {label}
                </button>
              ))}
            </div>
          )}
        </div>

        <button
          aria-label="Toggle theme"
          onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          className="flex h-10 w-10 items-center justify-center rounded-xl border border-[var(--border)] hover:bg-[var(--surface-muted)]"
        >
          <Sun size={17} className="hidden dark:block" />
          <Moon size={17} className="dark:hidden" />
        </button>

        <button
          aria-label="Notifications"
          className="relative flex h-10 w-10 items-center justify-center rounded-xl border border-[var(--border)] hover:bg-[var(--surface-muted)]"
        >
          <Bell size={17} />
          <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-orange-500" />
        </button>

        <div className="hidden items-center gap-2 sm:flex">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-[var(--primary-soft)] text-xs font-bold text-[var(--primary)]">
            {user?.name?.charAt(0).toUpperCase() || "F"}
          </div>
          <div className="hidden max-w-32 xl:block">
            <p className="truncate text-xs font-bold">{user?.name}</p>
            <p className="text-[10px] muted-text">{user?.role}</p>
          </div>
        </div>

        <button
          onClick={handleLogout}
          disabled={loggingOut}
          title="Logout"
          aria-label="Logout"
          className="flex h-10 w-10 items-center justify-center rounded-xl border border-[var(--border)] text-red-500 hover:bg-red-50 disabled:opacity-50"
        >
          <LogOut size={17} />
        </button>
      </div>
    </header>
  );
}
