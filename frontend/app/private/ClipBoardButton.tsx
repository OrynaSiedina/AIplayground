import { Button, Grid } from "@mui/material";
import { useState } from "react";
import { SimpleSnackbar } from "@/app/components";

interface CopyToClipboardButtonProps {
  input: string;
  onClose: () => void;
  variant: "outlined";
  fullWidth?: true;
}

export function ClipBoardButton(props: CopyToClipboardButtonProps) {
  const [open, setOpen] = useState(false);
  const handleCopy = () => {
    setOpen(true);
    navigator.clipboard.writeText(props.input);
  };
  const texts: { text: string; onClick: () => void }[] = [
    {
      text: "Copy URL to clipboard",
      onClick: handleCopy,
    },
    {
      text: "Exit",
      onClick: props.onClose,
    },
  ];
  return (
    <>
      <Grid container columnSpacing={3} justifyContent="center">
        {texts.map((text) => (
          <Grid item key={text.text}>
            <Button
              key={text.text}
              variant={props.variant}
              fullWidth={props.fullWidth}
              onClick={text.onClick}
            >
              {text.text}
            </Button>
          </Grid>
        ))}{" "}
      </Grid>
      <SimpleSnackbar
        isError={false}
        message={"Copied to clipboard"}
        isOpen={open}
        onClose={() => setOpen(false)}
      />
    </>
  );
}
