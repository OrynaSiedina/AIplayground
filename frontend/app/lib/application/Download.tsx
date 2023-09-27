export function downloadCode(
  name: string,
  html: string,
  css: string,
  script: string,
) {
  const code = html
    .replace("</head>", `<style>${css}</style></head>`)
    .replace("</body>", `<script>${script}</script></body>`);
  const blob = new Blob([code], { type: "text/html" });
  const blobUrl = URL.createObjectURL(blob);
  const downloadLink = document.createElement("a");
  downloadLink.href = blobUrl;
  downloadLink.download = name + ".html";
  downloadLink.click();
  URL.revokeObjectURL(blobUrl);
}
