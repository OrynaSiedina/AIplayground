import React from "react";
import { fireEvent, render } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Form } from "../form";
import "swr/mutation";

const mockRegister = jest.fn()

jest.mock("next/navigation", () => ({
  useRouter: jest.fn(),
}));

jest.mock("@/app/lib/useAuth", () => ({
  useAuth: () => ({
    register: mockRegister,
    login: jest.fn(),
    logout: jest.fn(),
    isLoggedIn: false,
  }),
}));

describe("Form component", () => {
  test("should render form for sign up", () => {
    const { getByText, getByPlaceholderText } = render(<Form id="signUp" />);

    expect(getByText("Create Account")).toBeInTheDocument();

    expect(getByPlaceholderText("User")).toBeInTheDocument();
    expect(getByPlaceholderText("Email")).toBeInTheDocument();
    expect(getByPlaceholderText("Password")).toBeInTheDocument();

    expect(getByText("Sign up")).toBeInTheDocument();
  });

  test("updates form data on input change", () => {
    const { getByPlaceholderText } = render(<Form id="signUp" />);

    const userField = getByPlaceholderText("User");
    const emailField = getByPlaceholderText("Email");
    const passwordField = getByPlaceholderText("Password");

    fireEvent.change(userField, { target: { value: "JohnDoe" } });
    fireEvent.change(emailField, { target: { value: "john@example.com" } });
    fireEvent.change(passwordField, { target: { value: "password123" } });

    expect(userField.value).toBe("JohnDoe");
    expect(emailField.value).toBe("john@example.com");
    expect(passwordField.value).toBe("password123");
  });

  test("should render form for sign in", async () => {
    const { getByPlaceholderText } = render(<Form id="signIn" />);

    expect(getByPlaceholderText("Email")).toBeInTheDocument();
    expect(getByPlaceholderText("Password")).toBeInTheDocument();
  });

  test("should call register function on form submit", async () => {
    const { getByPlaceholderText, getByText } = render(<Form id="signUp" />);
    fireEvent.change(getByPlaceholderText("User"), {
      target: { value: "testUser" },
    });
    fireEvent.change(getByPlaceholderText("Email"), {
      target: { value: "test@example.com" },
    });
    fireEvent.change(getByPlaceholderText("Password"), {
      target: { value: "testPassword" },
    });

    fireEvent.click(getByText("Sign up"));

    expect(mockRegister).toHaveBeenCalled();
  });
});
