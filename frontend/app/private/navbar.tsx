"use client";

import { MouseEvent, useState } from "react";
import MenuIcon from "@mui/icons-material/Menu";
import Link from "next/link";
import TipsAndUpdatesOutlinedIcon from "@mui/icons-material/TipsAndUpdatesOutlined";
import {
  alpha,
  AppBar,
  Box,
  Button,
  Container,
  IconButton,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
  useTheme,
} from "@mui/material";

const pages = [
  {
    name: "My Applications",
    link: "/private",
  },
  {
    name: "Create Application",
    link: "/private/create",
  },
  {
    name: "App Store",
    link: "/private/store",
  },
  {
    name: "Account",
    link: "/private/user",
  },
  {
    name: "Logout",
    link: "/private/logout",
  },
];
const ApplicationName: string = "APP WIZARD";

function ResponsiveAppBar() {
  const [anchorElNav, setAnchorElNav] = useState<null | HTMLElement>(null);
  const theme = useTheme();
  const textPrimary = theme.palette.text.primary;

  const handleOpenNavMenu = (event: MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  return (
    <AppBar
      position="sticky"
      sx={{
        borderRadius: "0.7rem",
        margin: "0 auto 2vh",
        width: "80vw",
        border: "1px solid black",
        boxShadow: "inset 0 0 10px rgba(0, 0, 0, 0.5)",
        backgroundColor: alpha(theme.palette.primary.main, 0.8),
      }}
    >
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <TipsAndUpdatesOutlinedIcon
            sx={{
              display: { xs: "none", md: "flex" },
              mr: 1,
              color: "warning.main",
            }}
          />
          <Typography
            variant="h6"
            component="h1"
            noWrap
            sx={{
              mr: 2,
              display: { xs: "none", md: "flex" },
              letterSpacing: ".1rem",
              color: "warning.main",
              textShadow: "0 0 0.2rem rgba(0, 0, 0, 1)",
            }}
          >
            <Link
              href="/private"
              style={{ textDecoration: "none", color: "inherit" }}
            >
              {ApplicationName}
            </Link>
          </Typography>

          <Box
            sx={{
              flexGrow: 1,
              display: {
                xs: "flex",
                md: "none",
              },
            }}
          >
            <IconButton
              sx={{
                color: "text.primary",
              }}
              size="large"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: "bottom",
                horizontal: "left",
              }}
              keepMounted
              transformOrigin={{
                vertical: "top",
                horizontal: "left",
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: "block", md: "none" },
              }}
            >
              {pages.map((page) => (
                <MenuItem
                  key={page.name}
                  component={Link}
                  href={page.link}
                  onClick={handleCloseNavMenu}
                >
                  <Typography textAlign="center">{page.name}</Typography>
                </MenuItem>
              ))}
            </Menu>
          </Box>
          <TipsAndUpdatesOutlinedIcon
            sx={{
              display: { xs: "flex", md: "none" },
              mr: 1,
              color: "warning.main",
            }}
          />
          <Typography
            variant="h5"
            component="h1"
            noWrap
            sx={{
              mr: 2,
              display: { xs: "flex", md: "none" },
              flexGrow: 1,
              letterSpacing: ".1rem",
              color: "warning.main",
            }}
          >
            <Link
              href="/private"
              style={{ textDecoration: "none", color: "inherit" }}
            >
              {ApplicationName}
            </Link>
          </Typography>
          <Box
            sx={{
              flexGrow: 1,
              display: { xs: "none", md: "flex" },
            }}
          >
            {pages.map((page) => (
              <Button
                key={page.name}
                component={Link}
                href={page.link}
                onClick={handleCloseNavMenu}
                sx={{
                  my: 2,
                  fontSize: "1.2rem",
                  marginRight: "0.5rem",
                  color: "text.primary",
                  textShadow: "0 0 0.2rem rgba(0, 0, 0, 1)",
                  textAlign: "center",
                  "&:hover": {
                    backgroundColor: alpha(textPrimary, 0.3),
                  },
                }}
              >
                {page.name}
              </Button>
            ))}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
}

export default ResponsiveAppBar;
