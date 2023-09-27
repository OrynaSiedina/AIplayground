"use client";
import React from "react";
import {
  Box,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";
import { useStore } from "@/app/lib";

interface CategoryProps {
  id: number;
  name: string;
}

interface SimpleSelectProps {
  setCategory: (value: string) => void;
}

export function SimpleSelect({ setCategory }: SimpleSelectProps) {
  const { categories: data } = useStore();

  const handleChange = (event: SelectChangeEvent<{ value: unknown }>): void => {
    setCategory(event.target.value as string);
  };

  return (
    <Box sx={{ minWidth: "50vw", padding: "1rem 0" }}>
      <FormControl fullWidth variant="standard">
        <Typography>
          <InputLabel>SELECT CATEGORY</InputLabel>
        </Typography>
        <Select
          onChange={handleChange}
          inputProps={{ "aria-label": "Without label" }}
        >
          {data?.map((cat: CategoryProps) => (
            <MenuItem key={cat.id} value={cat.name}>
              {cat.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Box>
  );
}
