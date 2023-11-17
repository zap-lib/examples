import { app, BrowserWindow } from "electron";
import robot from 'robotjs';
import { MetaInfo, ZapAccelerometer, ZapServer } from "zap-lib-js";

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

  const cursor = robot.getMousePos();
  (new class extends ZapServer {
    onAccelerometerReceived(_info: MetaInfo, data: ZapAccelerometer) {
      cursor.x -= data.x;
      cursor.y -= data.y;
      robot.moveMouse(cursor.x, cursor.y)
    }
  }).listen();
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});


