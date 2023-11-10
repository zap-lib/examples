import { ipcRenderer } from "electron";

(() => {
  const textField = document.querySelector("#text") as HTMLTextAreaElement;

  ipcRenderer.send("zap-start");
  ipcRenderer.on("zap-text-data", (_, text) => {
    textField.value = text;
  });
})();
