import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import AppList from "../page";

jest.mock("@/app/lib/useApplications/useApplications", () => ({
  useApplications: jest.fn(() => ({
    getAllApps: jest.fn(() => ({
      data: [
        {
          id: 1,
          name: "Test Application1",
          description: "This is a 1st test application.",
          owner: "user1@email.com",
          category: "Category1",
        },
        {
          id: 2,
          name: "Test Application2",
          description: "This is a 2nd test application.",
          owner: "user2@email.com",
          category: "Category2",
        },
      ],
      isMutating: false,
    })),
  })),
}));

describe("AppList", () => {
  it("renders the MY APPS title", () => {
    render(<AppList />);
    const titleElement = screen.getByText("MY APPS");
    expect(titleElement).toBeInTheDocument();
  });

  it("renders the CREATED tab by default", () => {
    render(<AppList />);
    expect(screen.getByText("CREATED")).toBeInTheDocument();
  });

  it("renders the application data under CREATED tab", () => {
    render(<AppList />);

    expect(screen.getByText("Test Application1")).toBeInTheDocument();
    expect(screen.getByText("This is a 1st test application.")).toBeInTheDocument();
    expect(screen.getByText("Test Application2")).toBeInTheDocument();
    expect(screen.getByText("This is a 2nd test application.")).toBeInTheDocument();
  });
});
