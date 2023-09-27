import * as React from "react";
import { fireEvent, render } from "@testing-library/react";
import { SimpleSnackbar } from ".."; // Replace with the actual path to your file

describe("SimpleSnackbar handleClose method", () => {
  it("should call onClose when Snackbar closes after autoHideDuration", async () => {
    jest.useFakeTimers(); // Mock the timers to control autoHideDuration

    const onCloseMock = jest.fn();
    const message = "Test message";
    render(
        <SimpleSnackbar
            isError={false}
            message={message}
            isOpen={true}
            onClose={onCloseMock}
        />,
    );

    // Fast-forward time to autoHideDuration
    jest.advanceTimersByTime(3000);

    // Expect the onClose function to be called after autoHideDuration
    expect(onCloseMock).toHaveBeenCalled();
  });

  it("should not call onClose when clicking away from the Snackbar", () => {
    const onCloseMock = jest.fn();
    const message = "Test message";
    const { container } = render(
        <SimpleSnackbar
            isError={false}
            message={message}
            isOpen={true}
            onClose={onCloseMock}
        />,
    );

    // Simulate a click away from the Snackbar
    fireEvent.click(container);

    // The onClose function should not be called
    expect(onCloseMock).toHaveBeenCalled();
  });
});
