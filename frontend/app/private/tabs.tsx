import * as React from "react";
import { ReactNode } from "react";
import { Box, Grid } from "@mui/material";

interface TabPanelProps {
  children?: ReactNode;
  index: number;
  value: number;
}

export function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
    >
      {value === index && (
        <Box sx={{ paddingLeft: "1rem", paddingTop: "1rem" }}>
          <Grid
            container
            spacing={{ xs: 1, sm: 2 }}
            direction="column"
            justifyContent="center"
            alignItems="stretch"
          >
            {children}
          </Grid>
        </Box>
      )}
    </div>
  );
}

export function a11yProps(index: number) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}
