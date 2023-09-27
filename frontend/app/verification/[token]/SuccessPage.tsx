import * as React from "react";
import { Button, Grid, Typography } from "@mui/material";
import { useRouter } from "next/navigation";

export default function SuccessPage() {
  const router = useRouter();

  const redirectToLogin = () => {
    router.push("/authorization");
  };

  return (
    <Grid container justifyContent="center" alignItems="center" spacing={2}>
      <Grid item xs={12}>
        <Typography
          variant="h3"
          component="h3"
          align="center"
          color="text.secondary"
        >
          Your account has been verified!
        </Typography>
      </Grid>
      <Grid item>
        <Button
          variant="text"
          size="large"
          color="inherit"
          onClick={redirectToLogin}
          sx={{ cursor: "pointer" }}
        >
          <Typography
            variant="h4"
            component="h4"
            align="center"
            color="text.secondary"
          >
            Click here to log in
          </Typography>
        </Button>
      </Grid>
    </Grid>
  );
}
