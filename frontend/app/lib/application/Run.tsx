export function Run(html: String, css: String, javascript: String) {
  const content = html
    .replace("</head>", `<style>${css}</style></head>`)
    .replace("</body>", `<script>${javascript}</script></body>`);

  let newWindow: Window | null = window.open("", "_blank");
  if (newWindow) {
    let iFrame = newWindow.document.createElement("iframe");
    iFrame.srcdoc = content;
    iFrame.focus();
    iFrame.style.width = "95%";
    iFrame.style.height = "95%";
    iFrame.sandbox.add(
      "allow-scripts",
      "allow-popups",
      "allow-same-origin",
      "allow-modals",
    );
    newWindow.document.body.appendChild(iFrame);
  }
}
