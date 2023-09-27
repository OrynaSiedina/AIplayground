import { Run } from "../Run.tsx";

describe("Run function", () => {
  let openSpy;

  beforeEach(() => {
    openSpy = jest.spyOn(window, "open").mockImplementation(() => null);
  });

  afterEach(() => {
    openSpy.mockRestore();
  });

  test("opens a new window and inserts iframe", () => {
    const iframe = document.createElement("iframe");
    iframe.sandbox = {
      add: jest.fn(),
    };
    const mockWindow = {
      document: {
        createElement: jest.fn(() => iframe),
        body: { appendChild: jest.fn() },
      },
    };

    openSpy.mockReturnValueOnce(mockWindow);
    const html = "<head></head><body></body>";
    const css = "body {background-color: red;}";
    const javascript = "console.log('test');";

    Run(html, css, javascript);

    expect(window.open).toHaveBeenCalled();

    expect(mockWindow.document.createElement).toHaveBeenCalledWith("iframe");
    expect(mockWindow.document.body.appendChild).toHaveBeenCalledWith(iframe);

    expect(iframe.srcdoc).toEqual(
      "<head><style>body {background-color: red;}</style></head><body><script>console.log('test');</script></body>",
    );

    expect(iframe.style.width).toEqual("95%");
    expect(iframe.style.height).toEqual("95%");
    expect(iframe.sandbox.add).toHaveBeenCalledTimes(1);
  });
});
