
"use client";

import {
  LayoutDashboard,
  Sprout,
  Droplets,
  CloudSun,
  TrendingUp,
  BookOpen,
  Bot,
  CalendarDays,
  Settings,
  CircleHelp,
  X,
  Leaf,
} from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";

const navigation = [
  { label: "Overview", icon: LayoutDashboard, href: "/" },
  { label: "My Crops", icon: Sprout, href: "/crops" },
  { label: "Soil Health", icon: Droplets, href: "#soil" },
  { label: "Weather", icon: CloudSun, href: "/weather" },
  { label: "Market Prices", icon: TrendingUp, href: "#market" },
  { label: "Advisories", icon: BookOpen, href: "#advisories" },
  { label: "AI Assistant", icon: Bot, href: "#assistant" },
  { label: "Crop Schedule", icon: CalendarDays, href: "#schedule" },
];

type SidebarProps = {
  open: boolean;
  onClose: () => void;
};

export default function Sidebar({ open, onClose }: SidebarProps) {
  const pathname = usePathname();

  return (
    <>
      {open && (
        <button
          aria-label="Close navigation"
          onClick={onClose}
          className="fixed inset-0 z-40 bg-black/40 lg:hidden"
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-[270px] flex-col border-r border-[var(--border)] bg-[var(--surface)] transition-transform duration-300 lg:static lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex h-[82px] items-center justify-between border-b border-[var(--border)] px-6">
          <Link href="/" className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[var(--primary)] text-white">
              <Leaf size={23} />
            </div>

            <div>
              <h1 className="text-xl font-bold tracking-tight">
                Krishi<span className="text-[var(--primary)]">Mitra</span>
              </h1>
              <p className="text-[10px] tracking-[2px] muted-text">
                SMART FARMING
              </p>
            </div>
          </Link>

          <button
            onClick={onClose}
            className="rounded-lg p-2 hover:bg-[var(--surface-muted)] lg:hidden"
            aria-label="Close sidebar"
          >
            <X size={19} />
          </button>
        </div>

        <div className="custom-scrollbar flex-1 overflow-y-auto px-4 py-6">
          <p className="mb-4 px-3 text-[10px] font-bold uppercase tracking-[2px] muted-text">
            Workspace
          </p>

          <nav className="space-y-1">
            {navigation.map((item) => {
              const Icon = item.icon;
              const active = pathname === item.href;

              return (
                <Link
                  key={item.label}
                  href={item.href}
                  onClick={onClose}
                  className={`flex items-center gap-3 rounded-xl px-3 py-3 text-[13px] font-medium transition ${
                    active
                      ? "bg-[var(--primary-soft)] text-[var(--primary)]"
                      : "muted-text hover:bg-[var(--surface-muted)] hover:text-[var(--foreground)]"
                  }`}
                >
                  <Icon size={18} strokeWidth={1.8} />
                  {item.label}

                  {item.label === "AI Assistant" && (
                    <span className="ml-auto rounded-md bg-[var(--primary-soft)] px-2 py-0.5 text-[9px] font-bold text-[var(--primary)]">
                      AI
                    </span>
                  )}
                </Link>
              );
            })}
          </nav>

          <div className="my-7 border-t border-[var(--border)]" />

          <p className="mb-4 px-3 text-[10px] font-bold uppercase tracking-[2px] muted-text">
            Preferences
          </p>

          <a
            href="#settings"
            className="flex items-center gap-3 rounded-xl px-3 py-3 text-[13px] font-medium muted-text hover:bg-[var(--surface-muted)]"
          >
            <Settings size={18} />
            Settings
          </a>

          <a
            href="#help"
            className="flex items-center gap-3 rounded-xl px-3 py-3 text-[13px] font-medium muted-text hover:bg-[var(--surface-muted)]"
          >
            <CircleHelp size={18} />
            Help & Support
          </a>
        </div>

        <div className="border-t border-[var(--border)] p-4">
          <div className="rounded-2xl bg-[var(--primary-soft)] p-4">
            <div className="mb-2 flex items-center gap-2 text-[var(--primary)]">
              <Sprout size={17} />
              <span className="text-xs font-bold">Grow smarter</span>
            </div>
            <p className="text-xs leading-5 muted-text">
              Make better farming decisions with trusted agricultural insights.
            </p>
          </div>

          <p className="mt-4 text-center text-[10px] muted-text">
            KrishiMitra © 2026
          </p>
        </div>
      </aside>
    </>
  );
}
