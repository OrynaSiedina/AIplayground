"use client";
import { API_ENDPOINTS, useAuth } from "@/app/lib";
import {
  alpha,
  Button,
  Grid,
  Paper,
  Typography,
  useTheme,
} from "@mui/material";
import * as React from "react";
import { useState } from "react";
import { SimpleSnackbar } from "@/app/components";

export default function ErrorPage({ params }: { params: { token: string } }) {
  const theme = useTheme();
  const [isOpen, setIsOpen] = useState(false);
  const { repeatVerify } = useAuth();

  const verify = () => {
    repeatVerify(`${API_ENDPOINTS.VERIFY}?token=${params.token}`, () => {
      setIsOpen(true);
    });
  };

  return (
    <div>
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
        <Grid container justifyContent="center" alignItems="center" spacing={2}>
          <Grid item>
            <Grid item>
              <div id="error-bar" style={{ maxWidth: "80vw" }}></div>
            </Grid>
            <Button
              variant="text"
              size="large"
              color="inherit"
              onClick={verify}
              sx={{ cursor: "pointer" }}
            >
              <Typography
                variant="h4"
                component="h4"
                align="center"
                color="text.secondary"
              >
                Click here to send new verification email
              </Typography>
            </Button>
          </Grid>
        </Grid>
      </Paper>
      <SimpleSnackbar
        message="New verification email has been sent. Please check your email."
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
      />
    </div>
  );
}
