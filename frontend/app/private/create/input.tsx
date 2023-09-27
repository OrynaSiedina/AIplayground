import { ChangeEvent, useCallback, useState } from "react";
import { TextField, useTheme } from "@mui/material";

export interface InputFieldProps {
  type: "text";
  onChange: (value: string) => void;
  label: string;
  disabled?: boolean;
  sx?: Object;
  multiline?: boolean;
  minRows?: number;
  maxRows?: number;
}

export function Input({
  type,
  onChange,
  label,
  disabled,
  multiline,
  minRows,
  maxRows,
}: InputFieldProps) {
  const [value, setValue] = useState("");

  const handleInputChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const { value } = e.target;
      setValue(value);
      onChange(value);
    },
    [onChange],
  );

  return (
    <>
      <TextField
        sx={{
          boxShadow: `0px 2px 8px ${useTheme().palette.primary.light}`,
          borderRadius: "0.7rem",
        }}
        type={type}
        value={value}
        onChange={handleInputChange}
        color="secondary"
        variant="outlined"
        label={label}
        fullWidth
        disabled={disabled}
        minRows={minRows}
        rows={maxRows}
        multiline={multiline}
      />
    </>
  );
}
