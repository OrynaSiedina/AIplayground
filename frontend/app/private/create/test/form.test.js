import React from "react";
import { fireEvent, render } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Form } from "../form";

jest.mock("../../../lib", () => {
  const mockApiEndpoint = "http://mockapi.com/applications";
  return {
    useApplications: jest.fn().mockImplementation(() => ({
      createApplication: jest.fn(),
      createAppResponse: {
        isLoading: false,
        data: null,
        error: null,
      },
    })),
    API_ENDPOINTS: {
      APPLICATIONS: mockApiEndpoint,
    },
    axios: jest.fn(),
  };
});

test("Form renders correctly", () => {
  const { getByLabelText, getByRole } = render(<Form />);
  const nameInput = getByLabelText("Name");
  const descriptionInput = getByLabelText("Description");
  const promptInput = getByLabelText("Requirements");
  const submitButton = getByRole("button", { name: "Create" });

  expect(descriptionInput).toBeInTheDocument();
  expect(promptInput).toBeInTheDocument();
  expect(submitButton).toBeInTheDocument();
  expect(nameInput).toBeInTheDocument();
});

it("updates form data on input change", () => {
  const { getByLabelText } = render(<Form />);

  // Get the input fields
  const appName = getByLabelText("Name");
  const appDescription = getByLabelText("Description");
  const appPrompt = getByLabelText("Requirements");

  // Simulate input change events
  fireEvent.change(appName, { target: { value: "AppName" } });
  fireEvent.change(appDescription, { target: { value: "AppDescription" } });
  fireEvent.change(appPrompt, { target: { value: "AppPrompt" } });

  // Check if the form data is updated correctly
  expect(appName.value).toBe("AppName");
  expect(appDescription.value).toBe("AppDescription");
  expect(appPrompt.value).toBe("AppPrompt");
});
