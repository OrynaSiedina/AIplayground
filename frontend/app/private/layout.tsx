"use client";
import { LayoutProps } from "../layout";
import ResponsiveAppBar from "./navbar";
import { alpha, Paper, useTheme } from "@mui/material";
import { RouteGuard } from "@/app/components/RouteGuard";

export default function PrivateLayout({ children }: LayoutProps) {
  return (
    <>
      <RouteGuard>
        <ResponsiveAppBar />
        <Paper
          elevation={6}
          sx={{
            maxWidth: "80vw",
            minHeight: "80vh",
            margin: "0 auto 0",
            padding: "2rem 1rem 2rem",
            backgroundColor: alpha(useTheme().palette.primary.main, 0.92),
            border: "1px solid black",
            boxShadow: "inset 0 0 10px rgba(0, 0, 0, 0.5)",
          }}
        >
          <div id="error-bar"></div>
          {children}
        </Paper>
      </RouteGuard>
    </>
  );
}
