import { createTheme, ThemeOptions } from "@mui/material/styles";
import { responsiveFontSizes } from "@mui/material";

const textShadow = "0 0 0.3rem rgba(0, 0, 0, 1)";
const textShadow2 = "0 0 0.2rem rgba(0, 0, 0, 0.5)";

const themeOptions: ThemeOptions = {
  palette: {
    primary: {
      main: "rgba(19, 56, 99)",
      light: "rgb(58,255,237)",
      dark: "#1D3C76",
    },
    secondary: {
      main: "rgb(248,95,193)",
      light: "#FFE6FF",
      dark: "#600064",
    },
    text: {
      primary: "rgba(58,210,237)",
      secondary: "rgb(255,115,205)",
    },
    info: {
      main: "rgba(58,210,237)",
    },
    error: {
      main: "rgb(255,50,50)",
      light: "rgba(252,161,152,0.7)",
      dark: "rgba(183,28,28,0.8)",
      contrastText: "#a8f5bb",
    },
    success: {
      main: "rgb(11, 255, 139)",
    },
    warning: {
      main: "rgb(255,255,125)",
    },
    action: {
      hoverOpacity: 0.3,
    },
  },
  shape: {
    borderRadius: 8,
  },
  typography: {
    fontWeightLight: "500",
    fontFamily: [
      "Exo_2",
      "Roboto",
      "-apple-system",
      "BlinkMacSystemFont",
      '"Segoe UI"',
      "Oxygen",
      "Ubuntu",
      "Cantarell",
      '"Open Sans"',
      '"Helvetica Neue"',
      "sans-serif",
    ].join(","),
  },
  components: {
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          fontSize: "1.5rem",
        },
      },
    },
    MuiInputLabel: {
      styleOverrides: {
        root: {
          fontSize: "1.5rem",
          marginTop: "-1rem",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        outlined: {
          border: "1px solid",
        },
      },
    },
    MuiTypography: {
      styleOverrides: {
        h1: {
          textShadow: textShadow,
        },
        h2: {
          textShadow: textShadow,
        },
        h3: {
          textShadow: textShadow,
        },
        h4: {
          textShadow: textShadow2,
        },
        h5: {
          textShadow: textShadow2,
        },
        h6: {
          textShadow: textShadow2,
        },
      },
    },
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundSize: "cover",
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          fontSize: "1.2rem",
        },
      },
    },
  },
};

export const theme = responsiveFontSizes(createTheme(themeOptions));
