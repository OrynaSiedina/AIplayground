"use client";
import { SWRConfig } from "swr";
import { ReactNode, useState } from "react";
import { SimpleSnackbar } from ".";
import { createPortal } from "react-dom";

interface SWRProviderProps {
  children: ReactNode;
}

export const SWRProvider = ({ children }: SWRProviderProps) => {
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const errorBar =
    typeof window !== "undefined"
      ? window.document.getElementById("error-bar") || window.document.body
      : null;

  return (
    <SWRConfig
      value={{
        onError: (err) => {
          let errorMessage;
          if (err.status === 500 || !err.message) {
            errorMessage = "Ooops, something went wrong.";
          } else {
            errorMessage = err.message;
          }
          setErrorMessage(errorMessage);
          setShowError(true);
        },
        shouldRetryOnError: false,
      }}
    >
      {!!errorBar &&
        createPortal(
          <SimpleSnackbar
            isError={showError}
            message={errorMessage}
            isOpen={showError}
            onClose={() => setShowError(false)}
          />,
          errorBar,
        )}
      {children}
    </SWRConfig>
  );
};
