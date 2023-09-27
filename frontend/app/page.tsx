"use client";
import {
  alpha,
  Button,
  Grid,
  Paper,
  Typography,
  useTheme,
} from "@mui/material";
import TipsAndUpdatesOutlinedIcon from "@mui/icons-material/TipsAndUpdatesOutlined";
import Link from "next/link";
import React from "react";
import useWindowDimensions from "@/app/lib/useWindowDimensions";
import { CenteredCircularProgress } from "@/app/components";

export default function Index() {
  const theme = useTheme();
  const { width } = useWindowDimensions();
  const isScreenSizeDetermined = width !== undefined;
  const isSmallScreen = isScreenSizeDetermined ? width <= 1100 : false;

  const text = theme.palette.text.primary;
  const highlight = theme.palette.warning.main;

  if (!isScreenSizeDetermined) return <CenteredCircularProgress />;

  return (
    <>
      <Paper
        elevation={6}
        sx={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          maxWidth: isSmallScreen ? "100vw" : "95vw",
          height: isSmallScreen ? "fit-content" : "95vh",
          margin: "0 auto 0",
          padding: isSmallScreen ? "1rem" : "2rem",
          backgroundColor: alpha(theme.palette.primary.main, 0.5),
          border: "1px solid black",
          boxShadow: "inset 0 0 10px rgba(0, 0, 0, 0.5)",
        }}
      >
        <Grid container id="logo" direction="row">
          <Grid item>
            <TipsAndUpdatesOutlinedIcon
              sx={{
                color: "warning.main",
                fontSize: "3.5rem",
              }}
            />
          </Grid>
          <Grid item id="icon">
            <Typography
              variant="h6"
              component="h1"
              noWrap
              sx={{
                letterSpacing: ".1rem",
                color: "warning.main",
                textShadow: "0 0 0.2rem rgba(0, 0, 0, 1)",
                marginBottom: "1rem",
              }}
            >
              <Link
                href="/"
                style={{
                  textDecoration: "none",
                  color: "inherit",
                  fontSize: "2.5rem",
                }}
              >
                APP WIZARD
              </Link>
            </Typography>
          </Grid>
        </Grid>
        <Grid
          container
          direction={isSmallScreen ? "column" : "row"}
          justifyContent="center"
          alignItems="space-around"
          sx={{
            margin: isSmallScreen ? "1rem 0" : "2.5rem 0",
          }}
        >
          <Grid item md id="text">
            <Grid
              container
              id="title"
              wrap="nowrap"
              direction="column"
              alignItems="center"
              justifyContent="center"
              rowSpacing={2}
            >
              <Grid item id="title" justifySelf={"center"}>
                <Typography
                  noWrap
                  variant={isSmallScreen ? "h3" : "h2"}
                  fontWeight={700}
                  color={text}
                >
                  YOUR WAY,&nbsp;
                  <span
                    style={{
                      color: `${highlight}`,
                    }}
                  >
                    YOUR APP
                  </span>
                </Typography>
              </Grid>
              <Grid item id="click">
                <Typography
                  variant={isSmallScreen ? "h3" : "h2"}
                  fontWeight={700}
                  justifySelf={"center"}
                  color={text}
                >
                  ONE CLICK
                </Typography>
              </Grid>
              <Grid item id="p">
                <Typography
                  variant="h4"
                  textAlign="center"
                  fontWeight={700}
                  color={text}
                >
                  <span style={{ display: "block" }}>
                    Your dream app in one click.{" "}
                  </span>
                  <span
                    style={{
                      display: "block",
                      color: `${highlight}`,
                    }}
                  >
                    No coding stress, no long waits.
                  </span>
                  <span style={{ display: "block" }}>
                    Combine your imagination with AI.
                  </span>
                </Typography>
              </Grid>
              <Grid item id="button">
                <Button
                  size="large"
                  href="/private/create"
                  variant="contained"
                  color="warning"
                  sx={{ marginTop: "2rem", fontSize: "1.5rem" }}
                >
                  Create Dream App
                </Button>
              </Grid>
              <Grid item>
                <Button
                  size="large"
                  href="/authorization"
                  variant="text"
                  color="warning"
                  sx={{ fontSize: "1.5rem" }}
                >
                  LOGIN
                </Button>
              </Grid>
            </Grid>
          </Grid>
          <Grid item id="picture" xs={12} md={5} alignSelf="center">
            <div
              style={{
                width: isSmallScreen ? "15rem" : "100%",
                height: isSmallScreen ? "15rem" : "25rem",
                backgroundImage: "url(/home-font.png)",
                backgroundSize: "contain",
                backgroundRepeat: "no-repeat",
                backgroundPosition: "center",
              }}
            ></div>
          </Grid>
        </Grid>
      </Paper>
    </>
  );
}
