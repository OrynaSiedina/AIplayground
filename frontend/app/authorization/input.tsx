import { ChangeEvent, useCallback, useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import styles from "../styles/form.module.css";

interface InputFieldProps {
  type: "password" | "email" | "text";
  placeholder: "Password" | "Email" | "User";
  className: string;
  onChange: (value: string) => void;
  id?: string;
}

export function Input({
  type,
  placeholder,
  className,
  onChange,
}: InputFieldProps) {
  const [isFocused, setIsFocused] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [value, setValue] = useState("");

  const handleInputChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const { value } = e.target;
      setValue(value);
      onChange(value);
    },
    [onChange],
  );
  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className={styles.inputContainer}>
      <input
        type={showPassword ? "text" : type}
        placeholder={isFocused ? "" : placeholder}
        className={className}
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        value={value}
        onChange={handleInputChange}
      />
      {type === "password" && (
        <button
          type="button"
          className={styles.icon}
          onClick={handleTogglePassword}
        >
          {showPassword ? <FaEyeSlash /> : <FaEye />}
        </button>
      )}
    </div>
  );
}
