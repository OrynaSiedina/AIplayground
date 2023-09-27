import { useState } from "react";
import { Button, Grid } from "@mui/material";
import PlayArrowOutlinedIcon from "@mui/icons-material/PlayArrowOutlined";
import FileDownloadOutlinedIcon from "@mui/icons-material/FileDownloadOutlined";
import ShareOutlinedIcon from "@mui/icons-material/ShareOutlined";
import AddCircleIcon from "@mui/icons-material/AddCircle";
import UpgradeOutlinedIcon from "@mui/icons-material/UpgradeOutlined";
import { ApplicationProps, SimpleSnackbar } from "@/app/components";
import { downloadCode, Run, useApplications, useUser } from "@/app/lib";
import { ShareModal } from "../private/ShareModal";
import DeleteOutlineOutlinedIcon from "@mui/icons-material/DeleteOutlineOutlined";

export const GetButtons = (props: ApplicationProps) => {
  const { addToUsedApplications } = useUser();
  const [openModal, setOpenModal] = useState(false);
  const { deleteApp } = useApplications();
  const [isDisabled, setDisabled] = useState(false);
  const [isOpened, setOpened] = useState(false);
  const handleAddToApplications = () => {
    addToUsedApplications({ id: props.appData.id }, () => {
      setDisabled(true);
      setOpened(true); // Open Snackbar

      // Close Snackbar after 3 seconds
      setTimeout(() => {
        setOpened(false);
      }, 3000);
    });
  };

  return (
    <>
      <Grid item>
        <Button
          color="success"
          fullWidth={true}
          variant="outlined"
          size="small"
          startIcon={<PlayArrowOutlinedIcon />}
          onClick={() =>
            Run(props.appData.html, props.appData.css, props.appData.javaScript)
          }
        >
          Run
        </Button>
      </Grid>
      {props.variant === "myApps" && (
        <Grid item>
          <Button
            color="info"
            fullWidth={true}
            variant="outlined"
            size="small"
            startIcon={<ShareOutlinedIcon />}
            onClick={() => setOpenModal(true)}
          >
            {props.appData.public ? "Link" : "Share"}
          </Button>
        </Grid>
      )}
      {openModal && (
        <ShareModal
          appData={props.appData}
          isOpen={openModal}
          onClose={() => setOpenModal(false)}
        />
      )}
      {(props.variant === "myApps" || props.variant === "usedApps") && (
        <Grid item>
          <Button
            fullWidth={true}
            color="info"
            variant="outlined"
            size="small"
            startIcon={<FileDownloadOutlinedIcon />}
            onClick={() =>
              downloadCode(
                props.appData.name,
                props.appData.html,
                props.appData.css,
                props.appData.javaScript,
              )
            }
          >
            Download
          </Button>
        </Grid>
      )}
      {(props.variant === "store" || props.variant === "page") && (
        <Grid item>
          <Button
            disabled={isDisabled}
            color="info"
            fullWidth={true}
            variant="outlined"
            size="small"
            startIcon={<AddCircleIcon />}
            onClick={handleAddToApplications}
          >
            Add to my applications
          </Button>
        </Grid>
      )}
      {props.variant === "myApps" && (
        <Grid item>
          <Button
            disabled
            color="warning"
            fullWidth={true}
            variant="outlined"
            size="small"
            startIcon={<UpgradeOutlinedIcon />}
          >
            UPDATE
          </Button>
        </Grid>
      )}
      {props.variant === "myApps" && (
        <Grid item>
          <Button
            color="warning"
            fullWidth={true}
            variant="outlined"
            size="small"
            onClick={() => deleteApp(props.appData.id, () => props.onClose?.())}
            startIcon={<DeleteOutlineOutlinedIcon />}
          >
            DELETE
          </Button>
        </Grid>
      )}
      <SimpleSnackbar
        message="Application was added in your applications"
        isOpen={isOpened}
      />
    </>
  );
};
