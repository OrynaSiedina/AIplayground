"use client";
import { AuthorizationForm } from "./authformbox";
import styles from "../styles/authformbox.module.css";
import useWindowDimensions from "@/app/lib/useWindowDimensions";
import { CenteredCircularProgress } from "@/app/components";
import * as React from "react";

export default function AuthorizationPage() {
  const { width } = useWindowDimensions();
  const isScreenSizeDetermined = width !== undefined;
  const isSmallScreen = isScreenSizeDetermined ? width <= 768 : false;

  if (!isScreenSizeDetermined) return <CenteredCircularProgress />;

  return (
    <div className={styles.authContainer}>
      <AuthorizationForm isSmallScreen={isSmallScreen} />
    </div>
  );
}
