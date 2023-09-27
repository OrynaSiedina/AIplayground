"use client";
import { FormEvent, useState } from "react";
import {
  Button,
  Grid,
  IconButton,
  InputAdornment,
  Typography,
} from "@mui/material";
import { CustomInput, SimpleSnackbar } from "@/app/components";
import { useUser } from "@/app/lib";
import { FaEye, FaEyeSlash } from "react-icons/fa";

interface FormData {
  oldPassword: string;
  newPassword: string;
  newPasswordAgain: string;
}

export function ChangePassForm() {
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showNewPasswordAgain, setShowNewPasswordAgain] = useState(false);

  // Function to toggle password visibility
  const togglePasswordVisibility = (field: keyof FormData) => {
    if (field === "oldPassword") {
      setShowOldPassword((prev) => !prev);
    } else if (field === "newPassword") {
      setShowNewPassword((prev) => !prev);
    } else if (field === "newPasswordAgain") {
      setShowNewPasswordAgain((prev) => !prev);
    }
  };

  const [formData, setFormData] = useState<FormData>({
    oldPassword: "",
    newPassword: "",
    newPasswordAgain: "",
  });

  const { changePassword } = useUser();
  const isMatching = formData.newPassword === formData.newPasswordAgain;
  const [showSuccess, setShowSuccess] = useState(false);

  const handleInputChange = (name: keyof FormData, value: string) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    changePassword(formData, () => {
      setShowSuccess(true);
    });
  };

  const isFormEmpty = Object.values(formData).some((value) => value === "");
  const isButtonDisabled = isFormEmpty || !isMatching;

  return (
    <>
      <form onSubmit={handleSubmit}>
        <Grid container rowSpacing={3}>
          <Grid item xs={12}>
            <Typography
              variant="h2"
              component="h2"
              align="center"
              color="text.secondary"
            >
              CHANGE PASSWORD
            </Typography>
          </Grid>

          <Grid item xs={12}>
            <CustomInput
              type={showOldPassword ? "text" : "password"}
              onChange={(value) => handleInputChange("oldPassword", value)}
              label={"Old password"}
              InputProps={{
                endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                          onClick={() => togglePasswordVisibility("oldPassword")}
                          edge="end"
                      >
                        {showOldPassword ? <FaEyeSlash /> : <FaEye />}
                      </IconButton>
                    </InputAdornment>
                ),
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <CustomInput
              type={showNewPassword ? "text" : "password"}
              onChange={(value) => handleInputChange("newPassword", value)}
              label={"New password"}
              InputProps={{
                endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                          onClick={() => togglePasswordVisibility("newPassword")}
                          edge="end"
                      >
                        {showNewPassword ? <FaEyeSlash /> : <FaEye />}
                      </IconButton>
                    </InputAdornment>
                ),
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <CustomInput
              type={showNewPasswordAgain ? "text" : "password"}
              onChange={(value) => handleInputChange("newPasswordAgain", value)}
              label={"New password again"}
              color={isMatching ? "success" : "error"}
              InputProps={{
                endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                          onClick={() => togglePasswordVisibility("newPasswordAgain")}
                          edge="end"
                      >
                        {showNewPasswordAgain ? <FaEyeSlash /> : <FaEye />}
                      </IconButton>
                    </InputAdornment>
                ),
              }}
            />
          </Grid>

          <Grid item xs={12}>
            <Button
              color="info"
              variant="outlined"
              type="submit"
              disabled={isButtonDisabled}
            >
              Change password
            </Button>
          </Grid>

          <Grid item xs={12}>
            <SimpleSnackbar
              message={"Password changed"}
              isOpen={showSuccess}
              onClose={() => setShowSuccess(false)}
            />
          </Grid>
        </Grid>
      </form>
    </>
  );
}
