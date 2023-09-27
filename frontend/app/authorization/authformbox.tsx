"use client";
import { useState } from "react";
import { Form } from "./form";
import styles from "@/app/styles/authorization.module.css";
import buttonStyles from "@/app/styles/button.module.css";
import LoginSmallScreen from "@/app/authorization/LoginSmallScreen";

interface AuthFromProps {
  logIn?: boolean;
  isSmallScreen?: boolean;
}

export function AuthorizationForm({
  logIn = true,
  isSmallScreen,
}: AuthFromProps) {
  const [isRightPanelActive, setIsRightPanelActive] = useState(logIn);

  return (
    <>
      {isSmallScreen ? (
        <LoginSmallScreen />
      ) : (
        <>
          <div className={styles.errorContainer} id="error-bar"></div>
          <main
            className={`${isRightPanelActive ? styles.rightPanelActive : ""} ${
              styles.container
            }`}
          >
            <section
              className={`${styles.card} ${
                isRightPanelActive ? styles.signIn : styles.signUp
              }`}
            >
              <Form id={isRightPanelActive ? "signUp" : "signIn"} />
            </section>

            <section
              className={`${styles.card} ${
                isRightPanelActive ? styles.signUp : styles.signIn
              }`}
            >
              <Form id={isRightPanelActive ? "signIn" : "signUp"} />
            </section>

            <section className={styles.containerOverlay}>
              <div className={styles.overlay}>
                <div className={`${styles.panel} ${styles.overlayLeft}`}>
                  <button
                    onClick={() => setIsRightPanelActive(false)}
                    className={buttonStyles.btn}
                    type="button"
                  >
                    Sign Up
                  </button>
                </div>
                <div className={`${styles.panel} ${styles.overlayRight}`}>
                  <button
                    onClick={() => setIsRightPanelActive(true)}
                    className={buttonStyles.btn}
                    type="button"
                  >
                    LOGIN
                  </button>
                </div>
              </div>
            </section>
          </main>
        </>
      )}
    </>
  );
}
