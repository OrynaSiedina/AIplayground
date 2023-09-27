import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import { ApplicationCard } from "../ApplicationCard";


jest.mock("../GetButtons", () => ({
    GetButtons: jest.fn(() => <div>Mocked GetButtons</div>),
}));


describe("Application component", () => {

  const mockApplicationData = {
    id: 1,
    name: "Test Application",
    description: "This is a test application.",
    html: "<div>Hello, world!</div>",
    css: "body { background-color: #f0f0f0; }",
    javaScript: 'console.log("Hello, world!");',
    owner: "user1@email.com",
    category: "Category1",
  };

  test("renders users application details correctly", () => {
    const mockProps = {
      appData: mockApplicationData,
      variant: "myApps",
    };

    render(<ApplicationCard {...mockProps} />);

    expect(screen.getByText(mockProps.appData.name)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.description)).toBeInTheDocument();

    // Expect the GetButtons component to be rendered as a placeholder
    expect(screen.getByText("Mocked GetButtons")).toBeInTheDocument();
  });

  test("renders used application details correctly", () => {
    Object.defineProperty(document, 'visibilityState', {
      value: 'hidden', // or 'hidden' depending on your use case
    });
    const mockProps = {
      appData: mockApplicationData,
      variant: "usedApps",
    };

    render(<ApplicationCard {...mockProps} />);

    expect(screen.getByText(mockProps.appData.name)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.description)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.owner)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.category)).toBeInTheDocument();
    expect(screen.getByText("Mocked GetButtons")).toBeInTheDocument();
  });

  test("renders appStore application details correctly", () => {
    Object.defineProperty(document, 'visibilityState', {
      value: 'hidden', // or 'hidden' depending on your use case
    });
    const mockProps = {
      appData: mockApplicationData,
      variant: "store",
    };

    render(<ApplicationCard {...mockProps} />);

    expect(screen.getByText(mockProps.appData.name)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.owner)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.category)).toBeInTheDocument();
    expect(screen.getByText("Mocked GetButtons")).toBeInTheDocument();
  });

  test("renders landing page application details correctly", () => {
    Object.defineProperty(document, 'visibilityState', {
      value: 'hidden', // or 'hidden' depending on your use case
    });

    const mockProps = {
      appData: mockApplicationData,
      variant: "page",
    };

    render(<ApplicationCard {...mockProps} />);

    expect(screen.getByText(mockProps.appData.name)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.description)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.owner)).toBeInTheDocument();
    expect(screen.getByText(mockProps.appData.category)).toBeInTheDocument();
    expect(screen.getByText("Mocked GetButtons")).toBeInTheDocument();
  });
});
