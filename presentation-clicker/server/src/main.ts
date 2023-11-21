import { app, BrowserWindow } from "electron";
import robot from 'robotjs';
import { MetaInfo, ZapServer, ZapUiEvent } from "zap-lib-js";

const createWindow = () => {
  const win = new BrowserWindow({
    width: 300,
    height: 300,
    webPreferences: {
      nodeIntegration: true,
      nodeIntegrationInWorker: true,
      contextIsolation: false,
    },
  });

  win.loadFile("index.html");
}

app.whenReady().then(async () => {
  createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });

  (new class extends ZapServer {
    onUiEventReceived(_info: MetaInfo, data: ZapUiEvent) {
      switch (data.uiId) {
        case "next": robot.keyTap("right"); break;
        case "prev": robot.keyTap("left"); break;
      }
    }
  }).listen();
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});
