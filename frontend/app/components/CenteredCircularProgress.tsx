import { CircularProgress } from "@mui/material";

export function CenteredCircularProgress() {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
      <CircularProgress color="secondary" size={100} />
    </div>
  );
}
