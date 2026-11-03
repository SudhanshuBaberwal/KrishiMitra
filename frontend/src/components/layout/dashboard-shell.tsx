
"use client";

import { ReactNode, useState } from "react";
import Sidebar from "./sidebar";
import Topbar from "./topbar";

export default function DashboardShell({
  children,
}: {
  children: ReactNode;
}) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-[var(--background)] lg:flex">
      <Sidebar
        open={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      <div className="min-w-0 flex-1">
        <Topbar onMenuClick={() => setSidebarOpen(true)} />
        <main className="mx-auto max-w-[1600px] px-4 py-6 sm:px-6 lg:px-9 lg:py-8">
          {children}
        </main>
      </div>
    </div>
  );
}
