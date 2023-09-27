import * as React from "react";
import { ReactNode, useState } from "react";
import AppBar from "@mui/material/AppBar";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import { Form } from "./form";
import styles from "@/app/styles/authorization.module.css";

interface TabPanelProps {
  children?: ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index } = props;

  return (
    <div
      style={{ padding: " 0 2rem 0" }}
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

export default function FullWidthTabs() {
  const [value, setValue] = useState(0);

  const handleChange = (event: React.SyntheticEvent, newValue: number) => {
    setValue(newValue);
  };

  return (
    <Box
      sx={{
        backgroundColor: "primary.main",
        maxWidth: 600,
        width: "100%",
        padding: "0",
        height: "fit-content",
      }}
    >
      <AppBar position="static">
        <Tabs
          value={value}
          onChange={handleChange}
          indicatorColor="secondary"
          textColor="inherit"
          variant="fullWidth"
          aria-label="full width tabs example"
        >
          <Tab label="LOGIN" />
          <Tab label="SIGN UP" />
        </Tabs>
      </AppBar>
      <div className={styles.errorContainer} id="error-bar"></div>
      <TabPanel value={value} index={0}>
        <Form id="signIn" />
      </TabPanel>
      <TabPanel value={value} index={1}>
        <Form id="signUp" />
      </TabPanel>
    </Box>
  );
}
