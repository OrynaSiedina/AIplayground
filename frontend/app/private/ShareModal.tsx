import { alpha, Button, Grid, Modal, Popover, Typography } from "@mui/material";
import { ClipBoardButton } from "./ClipBoardButton";
import * as React from "react";
import { useState } from "react";
import { useApplications, ApplicationData } from "@/app/lib";
import process from "process";
import { SimpleSelect } from "./SimpleSelect";
import { useTheme } from "@mui/material/styles";
import QRCode from "react-qr-code";

interface ShareModalProps {
  appData: ApplicationData;
  isOpen: boolean;
  onClose: () => void;
}

export function ShareModal({ appData, isOpen, onClose }: ShareModalProps) {
  const [share, setShare] = useState(false);
  const [category, setCategory] = useState("Uncategorized");
  const { update } = useApplications();
  const palette = useTheme().palette;

  function updateApp() {
    const updateData: ApplicationData = {
      ...appData,
      category: category,
      public: true,
    };
    update(updateData, () => {
      setShare(false);
    });
  }

  return (
    <Modal open={isOpen} aria-labelledby="modal-modal-title">
      <Popover
        sx={{ marginTop: 30 }}
        open={isOpen}
        anchorOrigin={{
          vertical: "top",
          horizontal: "center",
        }}
        transformOrigin={{
          vertical: "center",
          horizontal: "center",
        }}
      >
        <Grid
          container
          spacing={{ xs: 3 }}
          direction="column"
          rowSpacing={1}
          sx={{ p: 2, backgroundColor: alpha(palette.primary.light, 0.3) }}
        >
          <Grid item>
            {share ? (
              <>
                <SimpleSelect setCategory={setCategory} />
              </>
            ) : (
              <Typography id="modal-modal-title" variant="h5" component="h2">
                {appData.public ? (
                  <Grid
                    container
                    spacing={1}
                    direction="column"
                    justifyContent="center"
                    alignItems="center"
                  >
                    <Grid item>
                      <Typography color="primary" variant="body1">
                        {`${process.env.NEXT_PUBLIC_FE_URL}/private/store/app/${appData.id}`}
                      </Typography>
                    </Grid>
                    <Grid item>
                      <QRCode
                        value={`${process.env.NEXT_PUBLIC_FE_URL}/private/store/app/${appData.id}`}
                      />
                    </Grid>
                  </Grid>
                ) : (
                  <Typography color="primary" variant="h4">
                    MAKE APP PUBLIC?
                  </Typography>
                )}
              </Typography>
            )}
          </Grid>
          <Grid item xs={12}>
            {appData.public ? (
              <Grid item xs={12}>
                <ClipBoardButton
                  input={`${process.env.NEXT_PUBLIC_FE_URL}/private/store/app/${appData.id}`}
                  onClose={onClose}
                  variant="outlined"
                />
              </Grid>
            ) : (
              <Grid
                container
                columnSpacing={2}
                direction="row"
                justifyContent="center"
                alignItems="center"
              >
                <Grid item xs={6}>
                  <Button
                    variant="outlined"
                    fullWidth={true}
                    onClick={() => {
                      share ? updateApp() : setShare(true);
                    }}
                  >
                    {share ? "Share" : "Yes"}
                  </Button>
                </Grid>

                <Grid item xs={6}>
                  <Button
                    variant="outlined"
                    fullWidth={true}
                    onClick={() => {
                      setShare(false);
                      onClose();
                    }}
                  >
                    {share ? "Cancel" : "No"}
                  </Button>
                </Grid>
              </Grid>
            )}
          </Grid>
        </Grid>
      </Popover>
    </Modal>
  );
}
