"use client";
import { API_ENDPOINTS, useAuth } from "@/app/lib";
import { alpha, CircularProgress, Grid, Paper, useTheme } from "@mui/material";
import * as React from "react";
import { useEffect, useState } from "react";
import SuccessPage from "./SuccessPage";
import { useRouter } from "next/navigation";

export default function VerificationPage({
  params,
}: {
  params: { token: string };
}) {
  const [isSuccess, setIsSuccess] = useState(false);
  const { verify, verifyApiState } = useAuth();
  const router = useRouter();

  useEffect(() => {
    verify(`${API_ENDPOINTS.VERIFY}?token=${params.token}`, () => {
      setIsSuccess(true);
    });
  }, []);
  const theme = useTheme();

  if (verifyApiState.isError) {
    router.push(`/verification/${params.token}/error`);
  }

  return (
    <Paper
      elevation={6}
      sx={{
        maxWidth: "80vw",
        minHeight: "fit-content",
        margin: "0 auto 0",
        padding: "2rem 1rem 2rem",
        backgroundColor: alpha(theme.palette.primary.main, 0.8),
        border: "1px solid black",
        boxShadow: "inset 0 0 10px rgba(0, 0, 0, 0.5)",
      }}
    >
      <Grid
        container
        spacing={{ xs: 1, sm: 2 }}
        direction="column"
        justifyContent="flex-start"
        alignItems="stretch"
      >
        {isSuccess ? (
          <SuccessPage />
        ) : (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              height: "100vh",
            }}
          >
            <CircularProgress color="secondary" size={100} />
          </div>
        )}
      </Grid>
    </Paper>
  );
}
