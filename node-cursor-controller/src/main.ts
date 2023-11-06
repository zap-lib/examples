import { app, BrowserWindow, ipcMain } from "electron";
import robot from 'robotjs';
import { ZapServer } from "zap-lib";

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

app.whenReady().then(() => {
  createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });

  const cursor = robot.getMousePos();
  const zap = new class extends ZapServer {
    onAccelerometerChanged(_: string, x: number, y: number) {
      cursor.x += -x;
      cursor.y += y;
      robot.moveMouse(cursor.x, cursor.y)
    }
  };

  zap.start();
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});


