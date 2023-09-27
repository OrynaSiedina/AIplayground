"use client";
import { CreateFormData, Run, useApplications } from "@/app/lib";
import { FormEvent, useState } from "react";
import { CustomInput, SimpleSnackbar } from "@/app/components";
import PolylineOutlinedIcon from "@mui/icons-material/PolylineOutlined";
import ReplayOutlinedIcon from "@mui/icons-material/ReplayOutlined";
import { Button, Grid, LinearProgress, Typography } from "@mui/material";
import PlayArrowOutlinedIcon from "@mui/icons-material/PlayArrowOutlined";
import { LoadingMessage } from "./Messages";

export function Form() {
  const buttonStyle = {
    width: "15rem",
    fontSize: "1.2rem",
    fontWeight: 500,
    boxShadow: 1,
  };

  const [formData, setFormData] = useState<CreateFormData>({
    name: "",
    description: "",
    prompt: "",
  });
  const { createApplication, createAppResponse } = useApplications();

  const { isMutating } = createAppResponse;
  const { data } = createAppResponse;
  const [isFirstTime, setIsFirstTime] = useState(true);
  const [showSuccess, setShowSuccess] = useState(false);

  const handleInputChange = (name: keyof CreateFormData, value: string) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const handleSubmitForm = (e: FormEvent) => {
    e.preventDefault();
    createApplication(formData, () => {
      setShowSuccess(true);
    });
    setIsFirstTime(false);
  };

  return (
    <form onSubmit={handleSubmitForm}>
      <Grid container rowSpacing={3}>
        <Grid item xs={12}>
          <Typography
            variant="h2"
            component="h2"
            align="center"
            color="text.secondary"
          >
            CREATE APPLICATION
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <CustomInput
            disabled={isMutating}
            type="text"
            onChange={(value) => handleInputChange("name", value)}
            label="Name"
            placeHolder={"e.g. Tic tac toe"}
          />
        </Grid>
        <Grid item xs={12}>
          <CustomInput
            disabled={isMutating}
            type="text"
            onChange={(value) => handleInputChange("description", value)}
            label="Description"
            placeHolder={"e.g. A tic tac toe game for two players"}
          />
        </Grid>
        <Grid item xs={12}>
          <CustomInput
            disabled={isMutating}
            type="text"
            onChange={(value) => handleInputChange("prompt", value)}
            label="Requirements"
            multiline={true}
            minRows={4}
            placeHolder={"e.g. Create a 3x3 tic tac toe game for two players. Count score and declare winner."}
          />
        </Grid>

        {isMutating && (
          <>
            <Grid
              item
              xs={12}
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {isMutating && <LoadingMessage />}
            </Grid>
            <Grid item xs={12} sx={{ width: "100%" }}>
              <LinearProgress
                color={"secondary"}
                data-testid="linear-progress"
              />
            </Grid>
          </>
        )}

        <Grid item xs={12}>
          <Grid
            container
            spacing={2}
            direction="row"
            justifyContent="space-around"
            alignItems="center"
          >
            {!isMutating && (
              <Grid item>
                <Button
                  sx={buttonStyle}
                  color="info"
                  variant="outlined"
                  type="submit"
                  startIcon={
                    !isMutating &&
                    (isFirstTime ? (
                      <PolylineOutlinedIcon />
                    ) : (
                      <ReplayOutlinedIcon />
                    ))
                  }
                >
                  {isFirstTime ? "Create" : "Regenerate"}
                </Button>
              </Grid>
            )}

            {data && !isMutating && (
              <Grid item>
                <Button
                  sx={buttonStyle}
                  color="success"
                  variant="outlined"
                  startIcon={<PlayArrowOutlinedIcon />}
                  onClick={() => Run(data.html, data.css, data.javaScript)}
                >
                  RUN
                </Button>
              </Grid>
            )}
          </Grid>
        </Grid>
        <SimpleSnackbar
          message="Your app is created!"
          data-testid="snackbar"
          isOpen={showSuccess}
          onClose={() => setShowSuccess(false)}
        />
      </Grid>
    </form>
  );
}
