import styles from "../styles/form.module.css";
import buttonStyles from "../styles/button.module.css";
import { Input } from "./input";
import { FormEvent, ReactNode, useState } from "react";
import { AuthorizationRequest, RegisterResponse, useAuth } from "@/app/lib";
import { SimpleSnackbar } from "@/app/components/SimpleSnackbar";

interface FormProps {
  id: "signUp" | "signIn";
}

export function Form({ id }: FormProps): ReactNode {
  const [formData, setFormData] = useState<AuthorizationRequest>({
    nickname: "",
    password: "",
    email: "",
  });
  const [isOpen, setIsOpen] = useState(false);

  const handleInputChange = (
    name: keyof AuthorizationRequest,
    value: string,
  ) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const { register, login } = useAuth<RegisterResponse>();
  const handleSubmitForm = (e: FormEvent) => {
    e.preventDefault();
    id !== "signUp"
      ? login(formData)
      : register(formData, () => setIsOpen(true));
  };

  return (
    <>
      <div className={styles.errorContainer} id="error-bar"></div>
      <form
        action="#"
        className={styles.form}
        id={id}
        onSubmit={handleSubmitForm}
      >
        <h2 className={styles.title}>
          {id === "signUp" ? "Create Account" : "Login"}
        </h2>

        {id === "signUp" && (
          <Input
            type="text"
            placeholder="User"
            className={styles.input}
            onChange={(value) => handleInputChange("nickname", value)}
          />
        )}

        <Input
          type="email"
          placeholder="Email"
          className={styles.input}
          onChange={(value) => handleInputChange("email", value)}
        />

        <Input
          id={id}
          type="password"
          placeholder="Password"
          className={styles.input}
          onChange={(value) => handleInputChange("password", value)}
        />

        {id === "signIn" && (
          <a href="#" className={styles.link}>
            Forgot your password?
          </a>
        )}

        <button className={`${buttonStyles.btn} ${buttonStyles.bottom}`}>
          {id === "signUp" ? "Sign up" : "Log in"}
        </button>
      </form>
      <SimpleSnackbar
        message="Verification email has been sent. Please check your email."
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
      />
    </>
  );
}
