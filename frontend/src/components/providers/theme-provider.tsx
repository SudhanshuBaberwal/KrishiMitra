
"use client";

import { ThemeProvider as NextThemeProvider } from "next-themes";
import { ReactNode } from "react";
import { AuthProvider } from "./auth-provider";

export default function ThemeProvider({
  children,
}: {
  children: ReactNode;
}) {
  return (
    <NextThemeProvider
      attribute="class"
      defaultTheme="light"
      enableSystem
      disableTransitionOnChange
    >
      <AuthProvider>{children}</AuthProvider>
    </NextThemeProvider>
  );
}
