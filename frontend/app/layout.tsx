import "./styles/globals.css";
import type { Metadata } from "next";
import { ThemeRegistry } from "../theme/ThemeRegistry";
import { ReactNode } from "react";
import { SWRProvider } from "@/app/components";

export interface LayoutProps {
  children: ReactNode;
}

// noinspection JSUnusedGlobalSymbols
export const metadata: Metadata = {
  title: "AI playground",
};

// noinspection JSUnusedGlobalSymbols
export default function RootLayout({ children }: LayoutProps) {
  return (
    <html lang="en">
      <ThemeRegistry>
        <SWRProvider>
          <body>{children}</body>
        </SWRProvider>
      </ThemeRegistry>
    </html>
  );
}
