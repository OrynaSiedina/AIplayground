import * as React from "react";
import { useEffect, useRef } from "react";
import { Alert, Snackbar } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { ConditionalWrapper } from "./";

interface SimpleSnackbarProps {
  isError?: boolean;
  message: string;
  isOpen: boolean;
  onClose?: () => void;
  sx?: Object;
}

export function SimpleSnackbar({
  isError = false,
  message,
  isOpen,
  onClose,
}: SimpleSnackbarProps) {
  const theme = useTheme();
  const snackbarRef = useRef(null);
  const handleClose = () => {
    onClose?.();
  };

  useEffect(() => {
    // Add a click event listener to the window
    const handleGlobalClick = () => {
      if (
        isOpen &&
        snackbarRef.current
      ) {
        handleClose();
      }
    };

    window.addEventListener("click", handleGlobalClick);

    return () => {
      window.removeEventListener("click", handleGlobalClick);
    };
  }, [isOpen]);

  return (
    <ConditionalWrapper
      condition={!isError}
      wrapper={(children) => (
        <Snackbar open={isOpen} autoHideDuration={3000} onClose={handleClose} ref={snackbarRef}>
          {children}
        </Snackbar>
      )}
    >
      <Alert
        onClose={handleClose}
        severity={isError ? "error" : "success"}
        sx={
          isError
            ? {
                backgroundColor: theme.palette.error.dark,
                color: theme.palette.error.contrastText,
              }
            : {}
        }
      >
        {message}
      </Alert>
    </ConditionalWrapper>
  );
}
