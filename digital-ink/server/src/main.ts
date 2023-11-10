import { app, BrowserWindow, ipcMain } from "electron";
import { Charset, ZapServer } from "zap-lib";

const createWindow = () => {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true,
      nodeIntegrationInWorker: true,
      contextIsolation: false,
    },
  });

  win.loadFile("index.html");
}

app.whenReady().then(() => {
  createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});

ipcMain.on("zap-start", (e) => {
  (new class extends ZapServer {
    onTextReceived(_id: string, text: string, _charset: Charset) {
      e.sender.send("zap-text-data", text);
    }
  }).listen();
});
