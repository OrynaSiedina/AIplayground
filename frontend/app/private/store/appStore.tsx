"use client";
import { Box, Grid, Pagination, Tab, Tabs, Typography } from "@mui/material";
import { ApplicationCard } from "@/app/components";
import { a11yProps, CustomTabPanel } from "@/app/private/tabs";
import * as React from "react";
import { SyntheticEvent, useState } from "react";
import { ApplicationData, useStore } from "@/app/lib";

export function AppStore() {
  const [tab, setTab] = useState({ index: 0, category: "" });
  const [page, setPage] = useState(1);
  const { categories, applicationsApiState } = useStore(tab.category, page);

  const handleChangePage = (
    event: React.ChangeEvent<unknown>,
    value: number,
  ) => {
    event.preventDefault();
    setPage(value);
  };

  const handleChange = (event: SyntheticEvent, newTabIndex: number) => {
    const newCategory =
      newTabIndex === 0 ? "" : categories[newTabIndex - 1].name;
    setTab({ category: newCategory, index: newTabIndex });
    setPage(1);
  };

  return (
    <Grid
      container
      spacing={2}
      direction="column"
      justifyContent="flex-start"
      alignItems="stretch"
    >
      <Grid item xs={12}>
        <Typography
          variant="h2"
          component="h2"
          align="center"
          color="text.secondary"
          gutterBottom
        >
          APP STORE
        </Typography>
      </Grid>
      <Box sx={{ borderBottom: 1, borderColor: "divider", width: "100%" }}>
        <Tabs
          value={tab.index}
          variant="scrollable"
          onChange={handleChange}
          textColor="secondary"
          indicatorColor="secondary"
          aria-label="basic tabs example"
        >
          <Tab label="ALL" {...a11yProps(0)} />
          {categories &&
            categories.map(
              (
                category: {
                  id: number;
                  name: string;
                  applications: ApplicationData[];
                },
                index: number,
              ) => (
                <Tab
                  label={category.name}
                  {...a11yProps(index + 1)}
                  key={category.id}
                />
              ),
            )}
        </Tabs>
      </Box>
      {!!applicationsApiState.data && (
        <CustomTabPanel value={tab.index} index={tab.index}>
          <Grid item xs={12}>
            <Grid container spacing={2}>
              {applicationsApiState.data.apps.map((app: ApplicationData) => (
                <Grid item xs={12} sm={6} md={4} lg={3} key={app.id}>
                  <ApplicationCard
                    variant="store"
                    appData={app}
                    showCategory={tab.index === 0}
                  />
                </Grid>
              ))}
            </Grid>
          </Grid>
        </CustomTabPanel>
      )}
      {!!applicationsApiState.data?.totalPages &&
        !!applicationsApiState.data.apps && (
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              marginTop: 4,
            }}
          >
            <Pagination
              count={applicationsApiState.data.totalPages}
              page={page}
              onChange={handleChangePage}
              size="large"
            />
          </Box>
        )}
    </Grid>
  );
}
