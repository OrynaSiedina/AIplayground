import {
  Card,
  CardActions,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import * as React from "react";
import { useTheme } from "@mui/material/styles";
import { GetButtons } from "./GetButtons";
import { ApplicationData } from "@/app/lib";

export type ApplicationProps = {
  appData: ApplicationData;
  variant: "myApps" | "usedApps" | "store" | "page";
  onClose?: () => void;
  showCategory?: boolean;
};

export function ApplicationCard(props: ApplicationProps) {
  const { showCategory = true } = props;
  return (
    <Grid item>
      <Card
        sx={{
          backgroundColor: "transparent",
          boxShadow: `0px 2px 8px ${useTheme().palette.primary.light}`,
        }}
      >
        <Grid
          container
          alignItems={props.variant === "store" ? "stretch" : "center"}
          direction={props.variant === "store" ? "column" : "row"}
          sx={{ padding: "0 1rem" }}
        >
          <Grid item xs sm>
            <CardContent
              sx={{
                padding: 0,
                "&:last-child": {
                  paddingBottom: 0,
                },
              }}
            >
              <Typography variant="h3" component="h5" color="text.secondary">
                {props.appData.name}
              </Typography>
              {(props.variant === "usedApps" ||
                props.variant === "myApps" ||
                props.variant === "page") && (
                <Typography
                  variant="h6"
                  content="h6"
                  sx={{ mb: 1.5 }}
                  color="text.primary"
                >
                  {props.appData.description}
                </Typography>
              )}
              {(props.variant === "usedApps" ||
                props.variant === "store" ||
                props.variant === "page") && (
                <>
                  <Typography
                    variant="h6"
                    content="h6"
                    sx={{ mb: 1.5 }}
                    color="text.primary"
                  >
                    {props.appData.owner}
                  </Typography>
                  {showCategory && (
                    <Typography
                      variant="h6"
                      content="h6"
                      sx={{ mb: 1.5 }}
                      color="text.primary"
                    >
                      {props.appData.category}
                    </Typography>
                  )}
                </>
              )}
            </CardContent>
          </Grid>
          <Grid item xs={12} sm={3}>
            <CardActions sx={{ padding: "1rem 0 1rem 0 " }}>
              <Grid
                container
                spacing={1}
                direction="column"
                justifyContent={
                  props.variant === "store" ? "flex-start" : "flex-end"
                }
                alignItems="stretch"
              >
                {GetButtons(props)}
              </Grid>
            </CardActions>
          </Grid>
        </Grid>
      </Card>
    </Grid>
  );
}
