"use client";
import { Box, Grid, Tab, Tabs, Typography } from "@mui/material";
import * as React from "react";
import { SyntheticEvent, useState } from "react";
import {
  ApplicationCard,
  CenteredCircularProgress,
  SimpleSnackbar,
} from "@/app/components";
import { a11yProps, CustomTabPanel } from "@/app/private/tabs";
import Link from "next/link";
import { ApplicationData, useApplications, useUser } from "@/app/lib";

export default function AppList() {
  const [activeTabIndex, setActiveTabIndex] = useState(0);
  const [isDeleted, setIsDeleted] = useState(false);

  const { getAllApps } = useApplications();
  const { savedAppsApiState } = useUser();
  const createdApps = getAllApps().data;
  const isLoadingCreatedApps = getAllApps().isMutating;

  const data = activeTabIndex === 0 ? createdApps : savedAppsApiState.data;
  const isLoading =
    activeTabIndex === 0 ? isLoadingCreatedApps : savedAppsApiState.isMutating;

  if (isLoading) return <CenteredCircularProgress />;

  const handleChange = (event: SyntheticEvent, newValue: number) => {
    setActiveTabIndex(newValue);
  };

  return (
    <>
      <Typography
        variant="h2"
        component="h2"
        align="center"
        color="text.secondary"
      >
        MY APPS
      </Typography>
      <Box
        sx={{
          width: "100%",
          a: {
            color: "inherit",
            textDecoration: "none",
            textTransform: "uppercase",
          },
        }}
      >
        <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
          <Tabs
            value={activeTabIndex}
            onChange={handleChange}
            textColor="secondary"
            indicatorColor="secondary"
            aria-label="basic tabs example"
            sx={{ fontSize: "2rem" }}
          >
            <Tab label="CREATED" {...a11yProps(0)} />
            <Tab label="SAVED" {...a11yProps(1)} />
          </Tabs>
        </Box>
        <CustomTabPanel value={activeTabIndex} index={0}>
          {data?.length === 0 && (
            <Grid item>
              <Typography align="center" variant="h4">
                <Link href="./create">CREATE MY FIRST APPLICATION</Link>
              </Typography>
            </Grid>
          )}
          {data?.map((app: ApplicationData) => (
            <ApplicationCard
              variant="myApps"
              onClose={() => setIsDeleted(true)}
              appData={app}
              key={app.id}
            />
          ))}
        </CustomTabPanel>
        <CustomTabPanel value={activeTabIndex} index={1}>
          {data?.length === 0 && (
            <Grid item>
              <Typography align="center" variant="h4">
                <Link href="./store">PICK FROM APP STORE</Link>
              </Typography>
            </Grid>
          )}
          {data?.map((app: ApplicationData) => (
            <ApplicationCard
              variant="usedApps"
              appData={app}
              onClose={() => setIsDeleted(true)}
              key={app.id}
            />
          ))}
        </CustomTabPanel>
      </Box>
      <SimpleSnackbar
        message={"App deleted"}
        isOpen={isDeleted}
        onClose={() => setIsDeleted(false)}
      />
    </>
  );
}
